package com.hatsukoi.eshopblvd.ware.service.impl;

import com.hatsukoi.eshopblvd.to.SkuHasStockVO;
import com.hatsukoi.eshopblvd.utils.CommonResponse;
import com.hatsukoi.eshopblvd.ware.dao.WareSkuMapper;
import com.hatsukoi.eshopblvd.ware.entity.WareSku;
import com.hatsukoi.eshopblvd.ware.entity.WareSkuExample;
import com.hatsukoi.eshopblvd.ware.service.WareSkuRPCService;
import com.hatsukoi.eshopblvd.ware.service.WareSkuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author gaoweilin
 * @date 2022/04/06 Wed 2:09 AM
 */
@Service
@org.apache.dubbo.config.annotation.Service
public class WareSkuServiceImpl implements WareSkuService {
    @Autowired
    private WareSkuMapper wareSkuMapper;

    @Override
    public void addStock(Long skuId, Long wareId, Integer skuNum) {
        WareSkuExample wareSkuExample = new WareSkuExample();
        WareSkuExample.Criteria criteria = wareSkuExample.createCriteria();
        criteria.andSkuIdEqualTo(skuId);
        criteria.andWareIdEqualTo(wareId);
        List<WareSku> wareSkus = wareSkuMapper.selectByExample(wareSkuExample);
        if (wareSkus == null || wareSkus.size() == 0) {
            WareSku wareSku = new WareSku();
            wareSku.setSkuId(skuId);
            wareSku.setStock(skuNum);
            wareSku.setWareId(wareId);
            wareSku.setStockLocked(0);
            // TODO：远程调用product -> skuservice获取sku名字
            wareSkuMapper.insertSelective(wareSku);
        } else {
            wareSkuMapper.addStock(skuId, wareId, skuNum);
        }
    }
}
