package com.atguigu.gmall.index.config;

import org.springframework.transaction.TransactionDefinition;

import java.lang.annotation.*;

/**
 * @author lzzzzz
 * @create 2020-01-13 20:23
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GmallCache {
    //设置key的前缀
    String value() default "";
    //设置过期时间，分钟
    int timeout() default 30;
    //设置防止雪崩的范围
    int bound() default 5;
    //设置分布式锁的名字
    String lockName() default "lock";

}
