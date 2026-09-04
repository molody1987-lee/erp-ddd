package com.company.erp.inventory.infrastructure.idempotency;

import com.company.erp.support.AbstractIntegrationTest;
import com.company.erp.support.PersistenceTestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = PersistenceTestConfig.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
class RedisInventoryEventIdempotencyStoreIT extends AbstractIntegrationTest {

    @Autowired
    private RedisInventoryEventIdempotencyStore store;

    @Test
    void shouldMarkOnceRejectDuplicateAndAllowReuseAfterRemoval() {
        String eventId = "evt-" + UUID.randomUUID();

        assertThat(store.markIfAbsent(eventId)).isTrue();
        assertThat(store.markIfAbsent(eventId)).isFalse();

        store.remove(eventId);

        assertThat(store.markIfAbsent(eventId)).isTrue();
    }
}