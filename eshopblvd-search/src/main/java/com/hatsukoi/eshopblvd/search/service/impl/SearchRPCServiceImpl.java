package com.hatsukoi.eshopblvd.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.hatsukoi.eshopblvd.search.config.EshopblvdElasticSearchConfig;
import com.hatsukoi.eshopblvd.search.constant.EsConstant;
import com.hatsukoi.eshopblvd.search.service.SearchRPCService;
import com.hatsukoi.eshopblvd.to.es.SkuEsModel;
import com.hatsukoi.eshopblvd.utils.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author gaoweilin
 * @date 2022/04/10 Sun 3:58 PM
 */
@Slf4j
@Service
@com.alibaba.dubbo.config.annotation.Service
public class SearchRPCServiceImpl implements SearchRPCService {
    @Autowired
    RestHighLevelClient restHighLevelClient;

    /**
     * 保存sku们到es中
     * @param skuList
     * @return
     */
    @Override
    public CommonResponse productUp(List<SkuEsModel> skuList) throws IOException {
        // 1. 给es中建立索引product，建立好映射关系，kibana中手动创建
        // 2. 给es中批量保存这些数据
        BulkRequest bulkRequest = new BulkRequest();
        for (SkuEsModel model: skuList) {
            // 构造保存请求
            IndexRequest indexRequest = new IndexRequest(EsConstant.PRODUCT_INDEX);
            indexRequest.id(model.getSkuId().toString());
            String jsonString = JSON.toJSONString(model);
            indexRequest.source(jsonString, XContentType.JSON);
            bulkRequest.add(indexRequest);
        }
        BulkResponse responses = restHighLevelClient.bulk(bulkRequest, EshopblvdElasticSearchConfig.COMMON_OPTIONS);
        boolean isOk = responses.hasFailures();
        List<String> collect = Arrays.stream(responses.getItems()).map(item -> {
            return item.getId();
        }).collect(Collectors.toList());
        log.info("商品上架完成：{}，返回数据：{}",collect,responses.toString());
        if (isOk) {
            return CommonResponse.success();
        } else {
            return CommonResponse.error();
        }
    }
}
