package com.company.erp.purchase.domain.event;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SuppressWarnings("ConstantConditions")
class PurchaseOrderReceivedEventTest {

    @Test
    void shouldRejectNonPositiveQuantity() {
        assertThatThrownBy(() -> new PurchaseOrderReceivedEvent.Item(1L, BigDecimal.ZERO, "PCS", BigDecimal.ONE))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldRejectNullMaterial() {
        assertThatThrownBy(() -> new PurchaseOrderReceivedEvent.Item(null, BigDecimal.ONE, "PCS", BigDecimal.ONE))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldRejectBlankUnit() {
        assertThatThrownBy(() -> new PurchaseOrderReceivedEvent.Item(1L, BigDecimal.ONE, " ", BigDecimal.ONE))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldRejectNegativeUnitPrice() {
        assertThatThrownBy(() -> new PurchaseOrderReceivedEvent.Item(1L, BigDecimal.ONE, "PCS", new BigDecimal("-1")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldExposeAllFields() {
        PurchaseOrderReceivedEvent.Item item =
                new PurchaseOrderReceivedEvent.Item(100L, new BigDecimal("10"), "PCS", new BigDecimal("5.00"));
        PurchaseOrderReceivedEvent event = new PurchaseOrderReceivedEvent(
                "evt-1", 1L, 200L, List.of(item), LocalDateTime.now());

        assertThat(event.eventId()).isEqualTo("evt-1");
        assertThat(event.orgId()).isEqualTo(1L);
        assertThat(event.purchaseOrderId()).isEqualTo(200L);
        assertThat(event.items()).hasSize(1);
        assertThat(event.occurredAt()).isNotNull();
    }
}