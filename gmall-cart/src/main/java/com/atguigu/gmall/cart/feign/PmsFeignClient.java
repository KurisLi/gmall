package com.atguigu.gmall.cart.feign;

import com.atguigu.gmall.pms.api.GmallPmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author lzzzzz
 * @create 2020-01-14 18:26
 */
@FeignClient("pms-service")
public interface PmsFeignClient extends GmallPmsApi {
}
