package com.atguigu.gmall.search.feign;

import com.atguigu.gmall.wms.api.GmallWmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author lzzzzz
 * @create 2020-01-09 16:21
 */
@FeignClient("wms-service")
public interface WmsFeignClient extends GmallWmsApi {

}
