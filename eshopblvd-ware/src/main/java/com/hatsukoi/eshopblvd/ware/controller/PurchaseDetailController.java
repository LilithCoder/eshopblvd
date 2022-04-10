package com.hatsukoi.eshopblvd.ware.controller;

import com.hatsukoi.eshopblvd.utils.CommonPageInfo;
import com.hatsukoi.eshopblvd.utils.CommonResponse;
import com.hatsukoi.eshopblvd.ware.entity.PurchaseDetail;
import com.hatsukoi.eshopblvd.ware.service.PurchaseDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 采购需求controller
 * 表：wms_purchase_detail
 * @author gaoweilin
 * @date 2022/04/04 Mon 11:34 PM
 */
@RestController
@RequestMapping("ware/purchasedetail")
public class PurchaseDetailController {
    @Autowired
    private PurchaseDetailService purchaseDetailService;

    /**
     * 根据状态、仓库id、检索关键词来查询所有采购选项
     * @param params
     * {
     *    page: 1,//当前页码
     *    limit: 10,//每页记录数
     *    key: '华为',//检索关键字
     *    status: 0,//状态
     *    wareId: 1,//仓库id
     * }
     * @return
     */
    @RequestMapping("/list")
    public CommonResponse list(@RequestParam Map<String, Object> params) {
        CommonPageInfo<PurchaseDetail> queryPage = purchaseDetailService.queryPage(params);
        return CommonResponse.success().setData(queryPage);
    }
}
