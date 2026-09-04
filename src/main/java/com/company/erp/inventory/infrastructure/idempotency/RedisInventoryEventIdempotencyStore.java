package com.company.erp.inventory.infrastructure.idempotency;

import com.company.erp.inventory.application.port.InventoryEventIdempotencyStore;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 基于 Redis 的事件幂等实现，键规范 inventory:event:{eventId}。
 */
@Component
@RequiredArgsConstructor
public class RedisInventoryEventIdempotencyStore implements InventoryEventIdempotencyStore {

    private static final long TTL_SECONDS = 86400L;

    private final StringRedisTemplate redisTemplate;

    @Override
    public boolean markIfAbsent(String eventId) {
        String key = "inventory:event:" + eventId;
        Boolean ok = redisTemplate.opsForValue()
                .setIfAbsent(key, "1", TTL_SECONDS, TimeUnit.SECONDS);
        return Boolean.TRUE.equals(ok);
    }

    @Override
    public void remove(String eventId) {
        redisTemplate.delete("inventory:event:" + eventId);
    }
}