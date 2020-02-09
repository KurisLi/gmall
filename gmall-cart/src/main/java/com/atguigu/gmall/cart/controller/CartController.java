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

    /**
     * 获取选中的购物车
     */
    @GetMapping("{userId}")
    public List<Cart> getCheckedCarts(@PathVariable("userId") Long userId){
        return cartService.getCheckedCarts(userId);
    }

    /**
     *删除购物车
     */
    @PostMapping("delete")
    public Resp<Object> delete(@RequestParam("skuId") Long skuId){
        cartService.delete(skuId);
        return Resp.ok(null);
    }

    /**
     * 更新选中状态
     */
    @PostMapping("check")
    public Resp<Object> updateChecked(@RequestBody Cart cart){
        cartService.updateChecked(cart);
        return Resp.ok(null);
    }


    /**
     * 更新购物车数量
     * @param cart
     * @return
     */
    @PostMapping("update")
    public Resp<Object> updateNum(@RequestBody Cart cart){
        cartService.updateNum(cart);
        return Resp.ok(null);
    }

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
