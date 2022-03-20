package com.hatsukoi.eshopblvd.product.controller;

import com.hatsukoi.eshopblvd.product.entity.Attr;
import com.hatsukoi.eshopblvd.product.service.AttrService;
import com.hatsukoi.eshopblvd.product.vo.AttrVO;
import com.hatsukoi.eshopblvd.utils.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 属性（规格参数&商品属性）
 * 对应数据库：pms_attr
 * @author gaoweilin
 * @date 2022/03/20 Sun 4:10 PM
 */
@Slf4j
@RestController
@RequestMapping("product/attr")
public class AttrController {
    @Autowired
    AttrService attrService;

    /**
     * 新增属性
     * @param attrVO
     * @return
     */
    @RequestMapping("/insert")
    public CommonResponse insertAttr(@RequestBody AttrVO attrVO) {
        log.info("attrVO: {}", attrVO);
        attrService.insertAttr(attrVO);
        return CommonResponse.success();
    }

    /**
     * 根据分类id查询所属的属性，可以只有销售属性、或者基本属性
     * @param params
     * @param attrType 属性类型[0-销售属性，1-基本属性，2-既是销售属性又是基本属性]
     * @param catelogId
     * @return
     */
    @RequestMapping("/{attrType}/list/{catelogId}")
    public CommonResponse attrList(@RequestParam Map<String, Object> params,
                                   @PathVariable("attrType") String attrType,
                                   @PathVariable("catelogId") Long catelogId) {
        return CommonResponse.success();
    }
}
