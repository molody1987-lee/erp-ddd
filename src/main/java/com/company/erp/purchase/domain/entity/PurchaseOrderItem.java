package com.company.erp.purchase.domain.entity;

import com.company.erp.purchase.domain.exception.PurchaseDomainException;
import com.company.erp.purchase.domain.valueobject.MaterialId;
import com.company.erp.purchase.domain.valueobject.Money;
import com.company.erp.purchase.domain.valueobject.Quantity;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * 采购订单明细实体。
 */
@Getter
public class PurchaseOrderItem {

    private Long id;
    private MaterialId materialId;
    private Quantity quantity;
    private Money unitPrice;
    private Quantity receivedQuantity;

    protected PurchaseOrderItem() {
    }

    private PurchaseOrderItem(Long id, MaterialId materialId, Quantity quantity, Money unitPrice,
                              Quantity receivedQuantity) {
        this.id = id;
        this.materialId = materialId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.receivedQuantity = receivedQuantity;
    }

    public static PurchaseOrderItem create(MaterialId materialId, Quantity quantity, Money unitPrice) {
        if (materialId == null) {
            throw new PurchaseDomainException("物料ID不能为空");
        }
        if (quantity == null || quantity.value().compareTo(BigDecimal.ZERO) <= 0) {
            throw new PurchaseDomainException("采购数量必须大于0");
        }
        if (unitPrice == null || unitPrice.amount().compareTo(BigDecimal.ZERO) < 0) {
            throw new PurchaseDomainException("采购单价不能为负");
        }
        return new PurchaseOrderItem(null, materialId, quantity, unitPrice,
                new Quantity(BigDecimal.ZERO, quantity.unit()));
    }

    public static PurchaseOrderItem reconstitute(Long id, MaterialId materialId, Quantity quantity,
                                                 Money unitPrice, Quantity receivedQuantity) {
        return new PurchaseOrderItem(id, materialId, quantity, unitPrice, receivedQuantity);
    }

    /**
     * 累加入库数量；校验不超过采购数量。
     */
    public void receive(Quantity received) {
        if (received == null) {
            throw new PurchaseDomainException("入库数量不能为空");
        }
        Quantity newReceived = this.receivedQuantity.add(received);
        if (newReceived.value().compareTo(this.quantity.value()) > 0) {
            throw new PurchaseDomainException("入库数量超过采购数量");
        }
        this.receivedQuantity = newReceived;
    }

    /**
     * 明细行含税总额。
     */
    public Money amount() {
        return this.unitPrice.multiply(this.quantity);
    }

    public void assignId(Long id) {
        this.id = id;
    }
}