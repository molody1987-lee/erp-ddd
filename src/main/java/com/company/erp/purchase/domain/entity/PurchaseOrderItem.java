package com.company.erp.purchase.domain.entity;

import com.company.erp.purchase.domain.exception.DomainException;
import com.company.erp.purchase.domain.valueobject.MaterialId;
import com.company.erp.purchase.domain.valueobject.Money;
import com.company.erp.purchase.domain.valueobject.Quantity;
import lombok.Getter;

/**
 * 采购订单明细实体（属于 {@code PurchaseOrder} 聚合）。
 */
@Getter
public class PurchaseOrderItem {

    private Long id;
    private final MaterialId materialId;
    private final Quantity quantity;
    private final Money unitPrice;
    private Quantity receivedQuantity;

    public PurchaseOrderItem(MaterialId materialId, Quantity quantity, Money unitPrice) {
        if (materialId == null || quantity == null || unitPrice == null) {
            throw new DomainException("采购明细行字段不能为空");
        }
        if (!quantity.isPositive()) {
            throw new DomainException("采购数量必须为正数");
        }
        this.materialId = materialId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.receivedQuantity = Quantity.zero(quantity.unit());
    }

    /** 用于从持久化恢复明细行。 */
    public PurchaseOrderItem(Long id, MaterialId materialId, Quantity quantity,
                             Money unitPrice, Quantity receivedQuantity) {
        this(materialId, quantity, unitPrice);
        this.id = id;
        this.receivedQuantity = receivedQuantity == null ? Quantity.zero(quantity.unit()) : receivedQuantity;
    }

    public Money amount() {
        return unitPrice.multiply(quantity.value());
    }

    public void receive(Quantity received) {
        if (received == null || !received.isPositive()) {
            throw new DomainException("入库数量必须为正数");
        }
        Quantity sum = this.receivedQuantity.add(received);
        if (sum.value().compareTo(this.quantity.value()) > 0) {
            throw new DomainException("入库数量超过采购数量，物料: " + materialId.value());
        }
        this.receivedQuantity = sum;
    }

    public boolean isFullyReceived() {
        return this.receivedQuantity.value().compareTo(this.quantity.value()) >= 0;
    }
}