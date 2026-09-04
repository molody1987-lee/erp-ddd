package com.company.erp.inventory.infrastructure.cache;

import com.company.erp.inventory.domain.exception.InventoryDomainException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RedisInventoryDistributedLockTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private RedisInventoryDistributedLock lock;

    @Test
    void 获取锁成功应执行操作并使用lock前缀键() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.setIfAbsent(anyString(), anyString(), eq(5L), eq(TimeUnit.SECONDS)))
                .thenReturn(Boolean.TRUE);

        AtomicBoolean executed = new AtomicBoolean(false);
        lock.execute("inventory:material_1_100", () -> executed.set(true));

        assertThat(executed).isTrue();

        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        verify(valueOperations).setIfAbsent(keyCaptor.capture(), anyString(), eq(5L), eq(TimeUnit.SECONDS));
        assertThat(keyCaptor.getValue()).isEqualTo("lock:inventory:material_1_100");
    }

    @Test
    void 获取锁失败应抛出异常且不执行操作() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.setIfAbsent(anyString(), anyString(), eq(5L), eq(TimeUnit.SECONDS)))
                .thenReturn(Boolean.FALSE);

        AtomicBoolean executed = new AtomicBoolean(false);

        assertThatThrownBy(() -> lock.execute("inventory:material_1_100", () -> executed.set(true)))
                .isInstanceOf(InventoryDomainException.class);
        assertThat(executed).isFalse();
    }
}