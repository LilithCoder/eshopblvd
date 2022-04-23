package com.hatsukoi.eshopblvd.search.service;

import com.hatsukoi.eshopblvd.search.vo.SearchParam;
import com.hatsukoi.eshopblvd.search.vo.SearchResult;

/**
 * @author gaoweilin
 * @date 2022/04/23 Sat 3:12 PM
 */
public interface MainSearchService {
    SearchResult search(SearchParam params);
}
