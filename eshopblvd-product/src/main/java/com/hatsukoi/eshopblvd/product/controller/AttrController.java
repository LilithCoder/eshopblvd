package com.hatsukoi.eshopblvd.product.controller;

import com.hatsukoi.eshopblvd.product.entity.Attr;
import com.hatsukoi.eshopblvd.product.service.AttrService;
import com.hatsukoi.eshopblvd.product.vo.AttrRespVO;
import com.hatsukoi.eshopblvd.product.vo.AttrVO;
import com.hatsukoi.eshopblvd.utils.CommonPageInfo;
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
        attrService.insertAttr(attrVO);
        return CommonResponse.success();
    }

    /**
     * 根据删除属性id列表进行批量删除
     * @param attrIds
     * @return
     */
    @RequestMapping("/delete")
    public CommonResponse deleteByIds(@RequestBody Long[] attrIds) {
        attrService.batchDelete(attrIds);
        return CommonResponse.success();
    }

    /**
     * 更新某个属性
     * @param attrVO
     * @return
     */
    @RequestMapping("/update")
    public CommonResponse update(@RequestBody AttrVO attrVO) {
        attrService.updateAttr(attrVO);
        return CommonResponse.success();
    }

    /**
     * 根据 分类id 匹配属性id或者模糊查询所属的属性，(销售属性、或者基本属性)
     * @param params
     * {
     *     "page": 1（当前页数）
     *     "limit": 10 （每页展示的记录数）
     *     "key": "xxx"（查询用关键词）
     * }
     * @param attrType 属性类型[0-销售属性，1-基本属性]
     * @param catelogId 所属分类id：分类id若为0，则查询全部分类下的属性
     * @return 返回VO字段还包括了所属分类名，所有分组名（如果是规格参数）
     */
    @RequestMapping("/{attrType}/list/{catelogId}")
    public CommonResponse attrList(@RequestParam Map<String, Object> params,
                                   @PathVariable("attrType") String attrType,
                                   @PathVariable("catelogId") Long catelogId) {
        CommonPageInfo<AttrRespVO> attrPage = attrService.queryAttrPage(params, attrType, catelogId);
        return CommonResponse.success().setData(attrPage);
    }

    /**
     * 获取某个属性的详情信息（作为修改属性详情时回显用）
     * @param attrId
     * @return
     */
    @RequestMapping("/info/{attrId}")
    public CommonResponse attrDetail(@PathVariable("attrId") Long attrId) {
        AttrRespVO attrRespVO = attrService.getAttrDetail(attrId);
        return CommonResponse.success().setData(attrRespVO);
    }
}
