package com.atguigu.gmall.index.config;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author lzzzzz
 * @create 2020-01-13 22:46
 */
@Component
@Aspect
public class GmallCacheAspect {
    
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    @Around("@annotation(com.atguigu.gmall.index.config.GmallCache)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        //查看缓存
        Object[] args = joinPoint.getArgs();
        List<Object> argList = Arrays.asList(args);//将数组转成list集合，因为数组的同string方法返回的是地址
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Class<?> returnType = method.getReturnType();
        GmallCache annotation = method.getAnnotation(GmallCache.class);
        String prefix = annotation.value();
        //若有，则返回
        Object cache = getCache(prefix, argList, returnType);
        if (cache != null) {
            return cache;
        }
        //若没有，使用分布式锁
        RLock lock = redissonClient.getLock(annotation.lockName() + argList);
        lock.lock();
        Object cache1 = getCache(prefix, argList, returnType);
        if (cache1 != null) {
            lock.unlock();
            return cache1;
        }
        //从数据库查询
        Object result = joinPoint.proceed(joinPoint.getArgs());
        //将结果放入缓存
        stringRedisTemplate.opsForValue().set(prefix+argList,JSON.toJSONString(result),
                annotation.timeout()+new Random().nextInt(annotation.bound()), TimeUnit.MINUTES);
        lock.unlock();
        return result;
    }

    public Object getCache(String prefix,List<Object> argList,Class<?> returnType){
        String cacheString = stringRedisTemplate.opsForValue().get(prefix + argList);
        //若有，则返回
        if (StringUtils.isNotBlank(cacheString)){
            return JSON.parseObject(cacheString,returnType);
        }
        return null;
    }
}
