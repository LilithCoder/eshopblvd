package com.hatsukoi.eshopblvd.ware.service.impl;

import com.hatsukoi.eshopblvd.constant.WareConstant;
import com.hatsukoi.eshopblvd.utils.CommonPageInfo;
import com.hatsukoi.eshopblvd.ware.dao.PurchaseMapper;
import com.hatsukoi.eshopblvd.ware.entity.Purchase;
import com.hatsukoi.eshopblvd.ware.entity.PurchaseDetail;
import com.hatsukoi.eshopblvd.ware.entity.PurchaseExample;
import com.hatsukoi.eshopblvd.ware.service.PurchaseDetailService;
import com.hatsukoi.eshopblvd.ware.service.PurchaseService;
import com.hatsukoi.eshopblvd.ware.service.WareSkuService;
import com.hatsukoi.eshopblvd.ware.vo.MergeVO;
import com.hatsukoi.eshopblvd.ware.vo.PurchaseDoneVo;
import com.hatsukoi.eshopblvd.ware.vo.PurchaseItemDoneVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author gaoweilin
 * @date 2022/04/05 Tue 2:17 AM
 */
@Service
public class PurchaseServiceImpl implements PurchaseService {
    @Autowired
    private PurchaseMapper purchaseMapper;

    @Autowired
    private PurchaseDetailService purchaseDetailService;

    @Autowired
    private WareSkuService wareSkuService;

    /**
     * 合并采购需求
     * 将采购需求合并到指定的采购单，如果没有指定采购单，则新建一个并合并其中
     * 1. 新建或用原来的采购单，并更新采购单的更新时间
     * 2. 批量更新采购需求的状态和采购单id，表明已经被合并到某采购单
     * @param mergeVO
     * {
     *   purchaseId: 1, // 目标合并的采购单id
     *   items:[1,2,3,4] // 需要合并的采购需求
     * }
     * @return
     */
    @Override
    @Transactional
    public void mergePurchaseItems(MergeVO mergeVO) {
        Long purchaseId = mergeVO.getPurchaseId();

        // 确认采购单状态是0,1才可以合并
        if (purchaseId != null) {
            Purchase purchase = purchaseMapper.selectByPrimaryKey(purchaseId);
            if (purchase.getStatus() == WareConstant.PurchaseStatusEnum.CREATED.getCode() ||
                purchase.getStatus() == WareConstant.PurchaseStatusEnum.RECEIVE.getCode()
            ) {
                // 更新采购单的更新时间
                Purchase newPurchase = new Purchase();
                newPurchase.setId(purchaseId);
                newPurchase.setUpdateTime(new Date());
                purchaseMapper.updateByPrimaryKeySelective(newPurchase);
            } else {
                return;
            }
        }

        // 获取采购单id，如果mergeVO指定了就用，没有的话就新建采购单获取采购单id
        if (purchaseId == null) {
            // 新建一个采购单
            Purchase purchase = new Purchase();
            purchase.setStatus(WareConstant.PurchaseStatusEnum.CREATED.getCode());
            purchase.setCreateTime(new Date());
            purchase.setUpdateTime(new Date());
            purchaseMapper.insertSelective(purchase);
            purchaseId = purchase.getId();
        }

        // 批量更新采购需求的状态表明已经被合并到某采购单
        List<Long> items = mergeVO.getItems();
        Long finalPurchaseId = purchaseId;
        List<PurchaseDetail> collect = items.stream().map(purchaseDetailId -> {
            PurchaseDetail purchaseDetail = new PurchaseDetail();
            purchaseDetail.setId(purchaseDetailId);
            purchaseDetail.setPurchaseId(finalPurchaseId);
            purchaseDetail.setStatus(WareConstant.PurchaseDetailStatusEnum.ASSIGNED.getCode());
            return purchaseDetail;
        }).collect(Collectors.toList());
        purchaseDetailService.batchUpdate(collect);
    }

    @Override
    public CommonPageInfo<Purchase> queryUnreceivePurchases(Map<String, Object> params) {
        int pageNum = 1;
        int pageSize = 10;
        String pageNumStr = params.get("page").toString();
        if (!StringUtils.isEmpty(pageNumStr) && StringUtils.isNumeric(pageNumStr)) {
            pageNum = Integer.parseInt(pageNumStr);
        }
        String pageSizeStr = params.get("limit").toString();
        if (!StringUtils.isEmpty(pageSizeStr) && StringUtils.isNumeric(pageSizeStr)) {
            pageSize = Integer.parseInt(pageSizeStr);
        }
        // select * from wms_purchase
        // where
        // status = 0 or
        // status = 1
        PurchaseExample purchaseExample = new PurchaseExample();
        PurchaseExample.Criteria criteria = purchaseExample.createCriteria();
        criteria.andStatusEqualTo(WareConstant.PurchaseStatusEnum.CREATED.getCode());
        PurchaseExample.Criteria criteria1 = purchaseExample.createCriteria();
        criteria1.andStatusEqualTo(WareConstant.PurchaseStatusEnum.ASSIGNED.getCode());
        purchaseExample.or(criteria1);

        List<Purchase> purchases = purchaseMapper.selectByExample(purchaseExample);

        return CommonPageInfo.convertToCommonPage(purchases);
    }

    @Override
    @Transactional
    public void received(List<Long> ids) {
        // 1. 改变新建or已分配的采购单为已领取
        PurchaseExample purchaseExample = new PurchaseExample();
        PurchaseExample.Criteria criteria = purchaseExample.createCriteria();
        criteria.andIdIn(ids);
        List<Purchase> purchases = purchaseMapper.selectByExample(purchaseExample);
        List<Purchase> collect = purchases.stream().filter(purchase -> {
            if (purchase.getStatus() == WareConstant.PurchaseStatusEnum.CREATED.getCode() ||
                    purchase.getStatus() == WareConstant.PurchaseStatusEnum.ASSIGNED.getCode()
            ) {
                return true;
            } else {
                return false;
            }
        }).map(purchase -> {
            purchase.setStatus(WareConstant.PurchaseStatusEnum.RECEIVE.getCode());
            purchase.setUpdateTime(new Date());
            return purchase;
        }).collect(Collectors.toList());
        // 2. 批量更新采购单
        purchaseMapper.batchUpdate(collect);
        // 3. 改变这些采购单内相关采购需求的状态
        List<Long> purchaseIds = collect.stream().map(purchase -> {
            return purchase.getId();
        }).collect(Collectors.toList());
        List<PurchaseDetail> list = purchaseDetailService.selectPurchaseDetailsByIds(purchaseIds);
        List<PurchaseDetail> collect1 = list.stream().map(purchaseDetail -> {
            purchaseDetail.setStatus(WareConstant.PurchaseDetailStatusEnum.BUYING.getCode());
            return purchaseDetail;
        }).collect(Collectors.toList());
        // 4. 批量更新采购需求
        purchaseDetailService.batchUpdate(collect1);
    }

    @Override
    @Transactional
    public void done(PurchaseDoneVo doneVo) {
//        {
//            id: 123,//采购单id
//            items: [{itemId:1,status:4,reason:""}]//完成/失败的需求详情
//        }
        // 1. 变更订购需求的状态：如果是已完成，那就入库且变更状态，如果有一个采购失败，那就保持采购失败状态且标记这个采购单的状态为有异常
        Long purchaseId = doneVo.getId();
        List<PurchaseItemDoneVo> items = doneVo.getItems();
        Boolean flag = true;
        List<PurchaseDetail> purchaseDetails = new ArrayList<>();
        for (PurchaseItemDoneVo item: items) {
            PurchaseDetail purchaseDetail = new PurchaseDetail();
            if (item.getStatus() == WareConstant.PurchaseDetailStatusEnum.HASERROR.getCode()) {
                flag = false;
            } else {
                purchaseDetail.setStatus(WareConstant.PurchaseDetailStatusEnum.FINISH.getCode());
                // 将成功采购的需求进行入库
                PurchaseDetail purchaseDetail1 = purchaseDetailService.getPurchaseDetailById(item.getItemId());
                wareSkuService.addStock(purchaseDetail1.getSkuId(), purchaseDetail1.getWareId(), purchaseDetail1.getSkuNum());
            }
            purchaseDetail.setId(item.getItemId());
            purchaseDetails.add(purchaseDetail);
        }
        purchaseDetailService.batchUpdate(purchaseDetails);

        // 2. 根据标记位来变更订购单的状态
        Purchase purchase = new Purchase();
        purchase.setId(purchaseId);
        purchase.setStatus(flag ? WareConstant.PurchaseStatusEnum.FINISH.getCode() : WareConstant.PurchaseStatusEnum.HASERROR.getCode());
        purchase.setUpdateTime(new Date());

        purchaseMapper.updateByPrimaryKey(purchase);
    }
}
