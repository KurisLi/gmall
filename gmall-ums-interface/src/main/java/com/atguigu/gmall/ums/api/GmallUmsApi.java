package com.atguigu.gmall.ums.api;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.ums.entity.MemberEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author lzzzzz
 * @create 2020-01-15 20:11
 */
public interface GmallUmsApi {
    @PostMapping("ums/member/query")
    public Resp<MemberEntity> queryMember(@RequestParam("username") String username,
                                          @RequestParam("password")String password);
}
