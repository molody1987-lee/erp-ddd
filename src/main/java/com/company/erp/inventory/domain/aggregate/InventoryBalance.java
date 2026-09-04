package com.company.erp.inventory.domain.aggregate;

import com.company.erp.inventory.domain.exception.InventoryDomainException;
import com.company.erp.inventory.domain.valueobject.InventoryQuantity;

import lombok.AccessLevel;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

/**
 * 库存结存聚合根。
 *
 * <p>维护现存量与移动加权平均成本。
 */
@Getter
public class InventoryBalance {

    private Long id;
    private Long materialId;
    private Long orgId;
    @Getter(AccessLevel.NONE)
    private InventoryQuantity quantityOnHand;
    private BigDecimal unitCost;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    protected InventoryBalance() {
    }

    public static InventoryBalance create(Long materialId, Long orgId, String unit) {
        if (materialId == null || materialId <= 0) {
            throw new InventoryDomainException("物料ID不能为空");
        }
        if (orgId == null || orgId <= 0) {
            throw new InventoryDomainException("组织ID不能为空");
        }
        if (unit == null || unit.isBlank()) {
            throw new InventoryDomainException("库存单位不能为空");
        }
        InventoryBalance balance = new InventoryBalance();
        balance.materialId = materialId;
        balance.orgId = orgId;
        balance.quantityOnHand = InventoryQuantity.of(BigDecimal.ZERO, unit);
        balance.unitCost = BigDecimal.ZERO.setScale(4, RoundingMode.HALF_UP);
        balance.createdAt = LocalDateTime.now();
        balance.updatedAt = balance.createdAt;
        return balance;
    }

    public static InventoryBalance reconstitute(Long id, Long materialId, Long orgId,
                                                InventoryQuantity quantityOnHand, BigDecimal unitCost,
                                                LocalDateTime createdAt, LocalDateTime updatedAt) {
        InventoryBalance balance = new InventoryBalance();
        balance.id = id;
        balance.materialId = materialId;
        balance.orgId = orgId;
        balance.quantityOnHand = quantityOnHand;
        balance.unitCost = unitCost;
        balance.createdAt = createdAt;
        balance.updatedAt = updatedAt;
        return balance;
    }

    /**
     * 入库：按移动加权平均重新计算单位成本。
     */
    public void receive(InventoryQuantity quantity, BigDecimal unitCost) {
        if (quantity == null || quantity.value().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InventoryDomainException("入库数量必须大于0");
        }
        if (unitCost == null || unitCost.compareTo(BigDecimal.ZERO) < 0) {
            throw new InventoryDomainException("入库单价不能为空且不能为负");
        }
        if (!this.quantityOnHand.unit().equals(quantity.unit())) {
            throw new InventoryDomainException("入库单位与库存单位不一致");
        }
        BigDecimal oldTotal = this.quantityOnHand.value().multiply(this.unitCost);
        BigDecimal inTotal = quantity.value().multiply(unitCost);
        BigDecimal newQuantity = this.quantityOnHand.value().add(quantity.value());
        this.unitCost = oldTotal.add(inTotal).divide(newQuantity, 4, RoundingMode.HALF_UP);
        this.quantityOnHand = InventoryQuantity.of(newQuantity, this.quantityOnHand.unit());
        this.updatedAt = LocalDateTime.now();
    }

    public void assignId(Long id) {
        this.id = id;
    }

    public BigDecimal getQuantityOnHand() {
        return quantityOnHand.value();
    }

    public String getUnit() {
        return quantityOnHand.unit();
    }
}