package com.company.erp.inventory.infrastructure.cache;

import com.company.erp.inventory.domain.exception.InventoryDomainException;
import com.company.erp.inventory.domain.service.InventoryDistributedLock;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collections;
import java.util.UUID;

/**
 * 基于 Redis 的库存分布式锁实现（SET NX EX + Lua 释放）。
 */
@Component
@RequiredArgsConstructor
public class RedisInventoryDistributedLock implements InventoryDistributedLock {

    private static final String LOCK_PREFIX = "lock:inventory:";
    private static final Duration EXPIRE = Duration.ofSeconds(10);

    private static final String RELEASE_SCRIPT =
            "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";

    private final StringRedisTemplate redisTemplate;

    @Override
    public void execute(String resourceKey, Runnable action) {
        String lockKey = LOCK_PREFIX + resourceKey;
        String token = UUID.randomUUID().toString();
        boolean acquired = Boolean.TRUE.equals(
                redisTemplate.opsForValue().setIfAbsent(lockKey, token, EXPIRE));
        if (!acquired) {
            throw new InventoryDomainException("获取分布式锁失败: " + resourceKey);
        }
        try {
            action.run();
        } finally {
            release(lockKey, token);
        }
    }

    private void release(String lockKey, String token) {
        DefaultRedisScript<Long> script = new DefaultRedisScript<>(RELEASE_SCRIPT, Long.class);
        redisTemplate.execute(script, Collections.singletonList(lockKey), token);
    }
}