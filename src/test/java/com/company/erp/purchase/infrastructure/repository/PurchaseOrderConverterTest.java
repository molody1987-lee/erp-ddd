package com.company.erp.purchase.infrastructure.repository;

import com.company.erp.purchase.application.dto.PurchaseOrderDTO;
import com.company.erp.purchase.domain.aggregate.PurchaseOrder;
import com.company.erp.purchase.domain.entity.PurchaseOrderItem;
import com.company.erp.purchase.domain.valueobject.MaterialId;
import com.company.erp.purchase.domain.valueobject.Money;
import com.company.erp.purchase.domain.valueobject.PurchaseOrderId;
import com.company.erp.purchase.domain.valueobject.PurchaseOrderStatus;
import com.company.erp.purchase.domain.valueobject.Quantity;
import com.company.erp.purchase.infrastructure.persistence.PurchaseOrderItemPO;
import com.company.erp.purchase.infrastructure.persistence.PurchaseOrderPO;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PurchaseOrderConverterTest {

    private final PurchaseOrderConverter converter = new PurchaseOrderConverter();

    private PurchaseOrder newOrder() {
        PurchaseOrder order = PurchaseOrder.create("PO-1", 10L, 1L, List.of(
                PurchaseOrderItem.create(new MaterialId(100L),
                        new Quantity(new BigDecimal("10"), "PCS"),
                        new Money(new BigDecimal("5.00"), "CNY"))));
        order.assignId(new PurchaseOrderId(99L));
        return order;
    }

    @Test
    void shouldConvertOrderToPoPreservingFields() {
        PurchaseOrder order = newOrder();

        PurchaseOrderPO po = converter.toPO(order);

        assertThat(po.getId()).isEqualTo(99L);
        assertThat(po.getOrderCode()).isEqualTo("PO-1");
        assertThat(po.getSupplierId()).isEqualTo(10L);
        assertThat(po.getOrgId()).isEqualTo(1L);
        assertThat(po.getStatus()).isEqualTo(PurchaseOrderStatus.DRAFT.getCode());
        assertThat(po.getCreateTime()).isNotNull();
    }

    @Test
    void shouldConvertItemToPoWithOrderId() {
        PurchaseOrder order = newOrder();

        PurchaseOrderItemPO po = converter.toItemPO(order.getItems().get(0), 99L);

        assertThat(po.getOrderId()).isEqualTo(99L);
        assertThat(po.getMaterialId()).isEqualTo(100L);
        assertThat(po.getQuantity()).isEqualByComparingTo(new BigDecimal("10"));
        assertThat(po.getUnit()).isEqualTo("PCS");
        assertThat(po.getUnitPrice()).isEqualByComparingTo(new BigDecimal("5.00"));
        assertThat(po.getCurrency()).isEqualTo("CNY");
        assertThat(po.getReceivedQuantity()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void shouldConvertPoToDomainAggregate() {
        PurchaseOrder order = newOrder();
        PurchaseOrderPO po = converter.toPO(order);
        PurchaseOrderItemPO itemPO = converter.toItemPO(order.getItems().get(0), po.getId());

        PurchaseOrder domain = converter.toDomain(po, List.of(itemPO));

        assertThat(domain.getId()).isEqualTo(new PurchaseOrderId(99L));
        assertThat(domain.getStatus()).isEqualTo(PurchaseOrderStatus.DRAFT);
        assertThat(domain.getItems()).hasSize(1);
        assertThat(domain.getItems().get(0).getMaterialId()).isEqualTo(new MaterialId(100L));
    }

    @Test
    void shouldConvertPoToDtoReadModel() {
        PurchaseOrder order = newOrder();
        PurchaseOrderPO po = converter.toPO(order);
        PurchaseOrderItemPO itemPO = converter.toItemPO(order.getItems().get(0), po.getId());

        PurchaseOrderDTO dto = converter.toDTO(po, List.of(itemPO));

        assertThat(dto.id()).isEqualTo(99L);
        assertThat(dto.orderCode()).isEqualTo("PO-1");
        assertThat(dto.status()).isEqualTo("DRAFT");
        assertThat(dto.items()).hasSize(1);
        assertThat(dto.items().get(0).materialId()).isEqualTo(100L);
    }

    @Test
    void shouldFailOnInvalidStatusCode() {
        PurchaseOrderPO po = new PurchaseOrderPO();
        po.setId(1L);
        po.setOrderCode("PO-X");
        po.setSupplierId(10L);
        po.setOrgId(1L);
        po.setStatus(99);
        po.setCreateTime(LocalDateTime.now());
        po.setUpdateTime(LocalDateTime.now());

        org.assertj.core.api.Assertions.assertThatThrownBy(() -> converter.toDomain(po, List.of()))
                .isInstanceOf(IllegalArgumentException.class);
    }
}