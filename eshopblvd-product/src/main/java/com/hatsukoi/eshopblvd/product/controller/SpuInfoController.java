package com.hatsukoi.eshopblvd.product.controller;

import com.hatsukoi.eshopblvd.product.entity.SpuInfo;
import com.hatsukoi.eshopblvd.product.service.SpuInfoService;
import com.hatsukoi.eshopblvd.product.vo.SpuInsertVO;
import com.hatsukoi.eshopblvd.utils.CommonPageInfo;
import com.hatsukoi.eshopblvd.utils.CommonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author gaoweilin
 * @date 2022/03/28 Mon 2:07 AM
 */
@RestController
@RequestMapping("product/spuinfo")
public class SpuInfoController {
    @Autowired
    SpuInfoService spuInfoService;

    /**
     * 新增商品spu
     * @param vo
     * @return
     */
    @RequestMapping("/insert")
    public CommonResponse insert(@RequestBody SpuInsertVO vo) {
        spuInfoService.insertNewSpu(vo);
        return CommonResponse.success();
    }

    /**
     * spu查询
     * 筛选条件：分类、品牌、spu状态、关键词
     * @param params
     * @return
     */
    @RequestMapping("/list")
    public CommonResponse list(@RequestParam Map<String, Object> params) {
        CommonPageInfo<SpuInfo> queryPage = spuInfoService.querySpuPage(params);
        return CommonResponse.success().setData(queryPage);
    }
}
