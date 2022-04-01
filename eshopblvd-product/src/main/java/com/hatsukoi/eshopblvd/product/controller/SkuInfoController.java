package com.hatsukoi.eshopblvd.product.controller;

import com.hatsukoi.eshopblvd.product.entity.SkuInfo;
import com.hatsukoi.eshopblvd.product.service.SkuInfoService;
import com.hatsukoi.eshopblvd.utils.CommonPageInfo;
import com.hatsukoi.eshopblvd.utils.CommonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * sku管理
 * @author gaoweilin
 * @date 2022/04/02 Sat 2:58 AM
 */
@RestController
@RequestMapping("product/skuinfo")
public class SkuInfoController {

    @Autowired
    private SkuInfoService skuInfoService;

    /**
     * sku检索
     * @param params
     * @return
     */
    @RequestMapping("/list")
    public CommonResponse list(@RequestParam Map<String, Object> params) {
        CommonPageInfo<SkuInfo> querySkuPage = skuInfoService.querySkuPageByFilters(params);
        return CommonResponse.success().setData(querySkuPage);
    }
}
