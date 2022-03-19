package com.hatsukoi.eshopblvd.product.controller;

import com.hatsukoi.eshopblvd.product.entity.AttrGroup;
import com.hatsukoi.eshopblvd.product.service.AttrGroupService;
import com.hatsukoi.eshopblvd.utils.CommonPageInfo;
import com.hatsukoi.eshopblvd.utils.CommonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 属性分组控制器
 * 对应数据库：pms_attr_group
 * @author gaoweilin
 * @date 2022/03/19 Sat 9:08 PM
 */
@RestController
@RequestMapping("product/attrgroup")
public class AttrGroupController {
    @Autowired
    AttrGroupService attrGroupService;

    /**
     * 在某一分类(categoryId)下，通过关键词匹配属性分组的id或是模糊匹配属性分组的名称来分页查询属性分组的数据
     * @param params
     * {
     *    page: 1,//当前页码
     *    limit: 10,//每页记录数
     *    sidx: 'id',//排序字段
     *    order: 'asc/desc',//排序方式
     *    key: '华为'//检索关键字
     * }
     * @param categoryId
     * @return
     */
    @RequestMapping("/list/{categoryId}")
    public CommonResponse list(@RequestParam Map<String, Object> params, @PathVariable("categoryId") Long categoryId) {
        CommonPageInfo<AttrGroup> queryPage = attrGroupService.queryAttrGroupPage(params, categoryId);
        return CommonResponse.success().setData(queryPage);
    }

}
