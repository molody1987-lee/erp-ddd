package com.company.erp.inventory.application.acl;

import com.company.erp.purchase.domain.event.PurchaseOrderReceivedEvent;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PurchaseOrderReceivedEventTranslatorTest {

    private final PurchaseOrderReceivedEventTranslator translator = new PurchaseOrderReceivedEventTranslator();

    @Test
    void shouldTranslateEventToReceiveItems() {
        PurchaseOrderReceivedEvent event = new PurchaseOrderReceivedEvent("evt-1", 1L, 200L,
                List.of(new PurchaseOrderReceivedEvent.Item(100L, new BigDecimal("10"), "PCS",
                        new BigDecimal("5.00"))),
                LocalDateTime.now());

        List<InventoryReceiveItem> items = translator.translate(event);

        assertThat(items).hasSize(1);
        assertThat(items.get(0).materialId()).isEqualTo(100L);
        assertThat(items.get(0).quantity().value()).isEqualByComparingTo(new BigDecimal("10"));
        assertThat(items.get(0).quantity().unit()).isEqualTo("PCS");
        assertThat(items.get(0).unitCost()).isEqualByComparingTo(new BigDecimal("5.00"));
    }

    @Test
    void shouldTranslateMultipleItems() {
        PurchaseOrderReceivedEvent event = new PurchaseOrderReceivedEvent("evt-2", 1L, 200L,
                List.of(
                        new PurchaseOrderReceivedEvent.Item(100L, new BigDecimal("10"), "PCS", new BigDecimal("5.00")),
                        new PurchaseOrderReceivedEvent.Item(101L, new BigDecimal("2"), "KG", new BigDecimal("8.00"))),
                LocalDateTime.now());

        List<InventoryReceiveItem> items = translator.translate(event);

        assertThat(items).hasSize(2);
        assertThat(items.get(1).materialId()).isEqualTo(101L);
        assertThat(items.get(1).quantity().unit()).isEqualTo("KG");
    }
}