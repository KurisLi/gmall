package com.atguigu.gmall.auth.controller;

import com.atguigu.core.bean.Resp;
import com.atguigu.core.utils.CookieUtils;
import com.atguigu.gmall.auth.config.JwtProperties;
import com.atguigu.gmall.auth.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author lzzzzz
 * @create 2020-01-15 21:01
 */
@RestController
@RequestMapping("auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtProperties jwtProperties;

    @PostMapping("accredit")
    public Resp<Object> accredit(@RequestParam("username")String userName,
                                 @RequestParam("password")String passWord,
                                 HttpServletRequest servletRequest,
                                 HttpServletResponse servletResponse){
        String token = authService.accredit(userName, passWord);
        CookieUtils.setCookie(servletRequest,servletResponse,jwtProperties.getCookieName(),token,jwtProperties.getExpireTime()*60);
        return Resp.ok(null);
    }
}
