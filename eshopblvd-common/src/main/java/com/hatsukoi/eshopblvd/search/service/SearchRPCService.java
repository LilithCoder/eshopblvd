package com.hatsukoi.eshopblvd.search.service;

import com.hatsukoi.eshopblvd.to.es.SkuEsModel;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * @author gaoweilin
 * @date 2022/04/10 Sun 3:59 PM
 */
public interface SearchRPCService {
    HashMap<String, Object> productUp(List<SkuEsModel> skuList) throws IOException;
}
