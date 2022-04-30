package com.hatsukoi.eshopblvd.api.member;

import com.hatsukoi.eshopblvd.to.MemberRegisterTO;
import com.hatsukoi.eshopblvd.to.UserLoginTO;

import java.util.HashMap;
import java.util.Map;

/**
 * 会员服务的RPC接口
 * @author gaoweilin
 * @date 2022/04/28 Thu 2:44 AM
 */
public interface MemberService {
    public HashMap<String, Object> register(MemberRegisterTO memberRegisterTO);

    HashMap<String, Object> login(UserLoginTO userLoginVO);
}
