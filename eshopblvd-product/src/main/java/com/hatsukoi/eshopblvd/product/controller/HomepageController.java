package com.hatsukoi.eshopblvd.product.controller;

import com.hatsukoi.eshopblvd.product.service.CategoryService;
import com.hatsukoi.eshopblvd.product.vo.CatalogVO;
import com.hatsukoi.eshopblvd.utils.CommonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author gaoweilin
 * @date 2022/04/10 Sun 7:30 PM
 */
@RestController
@RequestMapping("product/homepage")
public class HomepageController {
    @Autowired
    CategoryService categoryService;

    /**
     * 首页初始请求
     * 获取一级分类以及「一级分类->该分类下所有子分类」映射
     * @return
     */
    @RequestMapping("/indexapi")
    public CommonResponse initRequest() {
        Map<String, Object> result = categoryService.getHomepageInitData();
        return CommonResponse.success().setData(result);
    }
}
