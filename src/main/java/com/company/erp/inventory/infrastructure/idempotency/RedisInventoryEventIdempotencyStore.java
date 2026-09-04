package com.company.erp.inventory.infrastructure.idempotency;

import com.company.erp.inventory.domain.service.InventoryEventIdempotencyStore;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

/**
 * 基于 Redis 的库存事件幂等存储实现。
 */
@Repository
@RequiredArgsConstructor
public class RedisInventoryEventIdempotencyStore implements InventoryEventIdempotencyStore {

    private static final String PREFIX = "inventory:event:idempotency:";
    private static final Duration TTL = Duration.ofHours(24);

    private final StringRedisTemplate redisTemplate;

    @Override
    public boolean markIfAbsent(String eventId) {
        Boolean ok = redisTemplate.opsForValue().setIfAbsent(PREFIX + eventId, "1", TTL);
        return Boolean.TRUE.equals(ok);
    }

    @Override
    public void remove(String eventId) {
        redisTemplate.delete(PREFIX + eventId);
    }
}