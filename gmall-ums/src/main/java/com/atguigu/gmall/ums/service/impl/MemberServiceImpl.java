package com.atguigu.gmall.ums.service.impl;

import com.atguigu.core.exception.UmsException;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;

import com.atguigu.gmall.ums.dao.MemberDao;
import com.atguigu.gmall.ums.entity.MemberEntity;
import com.atguigu.gmall.ums.service.MemberService;

import javax.xml.crypto.Data;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private MemberDao memberDao;

    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageVo(page);
    }

    @Override
    public Boolean checkData(String data, Integer type) {
        QueryWrapper<MemberEntity> wrapper = new QueryWrapper<>();
        switch (type){
            case 1: wrapper.eq("username",data); break;
            case 2: wrapper.eq("mobile",data); break;
            case 3: wrapper.eq("email",data); break;
            default:
                return null;
        }
        return this.count(wrapper) == 0;
    }

    @Override
    public void register(MemberEntity memberEntity, String code) {
        if (memberEntity==null){
            return;
        }
        String redisCode = redisTemplate.opsForValue().get(memberEntity.getMobile());
        if (!StringUtils.equals(redisCode,code)){
            throw new UmsException("验证码错误！");
        }

            String salt = StringUtils.substring(UUID.randomUUID().toString(), 0,6);
            memberEntity.setSalt(salt);
            memberEntity.setPassword(DigestUtils.md5Hex(memberEntity.getPassword() + salt));
            memberEntity.setLevelId(1l);
            memberEntity.setGrowth(1000);
            memberEntity.setIntegration(1000);
            memberEntity.setStatus(1);
            memberEntity.setSourceType(1);
            memberEntity.setCreateTime(new Date());
            this.save(memberEntity);
        redisTemplate.delete(memberEntity.getMobile());
    }

    @Override
    public MemberEntity queryMember(String username, String password) {
        MemberEntity memberEntity = memberDao.selectOne(new QueryWrapper<MemberEntity>().eq("username", username));
        if (memberEntity==null){
            return memberEntity;
        }
        String passwordFromDB = memberEntity.getPassword();
        String passwordFromCli = DigestUtils.md5Hex(password + memberEntity.getSalt());
        if (!StringUtils.equals(passwordFromDB,passwordFromCli)){
            return null;
        }
        return memberEntity;
    }

}