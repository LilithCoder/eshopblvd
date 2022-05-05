package com.hatsukoi.eshopblvd.cart.interceptor;

import com.alibaba.fastjson.JSON;
import com.hatsukoi.eshopblvd.cart.constant.CartConstant;
import com.hatsukoi.eshopblvd.cart.to.UserInfoTO;
import com.hatsukoi.eshopblvd.constant.AuthServerConstant;
import com.hatsukoi.eshopblvd.constant.DomainConstant;
import com.hatsukoi.eshopblvd.to.MemberTO;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.UUID;

/**
 * 请求拦截器
 * 在执行目标方法之前，判断用户的登录状态。并封装传递(用户信息)给controller
 * @author gaoweilin
 * @date 2022/05/05 Thu 3:10 AM
 */
public class CartInterceptor implements HandlerInterceptor {
    /**
     * 整条调用链条线程的local变量存入拦截器已经封装好的用户信息
     */
    public static ThreadLocal<UserInfoTO> threadLocal = new ThreadLocal<>();

    /**
     * 业务执行前逻辑
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        // 1. 在请求前拦截，封装用户信息（无论登录与否）
        UserInfoTO userInfoTO = new UserInfoTO();
        HttpSession session = request.getSession();
        Object memberTO = session.getAttribute(AuthServerConstant.LOGIN_USER);
        if (memberTO != null) {
            // 用户登陆了
            MemberTO loginUser = JSON.parseObject(JSON.toJSONString(memberTO), MemberTO.class);
            userInfoTO.setUserId(loginUser.getId());
        }

        // 2. 检查cookies里有没有临时用户userKey
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie: cookies) {
                if (cookie.getName().equals(CartConstant.TEMP_USER_COOKIE_NAME)) {
                    userInfoTO.setUserKey(cookie.getValue());
                    // 只要已经有临时用户userkey了，tempUser字段就设置为true，这是为了临时用户userKey不要一直被持续更新
                    userInfoTO.setTempUser(true);
                }
            }
        }

        // 3. 无论有没有用户登录，都分配一个临时用户userKey
        if (StringUtils.isEmpty(userInfoTO.getUserKey())) {
            String userKey = UUID.randomUUID().toString();
            userInfoTO.setUserKey(userKey);
        }

        // 4. 将封装的用户信息放入threadlocal
        threadLocal.set(userInfoTO);
        return true;
    }

    /**
     * 业务执行后逻辑
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        UserInfoTO userInfoTO = threadLocal.get();
        // 如果这是第一次分配临时用户，将分配的userkey写入cookie，过期时间为30天
        if (!userInfoTO.isTempUser()) {
            Cookie cookie = new Cookie(CartConstant.TEMP_USER_COOKIE_NAME, userInfoTO.getUserKey());
            cookie.setDomain(DomainConstant.COOKIE_DOMAIN);
            cookie.setDomain(DomainConstant.COOKIE_DOMAIN);
            cookie.setMaxAge(CartConstant.TEMP_USER_COOKIE_TIMEOUT);
            response.addCookie(cookie);
        }
    }
}
