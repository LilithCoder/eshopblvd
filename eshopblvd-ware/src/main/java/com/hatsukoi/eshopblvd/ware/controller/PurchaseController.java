package com.hatsukoi.eshopblvd.ware.controller;

import com.hatsukoi.eshopblvd.utils.CommonResponse;
import com.hatsukoi.eshopblvd.ware.service.PurchaseService;
import com.hatsukoi.eshopblvd.ware.vo.MergeVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 采购单Controller
 * @author gaoweilin
 * @date 2022/04/05 Tue 2:11 AM
 */
@RestController
@RequestMapping("ware/purchase")
public class PurchaseController {
    @Autowired
    private PurchaseService purchaseService;

    /**
     * 合并采购需求
     * @param mergeVO
     * {
     *   purchaseId: 1, // 目标合并的采购单id
     *   items:[1,2,3,4] // 需要合并的采购需求
     * }
     * @return
     */
    @PostMapping("/merge")
    public CommonResponse merge(@RequestBody MergeVO mergeVO) {
        purchaseService.mergePurchaseItems(mergeVO);
        return CommonResponse.success();
    }
}
