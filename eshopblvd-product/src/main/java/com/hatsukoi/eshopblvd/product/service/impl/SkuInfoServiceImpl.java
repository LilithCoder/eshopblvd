package com.hatsukoi.eshopblvd.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.github.pagehelper.PageHelper;
import com.hatsukoi.eshopblvd.api.seckill.SeckillRpcService;
import com.hatsukoi.eshopblvd.product.dao.SkuInfoMapper;
import com.hatsukoi.eshopblvd.product.entity.*;
import com.hatsukoi.eshopblvd.product.service.*;
import com.hatsukoi.eshopblvd.product.vo.SkuItemVO;
import com.hatsukoi.eshopblvd.to.SeckillSkuRedisTo;
import com.hatsukoi.eshopblvd.utils.CommonPageInfo;
import com.hatsukoi.eshopblvd.utils.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.http.HttpStatus;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * @author gaoweilin
 * @date 2022/03/29 Tue 1:02 AM
 */
@Slf4j
@Service
public class SkuInfoServiceImpl implements SkuInfoService {
    @Autowired
    private SkuInfoMapper skuInfoMapper;

    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

    @Autowired
    private SkuImagesService skuImagesService;

    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    private SpuInfoDescService spuInfoDescService;

    @Autowired
    private AttrGroupService attrGroupService;

    @Reference(interfaceName = "com.hatsukoi.eshopblvd.api.seckill.SeckillRpcService", check = false)
    private SeckillRpcService seckillRpcService;

    @Override
    public void insertSkuInfo(SkuInfo skuInfo) {
        skuInfoMapper.insert(skuInfo);
    }

    /**
     * {
     * page: 1,//当前页码
     * limit: 10,//每页记录数
     * sidx: 'id',//排序字段
     * order: 'asc/desc',//排序方式
     * key: '华为',//检索关键字
     * catelogId: 0,
     * brandId: 0,
     * min: 0,
     * max: 0
     * }
     * @param params
     * @return
     */
    @Override
    public CommonPageInfo<SkuInfo> querySkuPageByFilters(Map<String, Object> params) {
        // 分页参数
        int pageNum = 1;
        int pageSize = 10;
        // 模糊搜索关键词
        String keyword = "";
        if (params.get("page") != null) {
            pageNum = Integer.parseInt(params.get("page").toString());
        }
        if (params.get("limit") != null) {
            pageSize = Integer.parseInt(params.get("limit").toString());
        }
        if (params.get("key") != null) {
            keyword = params.get("key").toString();
        }
        PageHelper.startPage(pageNum, pageSize);
        SkuInfoExample skuInfoExample = new SkuInfoExample();
        SkuInfoExample.Criteria criteria = skuInfoExample.createCriteria();

        // 关键词检索
        if (!StringUtils.isEmpty(keyword)) {
            criteria.andKeywordFilter(keyword);
        }

        // 分类id检索
        String catelogId = params.get("catelogId").toString();
        if (!StringUtils.isEmpty(catelogId) && !catelogId.equalsIgnoreCase("0")) {
            criteria.andCatalogIdEqualTo(Long.parseLong(catelogId));
        }

        // 品牌id检索
        String brandId = params.get("brandId").toString();
        if (!StringUtils.isEmpty(brandId) && !brandId.equalsIgnoreCase("0")) {
            criteria.andBrandIdEqualTo(Long.parseLong(brandId));
        }

        // 价格区间检索
        String min = params.get("min").toString();
        if (!StringUtils.isEmpty(min)) {
            criteria.andPriceGreaterThanOrEqualTo(new BigDecimal(min));
        }
        String max = params.get("max").toString();
        if (!StringUtils.isEmpty(max)) {
            BigDecimal maxValue = new BigDecimal(max);
            // max值只有大于0时候才生效加入筛选条件，不设置的话默认为0
            if (maxValue.compareTo(new BigDecimal("0")) == 1) {
                criteria.andPriceLessThanOrEqualTo(new BigDecimal(max));
            }
        }
        List<SkuInfo> skuInfos = skuInfoMapper.selectByExample(skuInfoExample);
        return CommonPageInfo.convertToCommonPage(skuInfos);
    }

    /**
     * 根据spu查出所有sku
     * @param spuId
     * @return
     */
    @Override
    public List<SkuInfo> getSkusBySpuId(Long spuId) {
        SkuInfoExample skuInfoExample = new SkuInfoExample();
        SkuInfoExample.Criteria criteria = skuInfoExample.createCriteria();
        criteria.andSpuIdEqualTo(spuId);
        List<SkuInfo> skuInfos = skuInfoMapper.selectByExample(skuInfoExample);
        return skuInfos;
    }

    /**
     * 获取指定sku详情页的信息
     * @param skuId
     * @return
     */
    @Override
    public SkuItemVO getSkuDetail(Long skuId) {
        // 各种查数据库，然后封装数据
        SkuItemVO skuItemVO = new SkuItemVO();

        // 1. 获取sku的基本信息（查到sku信息返回给后续链式调用入参用）
        CompletableFuture<SkuInfo> skuInfoCompletableFuture = CompletableFuture.supplyAsync(() -> {
            SkuInfo skuInfo = skuInfoMapper.selectByPrimaryKey(skuId);
            skuItemVO.setSkuInfo(skuInfo);
            return skuInfo;
        }, threadPoolExecutor);

        // 2. 获取sku的图片信息「pms_sku_images」
        CompletableFuture<Void> skuImagesCompletableFuture = CompletableFuture.runAsync(() -> {
            List<SkuImages> images = skuImagesService.getSkuImgsBySkuId(skuId);
            skuItemVO.setImages(images);
        }, threadPoolExecutor);

        // ----------------下面是需要依赖的skuInfo查询结果的异步操作----------------

        // 3. 获取到spuId后去获取所有销售属性
        CompletableFuture<Void> saleAttrsCompletableFuture = skuInfoCompletableFuture.thenAcceptAsync((res) -> {
            List<SpuSaleAttrPO> saleAttrs = skuSaleAttrValueService.getSaleAttrsBySpuId(res.getSpuId());
            List<SkuItemVO.SpuSaleAttrVO> collect = saleAttrs.stream().map(spuSaleAttrPO -> {
                SkuItemVO.SpuSaleAttrVO spuSaleAttrVO = new SkuItemVO.SpuSaleAttrVO();
                BeanUtils.copyProperties(spuSaleAttrPO, spuSaleAttrVO);
                List<AttrValueWithSkuIdsPO> attrValues = spuSaleAttrPO.getAttrValues();
                List<SkuItemVO.AttrValueWithSkuIdsVO> attrValuesWithSkuIdVO = attrValues.stream().map(attrValue -> {
                    SkuItemVO.AttrValueWithSkuIdsVO attrValueWithSkuIdsVO = new SkuItemVO.AttrValueWithSkuIdsVO();
                    BeanUtils.copyProperties(attrValue, attrValueWithSkuIdsVO);
                    List<Long> skuIds = Arrays.stream(attrValue.getSkuIds().split(",")).map(skuIdStr -> Long.parseLong(skuIdStr)).collect(Collectors.toList());
                    attrValueWithSkuIdsVO.setSkuIds(skuIds);
                    return attrValueWithSkuIdsVO;
                }).collect(Collectors.toList());
                spuSaleAttrVO.setAttrValues(attrValuesWithSkuIdVO);
                return spuSaleAttrVO;
            }).collect(Collectors.toList());
            log.info("销售属性：" + collect.toString());
            skuItemVO.setSaleAttrs(collect);
        }, threadPoolExecutor);

        // 4. 获取到spuId后去获取对应的商品描述图片
        CompletableFuture<Void> spuDescCompletableFuture = skuInfoCompletableFuture.thenAcceptAsync((res) -> {
            SpuInfoDesc spuInfoDesc = spuInfoDescService.getSpuInfoDescById(res.getSpuId());
            skuItemVO.setSpuInfoDesc(spuInfoDesc);
        }, threadPoolExecutor);

        // 5. 获取到spuId和对应分类后去获取规格参数
        CompletableFuture<Void> baseAttrsCompletableFuture = skuInfoCompletableFuture.thenAcceptAsync((res) -> {
            List<SkuItemVO.SpuItemAttrGroupVO> spuItemAttrGroupVOs = attrGroupService.getAttrGroupWithAttrsBySpuId(res.getSpuId(), res.getCatalogId());
            skuItemVO.setGroupAttrs(spuItemAttrGroupVOs);
        }, threadPoolExecutor);

        // 6. 查询当前sku是否参与秒杀优惠
        CompletableFuture<Void> secKillFuture = CompletableFuture.runAsync(() -> {
            CommonResponse resp = CommonResponse.convertToResp(seckillRpcService.getSkuSeckillInfo(skuId));
            if (resp.getCode() == HttpStatus.SC_OK) {
                SeckillSkuRedisTo data = resp.getData(new TypeReference<SeckillSkuRedisTo>() {
                });
                skuItemVO.setSeckillInfo(data);
            }
        }, threadPoolExecutor);

        // 6. 等到所有异步任务都完成
        try {
            CompletableFuture.allOf(skuInfoCompletableFuture,
                    skuImagesCompletableFuture,
                    saleAttrsCompletableFuture,
                    spuDescCompletableFuture,
                    baseAttrsCompletableFuture,
                    secKillFuture).get();
        } catch (Exception e) {
            log.error("获取商品sku详情信息失败!");
            e.printStackTrace();
        }

        return skuItemVO;
    }
}








































