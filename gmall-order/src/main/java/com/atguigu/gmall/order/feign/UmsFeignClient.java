package com.atguigu.gmall.order.feign;

import com.atguigu.gmall.ums.api.GmallUmsApi;
import com.atguigu.gmall.wms.api.GmallWmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author lzzzzz
 * @create 2020-01-14 18:31
 */
@FeignClient("ums-service")
public interface UmsFeignClient extends GmallUmsApi {
}
