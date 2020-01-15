package com.atguigu.gmall.sms.api;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.sms.vo.ItemSaleVo;
import com.atguigu.gmall.sms.vo.SaleVo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author lzzzzz
 * @create 2020-01-05 19:24
 */
public interface GmallSmsApi {
    @PostMapping("/sms/skufullreduction/sale")
    public Resp<Object> saveSales(@RequestBody SaleVo saleVo);

    @GetMapping("/sms/skufullreduction/itemSaleVo/{skuId}")
    public Resp<List<ItemSaleVo>> queryItemSaleVoBySkuId(@PathVariable("skuId") Long skuId);
}
