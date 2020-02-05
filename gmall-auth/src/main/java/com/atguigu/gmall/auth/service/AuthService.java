package com.atguigu.gmall.auth.service;

import com.atguigu.core.bean.Resp;
import com.atguigu.core.exception.UmsException;
import com.atguigu.core.utils.JwtUtils;
import com.atguigu.gmall.auth.config.JwtProperties;
import com.atguigu.gmall.auth.feign.UmsFeignClient;
import com.atguigu.gmall.ums.entity.MemberEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lzzzzz
 * @create 2020-01-15 21:01
 */
@Service
@EnableConfigurationProperties({JwtProperties.class})
public class AuthService {

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private UmsFeignClient umsFeignClient;

    public String accredit(String userName, String passWord) {
        Resp<MemberEntity> memberEntityResp = umsFeignClient.queryMember(userName, passWord);
        MemberEntity memberEntity = memberEntityResp.getData();
        if (memberEntity == null){
            throw new UmsException("用户名或密码错误!");
        }
        try {
            Map<String,Object> map = new HashMap<>();
            map.put("id",memberEntity.getId());
            map.put("userName",memberEntity.getUsername());
            String token = JwtUtils.generateToken(map, jwtProperties.getPrivateKey(), jwtProperties.getExpireTime());
            return token;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
