package com.hatsukoi.eshopblvd.ware.service.impl;

import com.hatsukoi.eshopblvd.to.SkuHasStockVO;
import com.hatsukoi.eshopblvd.utils.CommonResponse;
import com.hatsukoi.eshopblvd.ware.dao.WareSkuMapper;
import com.hatsukoi.eshopblvd.ware.service.WareSkuRPCService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 库存服务RPC远程调用的实现
 * @author gaoweilin
 * @date 2022/04/10 Sun 3:09 PM
 */
@Service
@org.apache.dubbo.config.annotation.Service
public class WareSkuRPCServiceImpl implements WareSkuRPCService {
    @Autowired
    private WareSkuMapper wareSkuMapper;

    /**
     * 返回「sku -> 是否有库存」映射
     * @param skuIds
     * @return
     */
    @Override
    public CommonResponse getSkusHasStock(List<Long> skuIds) {
        List<SkuHasStockVO> collect = skuIds.stream().map(skuId -> {
            SkuHasStockVO skuHasStockVO = new SkuHasStockVO();
            // select sum(stock - stock_locked) from `wms_ware_sku` where sku_id = ?
            Long count = wareSkuMapper.getSkuStock(skuId);
            skuHasStockVO.setSkuId(skuId);
            skuHasStockVO.setHasStock(count == null ? false : count > 0);
            return skuHasStockVO;
        }).collect(Collectors.toList());
        return CommonResponse.success().setData(collect);
    }
}
