package com.atguigu.gmall.order.feign;

import com.atguigu.gmall.wms.api.GmallWmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author lzzzzz
 * @create 2020-01-14 18:31
 */
@FeignClient("wms-service")
public interface WmsFeignClient extends GmallWmsApi {
}
