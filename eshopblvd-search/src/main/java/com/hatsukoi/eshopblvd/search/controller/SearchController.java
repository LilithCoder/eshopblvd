package com.hatsukoi.eshopblvd.search.controller;

import com.hatsukoi.eshopblvd.search.service.MainSearchService;
import com.hatsukoi.eshopblvd.search.vo.SearchParam;
import com.hatsukoi.eshopblvd.search.vo.SearchResult;
import com.hatsukoi.eshopblvd.utils.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author gaoweilin
 * @date 2022/04/23 Sat 12:14 PM
 */
@Slf4j
@RestController
@RequestMapping("search")
public class SearchController {
    @Autowired
    MainSearchService mainSearchService;

    /**
     * 搜索页面的主搜接口
     * 根据检索入参检索出所有符合的商品、筛选项、分页信息
     * @return
     */
    @GetMapping("/mainSearch")
    public CommonResponse searchRequest(SearchParam params) {
        log.info(params.toString());
        SearchResult result = mainSearchService.search(params);
        return CommonResponse.success().setData(result);
    }
}
