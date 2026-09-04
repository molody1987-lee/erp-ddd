package com.company.erp.inventory.infrastructure.cache;

import com.company.erp.inventory.application.port.InventoryDistributedLock;
import com.company.erp.inventory.domain.exception.InventoryDomainException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 基于 Redis 的库存分布式锁实现，锁名遵循 lock:{resource}:{key} 规范。
 * <p>
 * 使用「SET NX EX + 令牌」加锁，解锁通过 Lua 脚本比对令牌后删除，避免误删他人持有的锁。
 */
@Component
@RequiredArgsConstructor
public class RedisInventoryDistributedLock implements InventoryDistributedLock {

    private static final String LOCK_PREFIX = "lock:";
    private static final long LOCK_TIMEOUT_SECONDS = 5;

    /** 仅当值等于自身令牌时才删除，保证超时后不会误删其他持有者的锁。 */
    private static final DefaultRedisScript<Long> UNLOCK_SCRIPT = new DefaultRedisScript<>(
            "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end",
            Long.class);

    private final StringRedisTemplate redisTemplate;

    @Override
    public void execute(String resourceKey, Runnable action) {
        String lockKey = LOCK_PREFIX + resourceKey;
        String token = UUID.randomUUID().toString();
        Boolean acquired = redisTemplate.opsForValue()
                .setIfAbsent(lockKey, token, LOCK_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        if (!Boolean.TRUE.equals(acquired)) {
            // 获取锁失败说明同一物料/组织正在被并发处理，抛出异常触发 RocketMQ 重投
            throw new InventoryDomainException("库存操作繁忙，请稍后重试");
        }
        try {
            action.run();
        } finally {
            redisTemplate.execute(UNLOCK_SCRIPT, List.of(lockKey), token);
        }
    }
}