package com.hatsukoi.eshopblvd.member.interceptor;

import com.alibaba.fastjson.JSON;
import com.hatsukoi.eshopblvd.constant.AuthServerConstant;
import com.hatsukoi.eshopblvd.to.MemberTO;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author gaoweilin
 * @date 2022/05/08 Sun 1:45 PM
 */
@Component
public class LoginUserInterceptor implements HandlerInterceptor {
    public static ThreadLocal<MemberTO> loginUser = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        String uri = request.getRequestURI();
//        boolean match = new AntPathMatcher().match("/member/**", uri);
//        if (match) {
//            return true;
//        }

        Object member = request.getSession().getAttribute(AuthServerConstant.LOGIN_USER);
        if (member != null) {
            // 用户已登陆
            MemberTO memberTO = JSON.parseObject(JSON.toJSONString(member), MemberTO.class);
            loginUser.set(memberTO);
            return true;
        } else {
            // 用户没登陆，重定向到登陆页，提示用户去登陆
//            request.getSession().setAttribute("msg", "请先进行登陆");
            response.sendRedirect("http://ssoauth.eshopblvd.com/login");
            return false;
        }
    }
}
