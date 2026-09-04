package com.company.erp.purchase.infrastructure.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Redis 分布式锁组件，锁名遵循 lock:{resource}:{key} 规范。
 */
@Component
@RequiredArgsConstructor
public class RedisDistributedLock {

    private final StringRedisTemplate redisTemplate;

    public boolean tryLock(String resourceKey, long timeout, TimeUnit unit) {
        String key = "lock:" + resourceKey;
        Boolean ok = redisTemplate.opsForValue()
                .setIfAbsent(key, UUID.randomUUID().toString(), timeout, unit);
        return Boolean.TRUE.equals(ok);
    }

    public void unlock(String resourceKey) {
        redisTemplate.delete("lock:" + resourceKey);
    }
}