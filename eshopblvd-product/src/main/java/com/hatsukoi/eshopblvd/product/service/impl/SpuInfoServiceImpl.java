package com.hatsukoi.eshopblvd.product.service.impl;

import com.hatsukoi.eshopblvd.coupon.service.SkuFullReductionService;
import com.hatsukoi.eshopblvd.coupon.service.SpuBoundsService;
import com.hatsukoi.eshopblvd.product.dao.SpuInfoMapper;
import com.hatsukoi.eshopblvd.product.entity.*;
import com.hatsukoi.eshopblvd.product.service.*;
import com.hatsukoi.eshopblvd.product.vo.*;
import com.hatsukoi.eshopblvd.to.SkuReductionTO;
import com.hatsukoi.eshopblvd.to.SpuBoundTO;
import com.hatsukoi.eshopblvd.utils.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.http.HttpStatus;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author gaoweilin
 * @date 2022/03/28 Mon 3:14 AM
 */
@Slf4j
@Service
@org.apache.dubbo.config.annotation.Service
public class SpuInfoServiceImpl implements SpuInfoService {
    @Autowired
    SpuInfoMapper spuInfoMapper;

    @Autowired
    SpuInfoDescService spuInfoDescService;

    @Autowired
    SpuImagesService spuImagesService;

    @Autowired
    AttrService attrService;

    @Autowired
    ProductAttrValueService productAttrValueService;

    @Reference(check = false, interfaceName = "com.hatsukoi.eshopblvd.coupon.service.SpuBoundsService")
    SpuBoundsService spuBoundsService;

    @Autowired
    SkuInfoService skuInfoService;

    @Autowired
    SkuImagesService skuImagesService;

    @Autowired
    SkuSaleAttrValueService skuSaleAttrValueService;

    @Reference(check = false, interfaceName = "com.hatsukoi.eshopblvd.coupon.service.SkuFullReductionService")
    SkuFullReductionService skuFullReductionService;

    @Override
    @Transactional
    public void insertNewSpu(SpuInsertVO vo) {
        //【1】保存spu基本信息「pms_spu_info」
        SpuInfo spuInfo = new SpuInfo();
        BeanUtils.copyProperties(vo, spuInfo);
        spuInfo.setCreateTime(new Date());
        spuInfo.setUpdateTime(new Date());
        this.insertBaseSpuInfo(spuInfo);

        //【2】保存spu的描述图片「pms_spu_info_desc」
        List<String> decriptImgs = vo.getDecript();
        SpuInfoDesc spuInfoDesc = new SpuInfoDesc();
        // 上一步保存spu基本信息会生成spuId，默认自增
        spuInfoDesc.setSpuId(spuInfo.getId());
        // 多个商品描述图片的地址用逗号分割
        spuInfoDesc.setDecript(String.join(",", decriptImgs));
        spuInfoDescService.insertSpuInfoDesc(spuInfoDesc);

        //【3】保存spu的商品图集（sku用）「pms_spu_images」
        List<String> images = vo.getImages();
        spuImagesService.insertImages(spuInfo.getId(), images);

        //【4】保存spu的积分信息「sms_spu_bounds」
        Bounds bounds = new Bounds();
        SpuBoundTO spuBoundTO = new SpuBoundTO(); // 远程调用传递自定义DTO
        BeanUtils.copyProperties(bounds, spuBoundTO);
        spuBoundTO.setSpuId(spuInfo.getId());
        CommonResponse response = spuBoundsService.insertSpuBounds(spuBoundTO);
        if (response.getCode() != HttpStatus.SC_OK) {
            log.error("远程调用coupon服务插入spu积分信息失败::spuBoundsService.insertSpuBounds()");
        }

        //【5】保存spu的规格参数「pms_product_attr_value」
        List<BaseAttr> baseAttrs = vo.getBaseAttrs();
        List<ProductAttrValue> collect = baseAttrs.stream().map((baseAttr) -> {
            ProductAttrValue productAttrValue = new ProductAttrValue();
            Attr attr = attrService.getAttrById(baseAttr.getAttrId());
            productAttrValue.setAttrId(attr.getAttrId());
            productAttrValue.setAttrName(attr.getAttrName());
            // pms_attr存的是属性可选值，pms_product_attr_value存的是某个spu存的属性具体值
            productAttrValue.setAttrValue(baseAttr.getAttrValues());
            productAttrValue.setSpuId(spuInfo.getId());
            productAttrValue.setQuickShow((byte) baseAttr.getShowDesc());
            return productAttrValue;
        }).collect(Collectors.toList());
        productAttrValueService.insertProductAttrValue(collect);

        //【6】保存当前spu对应的所有sku的信息

        List<Sku> skus = vo.getSkus();
        if (skus != null && skus.size() > 0) {
            skus.forEach(sku -> {
                // 6.1 插入sku的基本信息「pms_sku_info」
                SkuInfo skuInfo = new SkuInfo();
                skuInfo.setSpuId(spuInfo.getId());
                skuInfo.setSkuName(sku.getSkuName());
                skuInfo.setSkuDesc(sku.getDescar().toString());
                skuInfo.setCatalogId(spuInfo.getCatalogId());
                skuInfo.setBrandId(spuInfo.getBrandId());
                String defaultImg = "";
                for (Image img: sku.getImages()) {
                    if (img.getDefaultImg() == 1) {
                        defaultImg = img.getImgUrl();
                    }
                }
                skuInfo.setSkuDefaultImg(defaultImg);
                skuInfo.setSkuTitle(sku.getSkuTitle());
                skuInfo.setSkuSubtitle(sku.getSkuSubtitle());
                skuInfo.setPrice(sku.getPrice());
                skuInfo.setSaleCount(0L);
                skuInfoService.insertSkuInfo(skuInfo);

                // 6.2 插入sku的图片信息「pms_sku_image」
                List<SkuImages> skuImages = sku.getImages().stream().map(image -> {
                    SkuImages skuImage = new SkuImages();
                    skuImage.setSkuId(skuInfo.getSkuId());
                    skuImage.setDefaultImg(image.getDefaultImg());
                    skuImage.setImgUrl(image.getImgUrl());
                    return skuImage;
                }).filter(image -> {
                    // 过滤掉imgUrl为空的情况
                    return !StringUtils.isEmpty(image.getImgUrl());
                }).collect(Collectors.toList());
                skuImagesService.batchInsert(skuImages);

                // 6.3 插入sku的销售属性信息「pms_sku_sale_attr_value」
                List<SkuSaleAttrValue> skuSaleAttrValues = sku.getAttr().stream().map(saleAttr -> {
                    SkuSaleAttrValue skuSaleAttrValue = new SkuSaleAttrValue();
                    BeanUtils.copyProperties(saleAttr, skuSaleAttrValue);
                    skuSaleAttrValue.setSkuId(skuInfo.getSkuId());
                    return skuSaleAttrValue;
                }).collect(Collectors.toList());
                skuSaleAttrValueService.batchInsert(skuSaleAttrValues);

                // 6.4 插入sku的满减、满折、会员价信息「sms_sku_full_reduction」「sms_sku_ladder」「sms_member_price」
                SkuReductionTO skuReductionTO = new SkuReductionTO();
                BeanUtils.copyProperties(sku, skuReductionTO);
                skuReductionTO.setSkuId(skuInfo.getSkuId());
                if (skuReductionTO.getFullCount() > 0 || skuReductionTO.getFullPrice().compareTo(new BigDecimal("0")) == 1) {
                    CommonResponse resp = skuFullReductionService.insertSkuReduction(skuReductionTO);
                    if (resp.getCode() != HttpStatus.SC_OK) {
                        log.error("远程调用coupon服务插入sku优惠信息失败::skuFullReductionService.insertSkuReduction(skuReductionTO)");
                    }
                }
            });
        }
    }

    @Override
    public void insertBaseSpuInfo(SpuInfo spuInfo) {
        spuInfoMapper.insert(spuInfo);
    }
}
