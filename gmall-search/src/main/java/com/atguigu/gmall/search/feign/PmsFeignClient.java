package com.atguigu.gmall.search.feign;

import com.atguigu.gmall.pms.api.GmallPmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author lzzzzz
 * @create 2020-01-09 15:28
 */
@FeignClient("pms-service")
public interface PmsFeignClient extends GmallPmsApi {
}
