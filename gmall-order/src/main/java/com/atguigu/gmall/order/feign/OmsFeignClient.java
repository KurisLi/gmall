package com.atguigu.gmall.order.feign;

import com.atguigu.gmall.oms.api.GmallOmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author lzzzzz
 * @create 2020-02-09 21:45
 */
@FeignClient("oms-service")
public interface OmsFeignClient extends GmallOmsApi {
}
