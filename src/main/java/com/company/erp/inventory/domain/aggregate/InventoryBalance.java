package com.company.erp.inventory.domain.aggregate;

import com.company.erp.inventory.domain.exception.InventoryDomainException;
import com.company.erp.inventory.domain.valueobject.InventoryQuantity;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

/**
 * 库存结存聚合根，是库存上下文外部访问的唯一入口。
 * <p>
 * 采用移动加权平均成本：每次入库时按「(原结存总成本 + 本次入库成本) / (原结存数量 + 本次入库数量)」
 * 重新计算平均成本。
 */
@Getter
public class InventoryBalance {

    private Long id;
    private final Long materialId;
    private final Long orgId;
    private final String unit;
    private final String currency;
    private BigDecimal quantityOnHand;
    private BigDecimal totalCost;
    private final LocalDateTime createTime;
    private LocalDateTime updateTime;

    private InventoryBalance(Long id, Long materialId, Long orgId, String unit, String currency,
                             BigDecimal quantityOnHand, BigDecimal totalCost,
                             LocalDateTime createTime, LocalDateTime updateTime) {
        this.id = id;
        this.materialId = materialId;
        this.orgId = orgId;
        this.unit = unit;
        this.currency = currency;
        this.quantityOnHand = quantityOnHand;
        this.totalCost = totalCost;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    /** 新建结存（首次入库时使用，初始数量与总成本为零）。 */
    public static InventoryBalance create(Long materialId, Long orgId, String unit, String currency) {
        if (materialId == null || materialId <= 0) {
            throw new InventoryDomainException("物料ID非法");
        }
        if (orgId == null || orgId <= 0) {
            throw new InventoryDomainException("组织ID非法");
        }
        if (unit == null || unit.isBlank()) {
            throw new InventoryDomainException("数量单位不能为空");
        }
        if (currency == null || currency.isBlank()) {
            throw new InventoryDomainException("币种不能为空");
        }
        return new InventoryBalance(null, materialId, orgId, unit, currency,
                BigDecimal.ZERO, BigDecimal.ZERO, LocalDateTime.now(), LocalDateTime.now());
    }

    /** 从持久化恢复聚合。 */
    public static InventoryBalance reconstruct(Long id, Long materialId, Long orgId, String unit, String currency,
                                               BigDecimal quantityOnHand, BigDecimal totalCost,
                                               LocalDateTime createTime, LocalDateTime updateTime) {
        return new InventoryBalance(id, materialId, orgId, unit, currency,
                quantityOnHand, totalCost, createTime, updateTime);
    }

    /** 持久化时回填雪花 ID。 */
    public void assignId(Long id) {
        if (this.id != null) {
            throw new InventoryDomainException("结存ID已分配");
        }
        this.id = id;
    }

    /** 采购入库：增加结存并重算移动加权平均成本。 */
    public void receive(InventoryQuantity incoming, BigDecimal unitCost) {
        if (incoming == null || !incoming.isPositive()) {
            throw new InventoryDomainException("入库数量必须为正数");
        }
        if (!this.unit.equals(incoming.unit())) {
            throw new InventoryDomainException("数量单位不一致");
        }
        if (unitCost == null || unitCost.compareTo(BigDecimal.ZERO) < 0) {
            throw new InventoryDomainException("入库单价不能为负数");
        }
        this.quantityOnHand = quantityOnHand.add(incoming.value());
        this.totalCost = totalCost.add(incoming.value().multiply(unitCost));
        this.updateTime = LocalDateTime.now();
    }

    /** 当前移动加权平均成本。 */
    public BigDecimal averageCost() {
        if (quantityOnHand.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return totalCost.divide(quantityOnHand, 4, RoundingMode.HALF_UP);
    }
}