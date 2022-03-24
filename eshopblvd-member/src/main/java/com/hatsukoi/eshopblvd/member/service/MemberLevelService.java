package com.hatsukoi.eshopblvd.member.service;

import com.hatsukoi.eshopblvd.member.entity.MemberLevel;
import com.hatsukoi.eshopblvd.utils.CommonPageInfo;

import java.util.Map;

/**
 * @author gaoweilin
 * @date 2022/03/24 Thu 2:11 AM
 */
public interface MemberLevelService {
    CommonPageInfo<MemberLevel> queryMemberLevelPage(Map<String, Object> params);
}
