package com.hatsukoi.eshopblvd.ware.service;

import com.hatsukoi.eshopblvd.utils.CommonPageInfo;
import com.hatsukoi.eshopblvd.ware.entity.Purchase;
import com.hatsukoi.eshopblvd.ware.vo.MergeVO;

import java.util.List;
import java.util.Map;

/**
 * @author gaoweilin
 * @date 2022/04/05 Tue 2:17 AM
 */
public interface PurchaseService {
    void mergePurchaseItems(MergeVO mergeVO);

    CommonPageInfo<Purchase> queryUnreceivePurchases(Map<String, Object> params);

    void received(List<Long> ids);
}
