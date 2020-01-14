package com.atguigu.gmall.index.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.index.config.GmallCache;
import com.atguigu.gmall.index.feign.PmsFeignClient;
import com.atguigu.gmall.index.service.IndexService;
import com.atguigu.gmall.pms.vo.CategoryVo;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.mysql.cj.util.TimeUtil;
import net.sf.jsqlparser.expression.StringValue;
import org.apache.commons.lang.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Key;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author lzzzzz
 * @create 2020-01-12 21:39
 */
@Service
public class IndexServiceImpl implements IndexService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private PmsFeignClient pmsFeignClient;

    @Autowired
    private RedissonClient redissonClient;

    public static final String PREFIX_NAME = "index:pid:";

    @Override
    public List<CategoryEntity> queryLevel1Category() {

        Resp<List<CategoryEntity>> categoriesResp = pmsFeignClient.getCategoriesByLevelOrPid(1, null);
        List<CategoryEntity> categoryEntities = categoriesResp.getData();
        return categoryEntities;
    }

    @Override
    @GmallCache(value = PREFIX_NAME)
    public List<CategoryVo> queryCategory2and3ByPid(Long pid) {
       /* //因为首页访问量较大，所以需要加上缓存
        String jsonString = stringRedisTemplate.opsForValue().get(PREFIX_NAME + pid);
        if (StringUtils.isNotBlank(jsonString)) {
            List<CategoryVo> categoryVoFromCache = JSON.parseArray(jsonString, CategoryVo.class);
            return categoryVoFromCache;
        }
                //为防止缓存击穿，需要加锁控制，但在分布式情况下只能加分布式锁
        //拼接上pid是因为为了防止拦截住所有的请求，我们应该只拦击该pid相关的请求
        RLock lock = redissonClient.getLock("lock"+pid);
        lock.lock();
        String jsonString2 = stringRedisTemplate.opsForValue().get(PREFIX_NAME + pid);
        //再次判断，不用每次都去数据库查询
        if (StringUtils.isNotBlank(jsonString2)) {
            List<CategoryVo> categoryVoFromCache = JSON.parseArray(jsonString2, CategoryVo.class);
            lock.unlock();
            return categoryVoFromCache;
        }*/
        //为了防止缓存穿透，将不存在的值也放入缓存，值设为null
        Resp<List<CategoryVo>> categoryVoResp = pmsFeignClient.queryCategory2and3ByPid(pid);
        List<CategoryVo> categoryVos = categoryVoResp.getData();
        //将从数据库中查询的结果放入缓存
                //为了防止缓存雪崩，在过期时间上加随机数
//        stringRedisTemplate.opsForValue().set(PREFIX_NAME + pid, JSON.toJSONString(categoryVos), 5+new Random().nextInt(5), TimeUnit.MINUTES);
       // lock.unlock();
        return categoryVos;
    }

    /**
     * 在分布式的情况下该锁就会失效
     */
/*    @Override
    public synchronized void testLock() {
        String numString = stringRedisTemplate.opsForValue().get("num");
        if (numString==null){
            return;
        }
        int num = Integer.parseInt(numString);
        stringRedisTemplate.opsForValue().set("num", String.valueOf(++num));
    }*/

    /**
     * 使用redisson分布式锁
     */
    @Override
    public synchronized void testLock() {
        RLock lock = redissonClient.getLock("key");
        lock.lock();

        String numString = stringRedisTemplate.opsForValue().get("num");
        if (numString == null) {
            return;
        }
        int num = Integer.parseInt(numString);
        stringRedisTemplate.opsForValue().set("num", String.valueOf(++num));
        lock.unlock();
    }

    /**
     * 使用redis方式实现分布式锁
     */
   /* @Override
    public synchronized void testLock() {
        //在redis中保存一个固定key值，有相同的key值去设置是将会返回0，一个请求结束后删除该key
        //这样就可以保证每次执行业务都只有一个请求
        //设置一个key
                //设置过期时间，防止死锁
                //为了防止解开别人的锁，需要在value中加以个uuid进行判断
        String uuid = UUID.randomUUID().toString();
        Boolean flag = stringRedisTemplate.opsForValue().setIfAbsent("key", uuid,5,TimeUnit.SECONDS);
        if (flag){
            String numString = stringRedisTemplate.opsForValue().get("num");
            if (numString==null){
                return;
            }
            int num = Integer.parseInt(numString);
            stringRedisTemplate.opsForValue().set("num", String.valueOf(++num));
            String uuid2 = stringRedisTemplate.opsForValue().get("key");
            if (StringUtils.equals(uuid,uuid2)){
                stringRedisTemplate.delete("key");
            }
        }else {
            try {
                TimeUnit.SECONDS.sleep(1);
                testLock();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }*/
}
