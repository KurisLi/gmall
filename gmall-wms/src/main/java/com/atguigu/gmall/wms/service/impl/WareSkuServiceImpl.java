package com.atguigu.gmall.wms.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;
import com.atguigu.gmall.wms.dao.WareSkuDao;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import com.atguigu.gmall.wms.service.WareSkuService;
import com.atguigu.gmall.wms.vo.SkuLockVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private WareSkuDao wareSkuDao;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String KEY_PREFIX = "wms:stock:";

    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                new QueryWrapper<WareSkuEntity>()
        );

        return new PageVo(page);
    }

    @Override
    public List<SkuLockVo> checkAndLock(List<SkuLockVo> skuLockVos) {
        if (CollectionUtils.isEmpty(skuLockVos)){
            return null;
        }
        //如果不为空，遍历skuLockVos，对每个sku进行验库并锁库
        skuLockVos.forEach(skuLockVo -> {
            //因为验库并锁库必须具备原子性，所以需要加分布式锁
            this.checkLock(skuLockVo);
        });
        //如果有锁定失败的，需要将其他锁定成功的进行回滚
        if (skuLockVos.stream().anyMatch(skuLockVo -> skuLockVo.getLock() ==false)){
            //获取到锁定成功的
            skuLockVos.stream().filter(skuLockVo -> skuLockVo.getLock()).forEach(skuLockVo -> {
                wareSkuDao.unLock(skuLockVo.getWareId(),skuLockVo.getCount());
            });
            return skuLockVos;
            // 把库存的锁定信息保存到redis中，方便获取锁定库存的信息

        }
        String orderToken = skuLockVos.get(0).getOrderToken();
        redisTemplate.opsForValue().set(KEY_PREFIX+orderToken, JSON.toJSONString(skuLockVos));
        return null;
    }

    /**
     * 对每个sku进行验库并锁库
     * 因为验库和锁库必须具备原子性，所以小加分布式锁
     * @param skuLockVo
     */
    @Transactional
    public void checkLock(SkuLockVo skuLockVo){
        RLock fairLock = redissonClient.getFairLock("lock" + skuLockVo.getSkuId());
        fairLock.lock();
        //验库（查询是否还有库存）
        List<WareSkuEntity> wareSkuEntities = wareSkuDao.checkWare(skuLockVo.getSkuId(), skuLockVo.getCount());

        if (!wareSkuEntities.isEmpty()){
            //锁库（修改锁定的数量）
            WareSkuEntity wareSkuEntity = wareSkuEntities.get(0);
            int flag = wareSkuDao.updateWare(wareSkuEntity.getId(), skuLockVo.getCount());
            if (flag != 0){
                skuLockVo.setWareId(wareSkuEntity.getId());
                skuLockVo.setLock(true);
            }
        }
        fairLock.unlock();
    }

}