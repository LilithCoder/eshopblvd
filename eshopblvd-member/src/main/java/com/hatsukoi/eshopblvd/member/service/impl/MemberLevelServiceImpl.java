package com.hatsukoi.eshopblvd.member.service.impl;

import com.github.pagehelper.PageHelper;
import com.hatsukoi.eshopblvd.member.dao.MemberLevelMapper;
import com.hatsukoi.eshopblvd.member.entity.MemberLevel;
import com.hatsukoi.eshopblvd.member.entity.MemberLevelExample;
import com.hatsukoi.eshopblvd.member.service.MemberLevelService;
import com.hatsukoi.eshopblvd.utils.CommonPageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author gaoweilin
 * @date 2022/03/24 Thu 2:11 AM
 */
@Service
public class MemberLevelServiceImpl implements MemberLevelService {
    @Autowired
    MemberLevelMapper memberLevelMapper;

    @Override
    public CommonPageInfo<MemberLevel> queryMemberLevelPage(Map<String, Object> params) {
        // 分页参数
        int pageNum = 1;
        int pageSize = 10;
        if (params.get("page") != null) {
            pageNum = Integer.parseInt(params.get("page").toString());
        }
        if (params.get("limit") != null) {
            pageSize = Integer.parseInt(params.get("limit").toString());
        }
        PageHelper.startPage(pageNum, pageSize);
        List<MemberLevel> memberLevels = memberLevelMapper.selectByExample(new MemberLevelExample());
        return CommonPageInfo.convertToCommonPage(memberLevels);
    }
}
