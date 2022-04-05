package com.hatsukoi.eshopblvd.ware.controller;

import com.hatsukoi.eshopblvd.utils.CommonPageInfo;
import com.hatsukoi.eshopblvd.utils.CommonResponse;
import com.hatsukoi.eshopblvd.ware.entity.Purchase;
import com.hatsukoi.eshopblvd.ware.service.PurchaseService;
import com.hatsukoi.eshopblvd.ware.vo.MergeVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

    /**
     * 领取采购单
     * @param ids
     * @return
     */
    @PostMapping("/received")
    public CommonResponse received(@RequestBody List<Long> ids) {
        purchaseService.received(ids);
        return CommonResponse.success();
    }

    /**
     * 查询所有还尚在新建、已分配状态的采购单
     * 就是说分配人员还没开始处理
     * @return
     */
    @RequestMapping("/unreceive/list")
    public CommonResponse unreceiveList(@RequestParam Map<String, Object> params) {
        CommonPageInfo<Purchase> queyPage= purchaseService.queryUnreceivePurchases(params);
        return CommonResponse.success().setData(queyPage);
    }
}
