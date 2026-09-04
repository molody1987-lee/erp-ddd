package com.company.erp.inventory.infrastructure.cache;

import com.company.erp.inventory.domain.exception.InventoryDomainException;
import com.company.erp.support.AbstractIntegrationTest;
import com.company.erp.support.PersistenceTestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(classes = PersistenceTestConfig.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
class RedisInventoryDistributedLockIT extends AbstractIntegrationTest {

    @Autowired
    private RedisInventoryDistributedLock lock;
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    void shouldExecuteAndReleaseWhenLockAcquired() {
        AtomicBoolean ran = new AtomicBoolean(false);

        lock.execute("material_1_100", () -> ran.set(true));

        assertThat(ran).isTrue();
    }

    @Test
    void shouldThrowWhenLockHeldByOther() {
        String resourceKey = "material_1_101";
        redisTemplate.opsForValue().setIfAbsent("lock:inventory:" + resourceKey, "other-token", 5, TimeUnit.SECONDS);

        assertThatThrownBy(() -> lock.execute(resourceKey, () -> { }))
                .isInstanceOf(InventoryDomainException.class);
    }
}