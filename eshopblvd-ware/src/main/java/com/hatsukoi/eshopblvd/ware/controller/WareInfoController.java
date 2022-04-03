package com.hatsukoi.eshopblvd.ware.controller;

import com.hatsukoi.eshopblvd.utils.CommonPageInfo;
import com.hatsukoi.eshopblvd.utils.CommonResponse;
import com.hatsukoi.eshopblvd.ware.entity.WareInfo;
import com.hatsukoi.eshopblvd.ware.service.WareInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author gaoweilin
 * @date 2022/04/03 Sun 3:43 PM
 */
@RestController
@RequestMapping("ware/wareinfo")
public class WareInfoController {
    @Autowired
    private WareInfoService wareInfoService;

    /**
     * 条件查询所有仓库列表
     * @param params
     * @return
     */
    @RequestMapping("/list")
    public CommonResponse list(@RequestParam Map<String, Object> params) {
        CommonPageInfo<WareInfo> queryPage = wareInfoService.queryWareInfoPage(params);
        return CommonResponse.success().setData(queryPage);
    }
}
