package com.hatsukoi.eshopblvd.member.controller;

import com.hatsukoi.eshopblvd.member.entity.MemberLevel;
import com.hatsukoi.eshopblvd.member.service.MemberLevelService;
import com.hatsukoi.eshopblvd.utils.CommonPageInfo;
import com.hatsukoi.eshopblvd.utils.CommonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author gaoweilin
 * @date 2022/03/24 Thu 2:09 AM
 */
@RestController
@RequestMapping("member/memberlevel")
public class MemberLevelController {
    @Autowired
    MemberLevelService memberLevelService;

    /**
     * 分页查询会员等级的列表
     * @param params
     * @return
     */
    @RequestMapping("/list")
    public CommonResponse list(@RequestParam Map<String, Object> params) {
        CommonPageInfo<MemberLevel> page = memberLevelService.queryMemberLevelPage(params);
        return CommonResponse.success().setData(page);
    }
}
