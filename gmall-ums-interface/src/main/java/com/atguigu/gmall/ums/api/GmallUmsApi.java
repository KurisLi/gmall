package com.atguigu.gmall.ums.api;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.ums.entity.MemberEntity;
import com.atguigu.gmall.ums.entity.MemberReceiveAddressEntity;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author lzzzzz
 * @create 2020-01-15 20:11
 */
public interface GmallUmsApi {
    @PostMapping("ums/member/query")
    public Resp<MemberEntity> queryMember(@RequestParam("username") String username,
                                          @RequestParam("password")String password);
    @GetMapping("ums/memberreceiveaddress/{userId}")
    public Resp<List<MemberReceiveAddressEntity>> queryAddressesByUserId(@PathVariable("userId") Long userId);

    @GetMapping("ums/member/info/{id}")
    public Resp<MemberEntity> info(@PathVariable("id") Long id);
}
