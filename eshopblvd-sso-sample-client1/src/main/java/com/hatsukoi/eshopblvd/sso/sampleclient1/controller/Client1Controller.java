package com.hatsukoi.eshopblvd.sso.sampleclient1.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hatsukoi.eshopblvd.to.MemberTO;
import com.hatsukoi.eshopblvd.utils.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

import static com.hatsukoi.eshopblvd.constant.AuthServerConstant.LOGIN_USER;

/**
 * @author gaoweilin
 * @date 2022/05/03 Tue 12:05 PM
 */
@Slf4j
@Controller
public class Client1Controller {
    @Value("${sso.auth.host}")
    private String ssoAuthHost;

    @Value("${sso.auth.path}")
    private String ssoAuthPath;

    @Value("${sso.client.url}")
    private String ssoClient;

    @GetMapping("/")
    public String client1(@RequestParam(value = "ticket", required = false) String ticket,
                         HttpServletRequest request,
                         HttpSession session) throws Exception {
        // 检查ticket，有的话就发请求给CAS服务校验这个ticket，如果通过然后返回登陆用户的信息，然后放入应用系统域名下的session
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("ticket", ticket);
        queryParams.put("service", ssoClient);
        if (!StringUtils.isEmpty(ticket)) {
            HttpResponse getResp = HttpUtils.doGet(ssoAuthHost, "loginUser", "get", new HashMap<>(), queryParams);
            if (getResp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                String jsonStr = EntityUtils.toString(getResp.getEntity());
                JSONObject jsonObject = JSON.parseObject(jsonStr);
                MemberTO loginUser = jsonObject.getObject(LOGIN_USER, MemberTO.class);
                session.setAttribute(LOGIN_USER, loginUser);
            }
        }

        // 检查session有没有登陆
        Object loginUser = session.getAttribute("loginUser");
        if (loginUser != null) {
            // 登陆了
            return "client1";
        } else {
            // 没登陆跳转到SSO认证中心去，拼接重定向service参数为当前系统url，方便登陆成功后重定向回来
            return "redirect:" + ssoAuthHost + ssoAuthPath + "?service=" + ssoClient;
        }
    }
}
