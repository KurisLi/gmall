package com.atguigu.gmall.auth.feign;

import com.atguigu.gmall.ums.api.GmallUmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author lzzzzz
 * @create 2020-01-15 21:07
 */
@FeignClient("ums-service")
public interface UmsFeignClient extends GmallUmsApi {
}
