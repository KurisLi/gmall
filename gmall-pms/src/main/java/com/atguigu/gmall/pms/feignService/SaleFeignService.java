package com.atguigu.gmall.pms.feignService;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.sms.api.GmallSmsApi;
import com.atguigu.gmall.sms.vo.SaleVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author lzzzzz
 * @create 2020-01-05 17:04
 */
@FeignClient("sms-service")
public interface SaleFeignService extends GmallSmsApi {

}
