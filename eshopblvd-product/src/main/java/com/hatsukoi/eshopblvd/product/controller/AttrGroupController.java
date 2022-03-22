package com.hatsukoi.eshopblvd.product.controller;

import com.hatsukoi.eshopblvd.product.entity.Attr;
import com.hatsukoi.eshopblvd.product.entity.AttrGroup;
import com.hatsukoi.eshopblvd.product.service.AttrGroupService;
import com.hatsukoi.eshopblvd.product.service.AttrService;
import com.hatsukoi.eshopblvd.product.service.CategoryService;
import com.hatsukoi.eshopblvd.product.vo.AttrAttrGroupRelationVO;
import com.hatsukoi.eshopblvd.utils.CommonPageInfo;
import com.hatsukoi.eshopblvd.utils.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 属性分组控制器
 * 对应数据库：pms_attr_group
 * @author gaoweilin
 * @date 2022/03/19 Sat 9:08 PM
 */
@Slf4j
@RestController
@RequestMapping("product/attrgroup")
public class AttrGroupController {
    @Autowired
    AttrGroupService attrGroupService;

    @Autowired
    CategoryService categoryService;

    @Autowired
    AttrService attrService;

    /**
     * 新增属性分组
     * @param attrGroup
     * @return
     */
    @RequestMapping("/insert")
    public CommonResponse insert(@RequestBody AttrGroup attrGroup) {
        attrGroupService.insertAttrGroup(attrGroup);
        return CommonResponse.success();
    }

    /**
     * 批量删除属性分组
     * @param attrGroupIds
     * @return
     */
    @RequestMapping("/batchDelete")
    public CommonResponse delete(@RequestBody Long[] attrGroupIds) {
        attrGroupService.deleteAttrGroupByIds(attrGroupIds);
        return CommonResponse.success();
    }

    /**
     * 批量删除分组和基础属性的关联
     * @param relationVOs
     * @return
     */
    @PostMapping("/attr/relation/delete")
    public CommonResponse deleteRelations(@RequestBody AttrAttrGroupRelationVO[] relationVOs) {
        attrGroupService.deleteRelations(relationVOs);
        return CommonResponse.success();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public CommonResponse update(@RequestBody AttrGroup attrGroup) {
        attrGroupService.updateAttrGroup(attrGroup);
        return CommonResponse.success();
    }

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

    /**
     * 根据id获取属性分组的详细信息，且返回新增字段catelogPath，含义为该属性分组所属分类的三级分类路径
     * @param attrGroupId
     * @return
     */
    @RequestMapping("/info/{attrGroupId}")
    public CommonResponse getAttrGroupInfo(@PathVariable("attrGroupId") Long attrGroupId) {
        AttrGroup attrGroup = attrGroupService.getAttrGroupById(attrGroupId);
        Long catelogId = attrGroup.getCatelogId();
        Long[] catelogPath = categoryService.getCatelogPath(catelogId);
        attrGroup.setCatelogPath(catelogPath);
        return CommonResponse.success().setData(attrGroup);
    }

    /**
     * 获取指定分组关联的所有属性，根据分组id来查找与之相关的规格参数
     * @param attrgroupId
     * @return
     */
    @GetMapping("/{attrgroupId}/attr/relation")
    public CommonResponse getAllRelatedAttrs(@PathVariable("attrgroupId") Long attrgroupId) {
        List<Attr> attrs = attrService.getRelatedAttrsByAttrGroup(attrgroupId);
        return CommonResponse.success().setData(attrs);
    }

    /**
     * 获取属性分组里面还没有关联的本分类里面的其他基本属性，方便添加新的关联
     * @param attrgroupId
     * @return
     */
    @GetMapping("/{attrgroupId}/noattr/relation`")
    public CommonResponse getAllNonRelatedAttrs(@PathVariable("attrgroupId") Long attrgroupId,
                                                @RequestParam Map<String, Object> params) {
        CommonPageInfo<Attr> queryPage = attrService.getNonRelatedAttrsByAttrGroup(attrgroupId, params);
        return CommonResponse.success().setData(queryPage);
    }

}
