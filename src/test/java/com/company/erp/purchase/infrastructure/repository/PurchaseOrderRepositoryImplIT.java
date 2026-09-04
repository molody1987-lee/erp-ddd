package com.company.erp.purchase.infrastructure.repository;

import com.company.erp.purchase.domain.aggregate.PurchaseOrder;
import com.company.erp.purchase.domain.entity.PurchaseOrderItem;
import com.company.erp.purchase.domain.valueobject.MaterialId;
import com.company.erp.purchase.domain.valueobject.Money;
import com.company.erp.purchase.domain.valueobject.PurchaseOrderId;
import com.company.erp.purchase.domain.valueobject.PurchaseOrderStatus;
import com.company.erp.purchase.domain.valueobject.Quantity;
import com.company.erp.purchase.domain.valueobject.ReceivedQuantity;
import com.company.erp.support.AbstractIntegrationTest;
import com.company.erp.support.PersistenceTestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = PersistenceTestConfig.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
class PurchaseOrderRepositoryImplIT extends AbstractIntegrationTest {

    @Autowired
    private PurchaseOrderRepositoryImpl repository;

    @Test
    void shouldSaveOrderAndReadBackWithItems() {
        PurchaseOrder order = newOrder("PO-IT-001");

        PurchaseOrderId id = repository.save(order);

        assertThat(id).isNotNull();
        Optional<PurchaseOrder> found = repository.findById(id);
        assertThat(found).isPresent();
        assertThat(found.orElseThrow().getStatus()).isEqualTo(PurchaseOrderStatus.DRAFT);
        assertThat(found.orElseThrow().getItems()).hasSize(1);
    }

    @Test
    void shouldUpdateItemsAndRecalculateStatus() {
        PurchaseOrder order = newOrder("PO-IT-002");
        PurchaseOrderId id = repository.save(order);
        order.review(9L);
        repository.save(order);

        order.receive(List.of(new ReceivedQuantity(
                new MaterialId(100L), new Quantity(new BigDecimal("10"), "PCS"))));
        repository.save(order);

        Optional<PurchaseOrder> found = repository.findById(id);
        assertThat(found.orElseThrow().getStatus()).isEqualTo(PurchaseOrderStatus.RECEIVED);
        assertThat(found.orElseThrow().getItems().get(0).getReceivedQuantity().value())
                .isEqualByComparingTo(new BigDecimal("10"));
    }

    @Test
    void shouldReturnEmptyForMissingOrder() {
        assertThat(repository.findById(new PurchaseOrderId(999999999L))).isEmpty();
    }

    private PurchaseOrder newOrder(String code) {
        PurchaseOrderItem item = PurchaseOrderItem.create(
                new MaterialId(100L),
                new Quantity(new BigDecimal("10"), "PCS"),
                new Money(new BigDecimal("5.00"), "CNY"));
        return PurchaseOrder.create(code, 10L, 1L, List.of(item));
    }
}