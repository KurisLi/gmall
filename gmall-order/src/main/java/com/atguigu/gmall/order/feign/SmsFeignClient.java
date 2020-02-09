package com.atguigu.gmall.order.feign;

import com.atguigu.gmall.sms.api.GmallSmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author lzzzzz
 * @create 2020-01-14 18:31
 */
@FeignClient("sms-service")
public interface SmsFeignClient extends GmallSmsApi {
}
