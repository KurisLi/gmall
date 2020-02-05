package com.atguigu.gmall.auth;

import com.atguigu.core.utils.JwtUtils;
import com.atguigu.core.utils.RsaUtils;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
@RunWith(SpringRunner.class)
class GmallAuthApplicationTests {

    private static final String pubKeyPath = "E:\\Project\\rsa\\rsa.pub";

    private static final String priKeyPath = "E:\\Project\\rsa\\rsa.pri";

    private PublicKey publicKey;

    private PrivateKey privateKey;

    @Test
    public void testRsa() throws Exception {
        RsaUtils.generateKey(pubKeyPath, priKeyPath, "234");
    }

   // @Before
    public void testGetRsa() throws Exception {
        this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
        this.privateKey = RsaUtils.getPrivateKey(priKeyPath);
    }

    @Test
    public void testGenerateToken() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("id", "11");
        map.put("username", "liuyan");
        // 生成token
        String token = JwtUtils.generateToken(map, privateKey, 5);
        System.out.println("token = " + token);
    }

    @Test
    public void testParseToken() throws Exception {
        String token = "eyJhbGciOiJSUzI1NiJ9.eyJpZCI6MiwidXNlck5hbWUiOiJsaXNpIiwiZXhwIjoxNTc5MTA0MjYxfQ.gCz051urVpGjRGxWve6q9SjqMzvnN78td_BFEKw1xEuMVBvJlM_-_LzgHZjrBwQMqsWo68qYHRJUohmTugHMA5qNcAo90ZDALxTfWABGpo0i5SYIw_SF2_6e50oukBjhg0eUbdZWDtn8-6Y3DbNyN7Iy-go2bkEHgZTeyuBqva1iZUkCMWkNp5EpRcJxqYuf0o8AFqO-9pvvniPEFf4SjBCOyhhELc0szOdFMDB7BtHr_MajW6sLYtDMhIvVSeiacifEYI30Cx06gFZw2qtEAWcvU0vsKvpmbHB7ubozQn6YMnT2rZqIq1kCBzqpwB9MsVmWsY978ZeeT8ZU06rOsw";

        this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
        this.privateKey = RsaUtils.getPrivateKey(priKeyPath);
        // 解析token
        Map<String, Object> map = JwtUtils.getInfoFromToken(token, publicKey);
        System.out.println("id: " + map.get("id"));
        System.out.println("userName: " + map.get("userName"));
    }
}
