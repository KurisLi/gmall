package com.atguigu.gmall.cart.interceptor;

import com.atguigu.core.utils.CookieUtils;
import com.atguigu.core.utils.JwtUtils;
import com.atguigu.core.utils.RsaUtils;
import com.atguigu.gmall.cart.config.JwtProperties;
import com.atguigu.gmall.cart.pojo.UserInfo;
import io.netty.util.internal.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.UUID;

/**
 * @author lzzzzz
 * @create 2020-01-31 15:59
 */
//目的是为了获取userId
@EnableConfigurationProperties({JwtProperties.class})
@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtProperties jwtProperties;

    private static final ThreadLocal<UserInfo> THREAD_LOCAL = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        UserInfo userInfo = new UserInfo();
        //从客户端获取userKey和token
        String userKey = CookieUtils.getCookieValue(request, jwtProperties.getUserKey());
        String token = CookieUtils.getCookieValue(request, jwtProperties.getCookieName());
        if (StringUtils.isEmpty(userKey)){
            userKey = UUID.randomUUID().toString();
            CookieUtils.setCookie(request,response,jwtProperties.getUserKey(),userKey,jwtProperties.getExpireTime());
        }
        userInfo.setUserKey(userKey);
        if (StringUtils.isEmpty(token)){
            //把userinfo传递给后续的业务
            /*使用request域传送
            request.setAttribute("userkey" ,userKey);*/
            THREAD_LOCAL.set(userInfo);
            return true;
        }
        try {
            Map<String, Object> infoFromToken = JwtUtils.getInfoFromToken(token, jwtProperties.getPublicKey());
            Long userId = Long.valueOf(infoFromToken.get("id").toString());
            userInfo.setUserId(userId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        /*request.setAttribute("userKey",userInfo.getUserKey());
        request.setAttribute("userId",userInfo.getUserId());*/
        //把userInfo传递给后续业务
        THREAD_LOCAL.set(userInfo);
        //return true：放行
        return true;
    }

    //提供对外获取threadlocal的方法
    public static UserInfo getUserInfo(){
        return THREAD_LOCAL.get();
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //防止内存泄漏   线程池：请求结束不代表线程结束
        THREAD_LOCAL.remove();
    }
}
