package com.atguigu.gmall.order.feign;

import com.atguigu.gmall.cart.api.GmallCartApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author lzzzzz
 * @create 2020-02-07 23:30
 */
@FeignClient("cart-service")
public interface CartFeignClient extends GmallCartApi {
}
