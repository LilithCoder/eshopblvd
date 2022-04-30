package com.hatsukoi.eshopblvd.member.service.impl;

import com.hatsukoi.eshopblvd.api.member.MemberService;
import com.hatsukoi.eshopblvd.exception.BizCodeEnum;
import com.hatsukoi.eshopblvd.member.dao.MemberLevelMapper;
import com.hatsukoi.eshopblvd.member.dao.MemberMapper;
import com.hatsukoi.eshopblvd.member.entity.Member;
import com.hatsukoi.eshopblvd.member.entity.MemberExample;
import com.hatsukoi.eshopblvd.member.exception.PhoneExistException;
import com.hatsukoi.eshopblvd.member.exception.UsernameExistException;
import com.hatsukoi.eshopblvd.member.util.EncryptUtils;
import com.hatsukoi.eshopblvd.to.MemberRegisterTO;
import com.hatsukoi.eshopblvd.to.MemberTO;
import com.hatsukoi.eshopblvd.to.UserLoginTO;
import com.hatsukoi.eshopblvd.utils.CommonResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @author gaoweilin
 * @date 2022/04/28 Thu 2:48 AM
 */
@Service
@org.apache.dubbo.config.annotation.Service
public class MemberRpcServiceImpl implements MemberService {
    @Autowired
    private MemberLevelMapper memberLevelMapper;

    @Autowired
    private MemberMapper memberMapper;

    /**
     * 用户注册接口方法RPC实现
     * @param memberRegisterTO
     */
    @Override
    public CommonResponse register(MemberRegisterTO memberRegisterTO) {
        try {
            doRegister(memberRegisterTO);
        } catch (PhoneExistException exception) {
            return CommonResponse.error(BizCodeEnum.PHONE_EXIST_EXCEPTION.getCode(), BizCodeEnum.PHONE_EXIST_EXCEPTION.getMsg());
        } catch (UsernameExistException exception) {
            return CommonResponse.error(BizCodeEnum.USER_EXIST_EXCEPTION.getCode(), BizCodeEnum.USER_EXIST_EXCEPTION.getMsg());
        }
        return CommonResponse.success();
    }

    /**
     * 用户登陆接口方法RPC实现
     * @param userLoginVO
     * @return
     */
    @Override
    public CommonResponse login(UserLoginTO userLoginVO) {
        String userAcc = userLoginVO.getUserAcc();
        String rawPassword = userLoginVO.getPassword();

        // 1. 去数据库查询是否有这个登录账号对应的用户，可能是手机号或用户名
        // SELECT * FROM `ums_member` WHERE username=? OR mobile=?
        MemberExample memberExample = new MemberExample();
        MemberExample.Criteria criteria = memberExample.createCriteria();
        criteria.andUsernameEqualTo(userAcc);
        MemberExample.Criteria criteria1 = memberExample.createCriteria();
        criteria1.andMobileEqualTo(userAcc);
        memberExample.or(criteria1);
        List<Member> members = memberMapper.selectByExample(memberExample);

        if (members == null || members.size() == 0) {
            // 该用户没有注册
            CommonResponse error = CommonResponse.error(BizCodeEnum.LOGINACCT_NONEXIST_EXCEPTION.getCode(), BizCodeEnum.LOGINACCT_NONEXIST_EXCEPTION.getMsg());
            return error;
        } else {
            Member member = members.get(0);
            // 登陆密码校验
            String encryptedPassword = member.getPassword();
            Long salt = member.getSalt();
            boolean verify = EncryptUtils.verify(rawPassword, salt, encryptedPassword);
            if (verify) {
                MemberTO memberTO = new MemberTO();
                BeanUtils.copyProperties(member, memberTO);
                return CommonResponse.success().setData(memberTO);
            } else {
                return CommonResponse.error(BizCodeEnum.LOGINACCT_PASSWORD_INVAILD_EXCEPTION.getCode(), BizCodeEnum.LOGINACCT_PASSWORD_INVAILD_EXCEPTION.getMsg());
            }
        }
    }

    /**
     * 具体注册逻辑
     * @param memberRegisterTO
     */
    private void doRegister(MemberRegisterTO memberRegisterTO) {
        Member member = new Member();
        // 1. 设置会员等级为默认等级
        member.setLevelId(memberLevelMapper.getDefaultMemberLevel());

        // 2. 验证用户名和手机号是否唯一，不是的话抛异常出来
        String phone = memberRegisterTO.getPhone();
        String userName = memberRegisterTO.getUserName();
        checkPhoneUnique(phone);
        checkUsernameUnique(userName);

        // 3. 验证通过后设置手机号、用户名、昵称
        member.setMobile(phone);
        member.setUsername(userName);
        member.setNickname(userName);

        // 4. 密码加密处理「md5盐值加密」
        String password = memberRegisterTO.getPassword();
        Long salt = System.currentTimeMillis();
        String encryptedPassword = EncryptUtils.encryptWithSalt(password, salt);
        member.setPassword(encryptedPassword);
        member.setSalt(salt);

        // 5. 设置其他默认信息
        member.setCreateTime(new Date());

        // 6. 数据库插入信息
        memberMapper.insertSelective(member);
    }

    /**
     * 检查手机号是否已被注册，如果是则抛异常
     * @param phone
     * @throws PhoneExistException
     */
    private void checkPhoneUnique(String phone) throws PhoneExistException {
        MemberExample memberExample = new MemberExample();
        MemberExample.Criteria criteria = memberExample.createCriteria();
        criteria.andMobileEqualTo(phone);
        long count = memberMapper.countByExample(memberExample);
        if (count > 0) {
            throw new PhoneExistException();
        }
    }

    /**
     * 检查用户名是否已被占用，如果是则抛异常
     * @param username
     * @throws UsernameExistException
     */
    private void checkUsernameUnique(String username) throws UsernameExistException {
        MemberExample memberExample = new MemberExample();
        MemberExample.Criteria criteria = memberExample.createCriteria();
        criteria.andUsernameEqualTo(username);
        long count = memberMapper.countByExample(memberExample);
        if (count > 0) {
            throw new UsernameExistException();
        }
    }
}
