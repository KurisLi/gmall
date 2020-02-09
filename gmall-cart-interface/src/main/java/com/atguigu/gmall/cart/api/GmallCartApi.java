package com.atguigu.gmall.cart.api;

import com.atguigu.gmall.cart.pojo.Cart;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @author lzzzzz
 * @create 2020-02-07 23:25
 */
public interface GmallCartApi {
    @GetMapping("cart/{userId}")
    public List<Cart> getCheckedCarts(@PathVariable("userId") Long userId);
}
