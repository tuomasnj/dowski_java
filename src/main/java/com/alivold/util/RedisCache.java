package com.alivold.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
public class RedisCache {
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 缓存基本对象 Integer、String、实体类等
     * key 缓存的键
     * value 缓存的值
     */
    public <T> void setCacheObject(final String key, final T value){
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 缓存基本对象
     * key 缓存的键
     * value 缓存的值
     * timeout 过期时间
     * timeUnit 时间单位
     */
    public <T> void setCacheObject(final String key, final T value, final long timeout){
        redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
    }

    public <T> void setCacheObject(final String key, final T value, final long timeout, TimeUnit timeUnit){
        redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
    }

    /**
     * 添加过期时间
     */
    public boolean expire(final String key, final long timeout){
        return redisTemplate.expire(key, timeout, TimeUnit.SECONDS);
    }

    /**
     * 获取对象
     */
    public <T> T getCacheObject(final String key){
        ValueOperations<String, T> valueOperations = redisTemplate.opsForValue();
        return valueOperations.get(key);
    }

    /**
     * 删除单个对象
     */
    public boolean deleteObject(final String key){
        return redisTemplate.delete(key);
    }

    /**
     * 删除集合对象
     */
    public long deleteObject(final Collection collection){
        return redisTemplate.delete(collection);
    }

    /**
     * 缓存List数据
     */
    public <T> long setCacheList(final String key, final List<T> dataList){
        Long count = redisTemplate.opsForList().rightPushAll(key, dataList);
        return count == null? 0: count;
    }

    /**
     * 获取缓存的List对象
     */
    public <T> List<T> getCacheList(final String key){
        return redisTemplate.opsForList().range(key, 0, -1);
    }

    /**
     * 缓存set
     */
    public <T> BoundSetOperations<String, T> setCachSet(final String key, final Set<T> dataSet){
        BoundSetOperations<String, T> setOperation = redisTemplate.boundSetOps(key);
        Iterator<T> it = dataSet.iterator();
        while (it.hasNext()){
            setOperation.add(it.next());
        }
        return setOperation;
    }

    /**
     * 获取set
     */
    public <T> Set<T> getCacheSet(final String key){
        return redisTemplate.opsForSet().members(key);
    }

    /**
     * 设置Map
     */
    public <T> void setCacheMap(final String key, final Map<String, T> dataMap){
        if(dataMap != null){
            redisTemplate.opsForHash().putAll(key, dataMap);
        }
    }

    /**
     * 获取Map
     */
    public <T> Map<String, T> getCacheMap(final String key){
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * 获得缓存的key
     */
    public Collection<String> keys(final String pattern){
        return redisTemplate.keys(pattern);
    }
}
