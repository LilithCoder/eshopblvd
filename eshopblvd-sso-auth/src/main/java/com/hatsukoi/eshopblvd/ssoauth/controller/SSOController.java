package com.hatsukoi.eshopblvd.ssoauth.controller;

import com.alibaba.fastjson.JSON;
import com.hatsukoi.eshopblvd.constant.AuthServerConstant;
import com.hatsukoi.eshopblvd.constant.DomainConstant;
import com.hatsukoi.eshopblvd.exception.BizCodeEnum;
import com.hatsukoi.eshopblvd.ssoauth.exception.LoginAcctNonExistException;
import com.hatsukoi.eshopblvd.ssoauth.exception.LoginAcctPasswordInvalidException;
import com.hatsukoi.eshopblvd.ssoauth.service.AuthService;
import com.hatsukoi.eshopblvd.ssoauth.vo.UserLoginVO;
import com.hatsukoi.eshopblvd.to.MemberTO;
import com.hatsukoi.eshopblvd.to.UserLoginTO;
import com.hatsukoi.eshopblvd.utils.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author gaoweilin
 * @date 2022/05/03 Tue 2:49 AM
 */
@Slf4j
@Controller
public class SSOController {
    /**
     * 第一个key是session_id
     * 第二个key是某个session中的key，例如"loginUser"
     */
    public static Map<String,Map<String, Object>> sessionCache = new HashMap<>();

    public static Map<String,Object> serviceTicketCache = new HashMap<>();

    @Autowired
    private AuthService authService;

    @GetMapping("/login")
    public String getLoginPage(@RequestParam(value = "service", required = false) String service,
                               @CookieValue(value = DomainConstant.CASTGC, required = false) String CASTGC,
                               HttpServletRequest request,
                               Model model) {
        if (!StringUtils.isEmpty(service)) {
            model.addAttribute("service", service);
        }
        if (!StringUtils.isEmpty(CASTGC)) {
            // 说明之前有人登录过，浏览器留下了痕迹
            // 生成一个ticket给这个应用系统
            // key为service，value为TGT，存入本地缓存
            String ST = "ST-" + UUID.randomUUID().toString().replace("-", "");
            if (serviceTicketCache.get(service) == null) {
                serviceTicketCache.put(service, ST);
            }
            log.info("1: {}", service);
            return "redirect:" + service + "?ticket=" + ST;
        }
        // 没有session直接展示登陆页
        return "login";
    }

    @ResponseBody
    @PostMapping("/ssologin")
    public CommonResponse ssoLogin(@RequestParam("userAcc") String userAcc,
                                   @RequestParam("password") String password,
                                   @RequestParam("service") String service,
                                   HttpSession session,
                                   HttpServletRequest request,
                                   HttpServletResponse response) {
        try {
            // 调用登陆业务
            UserLoginVO userLoginVO = new UserLoginVO();
            userLoginVO.setUserAcc(userAcc);
            userLoginVO.setPassword(password);
            authService.login(userLoginVO, session);
        } catch (LoginAcctNonExistException exception) {
            // 返回错误信息，在登陆页上展示
            return CommonResponse.error(BizCodeEnum.LOGINACCT_NONEXIST_EXCEPTION.getCode(), BizCodeEnum.LOGINACCT_NONEXIST_EXCEPTION.getMsg());
        } catch (LoginAcctPasswordInvalidException exception) {
            // 返回错误信息，在登陆页上展示
            return CommonResponse.error(BizCodeEnum.LOGINACCT_PASSWORD_INVAILD_EXCEPTION.getCode(), BizCodeEnum.LOGINACCT_PASSWORD_INVAILD_EXCEPTION.getMsg());
        }

        // 创建一个session的id(TGT)，并将loginUser的键值对存入该TGT下
        // 将这个TGT存入TGC
        String TGT = "TGT-" + UUID.randomUUID().toString().replace("-", "");
        Map<String, Object> finalSession = new HashMap<>();
        finalSession.put(AuthServerConstant.LOGIN_USER, session.getAttribute(AuthServerConstant.LOGIN_USER));
        sessionCache.put(TGT, finalSession);
        Cookie CASTGC = new Cookie(DomainConstant.CASTGC, TGT);
        response.addCookie(CASTGC);

        // service ticket给应用系统用来给CAS鉴权获取登陆态用
        // key为service，value为TGT，存入本地缓存
        String ST = "ST-" + UUID.randomUUID().toString().replace("-", "");
        if (serviceTicketCache.get(service) == null) {
            serviceTicketCache.put(service, ST);
        }
        // 重定向到应用系统页
        String url = service + "?ticket=" + ST;
        log.info("2: {}", url);
        response.setStatus(HttpStatus.SC_MOVED_TEMPORARILY);
        response.addHeader("Location", url);

        return CommonResponse.success();
    }

    @ResponseBody
    @GetMapping("/loginUser")
    public MemberTO loginUser(@RequestParam("ticket") String ticket,
                              @RequestParam("service") String service,
                              HttpServletRequest request){
        // 获取传入的ticket，先通过应用系统的地址来找到对应的ticket，来匹配ticket是否正确，正确的话将这个service的key删除
        // 这个ticket的生命周期就一次，且针对这个服务
        log.info("sso {}", service);
        if (serviceTicketCache.get(service) != null && serviceTicketCache.get(service).toString().equals(ticket)) {
            // 从cookie中获取TGT
            Cookie[] cookies = request.getCookies();
            if (cookies != null && cookies.length > 0) {
                for (Cookie cookie: cookies) {
                    if (cookie.getName() == DomainConstant.CASTGC) {
                        String TGT = cookie.getValue();
                        Object loginUser = sessionCache.get(TGT).get(AuthServerConstant.LOGIN_USER);
                        String jsonString = JSON.toJSONString(loginUser);
                        MemberTO memberTO = JSON.parseObject(jsonString, MemberTO.class);
                        return memberTO;
                    }
                }
            }
        }
        return null;
    }
}
