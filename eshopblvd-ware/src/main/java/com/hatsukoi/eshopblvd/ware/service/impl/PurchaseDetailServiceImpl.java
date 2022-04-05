package com.hatsukoi.eshopblvd.ware.service.impl;

import com.github.pagehelper.PageHelper;
import com.hatsukoi.eshopblvd.utils.CommonPageInfo;
import com.hatsukoi.eshopblvd.ware.dao.PurchaseDetailMapper;
import com.hatsukoi.eshopblvd.ware.entity.PurchaseDetail;
import com.hatsukoi.eshopblvd.ware.entity.PurchaseDetailExample;
import com.hatsukoi.eshopblvd.ware.service.PurchaseDetailService;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class PurchaseDetailServiceImpl implements PurchaseDetailService {
    @Autowired
    private PurchaseDetailMapper purchaseDetailMapper;

    /**
     * 检索采购需求
     * 检索条件：
     * 1. 关键词匹配采购单id或采购商品id
     * 2. 匹配采购需求状态和仓库id
     * @param params
         * {
         *      page: 1,//当前页码
         *      limit: 10,//每页记录数
         *      key: '华为',//检索关键字
         *      status: 0,//状态
         *      wareId: 1,//仓库id
         * }
     * @return
     */
    @Override
    public CommonPageInfo<PurchaseDetail> queryPage(Map<String, Object> params) {
        // 分页参数
        int pageNum = 1;
        int pageSize = 10;
        String pageNumStr = params.get("page").toString();
        if (!StringUtils.isEmpty(pageNumStr)) {
            pageNum = Integer.parseInt(pageNumStr);
        }
        String pageSizeStr = params.get("limit").toString();
        if (!StringUtils.isEmpty(pageSizeStr)) {
            pageSize = Integer.parseInt(pageSizeStr);
        }

        PageHelper.startPage(pageNum, pageSize);

        // select * from wms_purchase_detail
        // where
        // (purchase_id=#{key} or sku_id=#{key}) and
        // (status=#{status}) and
        // (ware_id=#{wareId})
        PurchaseDetailExample purchaseDetailExample = new PurchaseDetailExample();
        PurchaseDetailExample.Criteria criteria = purchaseDetailExample.createCriteria();

        // 关键词匹配采购单id或采购商品id
        String key = params.get("key").toString();
        if (!StringUtils.isEmpty(key)) {
            criteria.andKeyFilter(key);
        }

        // 匹配采购需求状态
        String status = params.get("status").toString();
        if (!StringUtils.isEmpty(status) && StringUtils.isNumeric(status)) {
            criteria.andStatusEqualTo(Integer.parseInt(status));
        }

        // 匹配采购需求的仓库id
        String wareId = params.get("wareId").toString();
        if (!StringUtils.isEmpty(wareId) && StringUtils.isNumeric(wareId)) {
            criteria.andWareIdEqualTo(Long.parseLong(wareId));
        }

        List<PurchaseDetail> purchaseDetails = purchaseDetailMapper.selectByExample(purchaseDetailExample);

        return CommonPageInfo.convertToCommonPage(purchaseDetails);
    }

    @Override
    public void batchUpdate(List<PurchaseDetail> collect) {
        purchaseDetailMapper.batchUpdateSelective(collect);
    }
}
