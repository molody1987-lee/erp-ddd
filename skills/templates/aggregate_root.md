```java
// 库存上下文的聚合根
public class InventoryTransaction {
    private InventoryTransactionId id;
    private MaterialId materialId;
    private OrganizationId orgId;
    private Quantity quantity;          // 正数=入库，负数=出库
    private MoveType moveType;          // 入库、出库、盘盈、盘亏
    private BigDecimal unitCost;        // 入库单价 / 出库时的加权平均价
    private LocalDateTime occurredOn;

    // 业务行为：执行入库（重新计算加权平均）
    public InventoryTransaction receiveInventory(
        MaterialId materialId,
        Quantity incomingQty,
        BigDecimal incomingCost,
        InventoryAggregate inventoryAggregate
    ) {
        // 1. 获取当前库存数量与总金额
        BigDecimal currentQty = inventoryAggregate.getCurrentQuantity();
        BigDecimal currentTotalCost = inventoryAggregate.getCurrentTotalCost();

        // 2. 计算新的加权平均成本
        BigDecimal newTotalQty = currentQty.add(incomingQty.getValue());
        BigDecimal newTotalCost = currentTotalCost.add(incomingQty.getValue().multiply(incomingCost));
        BigDecimal newAvgCost = newTotalQty.compareTo(BigDecimal.ZERO) == 0
            ? BigDecimal.ZERO
            : newTotalCost.divide(newTotalQty, 4, RoundingMode.HALF_UP);

        // 3. 创建库存移动记录
        return InventoryTransaction.builder()
            .materialId(materialId)
            .quantity(incomingQty)
            .moveType(MoveType.RECEIPT)   // 入库
            .unitCost(incomingCost)       // 入库时记录原价，同时更新聚合成本
            .occurredOn(LocalDateTime.now())
            .build();
    }
}