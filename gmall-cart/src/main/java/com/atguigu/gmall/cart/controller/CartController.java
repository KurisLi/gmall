package com.atguigu.gmall.cart.controller;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.cart.interceptor.LoginInterceptor;
import com.atguigu.gmall.cart.pojo.Cart;
import com.atguigu.gmall.cart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author lzzzzz
 * @create 2020-02-01 15:56
 */
@RestController
@RequestMapping("cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping
    public Resp<Object> addCart(@RequestBody Cart cart){
        cartService.addCart(cart);
        return Resp.ok(null);
    }

    @GetMapping
    public Resp<List<Cart>> queryCarts(){
        List<Cart> carts = cartService.queryCarts();
        return Resp.ok(carts);
    }






    @GetMapping("test")
    public String test(HttpServletRequest request){
        /*System.out.println(request.getAttribute("userKey"));
        System.out.println(request.getAttribute("userId"));*/
        System.out.println(LoginInterceptor.getUserInfo().getUserId());
        System.out.println(LoginInterceptor.getUserInfo().getUserKey());
        return "xxxx";
    }


}
