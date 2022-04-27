package com.hatsukoi.eshopblvd.product.controller;

import com.hatsukoi.eshopblvd.product.service.SkuInfoService;
import com.hatsukoi.eshopblvd.product.vo.SkuItemVO;
import com.hatsukoi.eshopblvd.utils.CommonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商品详情控制器
 * @author gaoweilin
 * @date 2022/04/25 Mon 12:33 AM
 */
@RestController
@RequestMapping("product/itempage")
public class ItemController {
    @Autowired
    private SkuInfoService skuInfoService;

    @GetMapping("/sku/{skuId}")
    public CommonResponse getProductDetail(@PathVariable("skuId") Long skuId) {
        SkuItemVO skuItemVO = skuInfoService.getSkuDetail(skuId);
        return CommonResponse.success().setData(skuItemVO);
    }
}
