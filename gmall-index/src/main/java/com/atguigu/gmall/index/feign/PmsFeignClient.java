package com.atguigu.gmall.index.feign;

import com.atguigu.gmall.pms.api.GmallPmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author lzzzzz
 * @create 2020-01-12 21:44
 */
@FeignClient("pms-service")
public interface PmsFeignClient extends GmallPmsApi {
}
