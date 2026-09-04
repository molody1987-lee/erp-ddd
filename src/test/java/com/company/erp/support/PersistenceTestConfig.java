package com.company.erp.support;

import com.company.erp.inventory.infrastructure.cache.RedisInventoryDistributedLock;
import com.company.erp.inventory.infrastructure.idempotency.RedisInventoryEventIdempotencyStore;
import com.company.erp.inventory.infrastructure.repository.InventoryBalanceConverter;
import com.company.erp.inventory.infrastructure.repository.InventoryBalanceMapper;
import com.company.erp.inventory.infrastructure.repository.InventoryBalanceRepositoryImpl;
import com.company.erp.purchase.infrastructure.repository.PurchaseOrderConverter;
import com.company.erp.purchase.infrastructure.repository.PurchaseOrderItemMapper;
import com.company.erp.purchase.infrastructure.repository.PurchaseOrderMapper;
import com.company.erp.purchase.infrastructure.repository.PurchaseOrderRepositoryImpl;
import org.apache.rocketmq.spring.autoconfigure.RocketMQAutoConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * 集成测试配置：装配必要 Bean，排除 RocketMQ 自动配置。
 */
@SpringBootConfiguration
@EnableAutoConfiguration(exclude = RocketMQAutoConfiguration.class)
@MapperScan("com.company.erp")
public class PersistenceTestConfig {

    @Bean
    public InventoryBalanceConverter inventoryBalanceConverter() {
        return new InventoryBalanceConverter();
    }

    @Bean
    public InventoryBalanceRepositoryImpl inventoryBalanceRepository(InventoryBalanceMapper mapper) {
        return new InventoryBalanceRepositoryImpl(mapper, inventoryBalanceConverter());
    }

    @Bean
    public PurchaseOrderConverter purchaseOrderConverter() {
        return new PurchaseOrderConverter();
    }

    @Bean
    public PurchaseOrderRepositoryImpl purchaseOrderRepository(PurchaseOrderMapper orderMapper,
                                                               PurchaseOrderItemMapper itemMapper) {
        return new PurchaseOrderRepositoryImpl(orderMapper, itemMapper, purchaseOrderConverter());
    }

    @Bean
    public RedisInventoryEventIdempotencyStore redisInventoryEventIdempotencyStore(StringRedisTemplate redisTemplate) {
        return new RedisInventoryEventIdempotencyStore(redisTemplate);
    }

    @Bean
    public RedisInventoryDistributedLock redisInventoryDistributedLock(StringRedisTemplate redisTemplate) {
        return new RedisInventoryDistributedLock(redisTemplate);
    }
}