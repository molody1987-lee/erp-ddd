package com.company.erp.purchase.domain.aggregate;

import com.company.erp.purchase.domain.entity.PurchaseOrderItem;
import com.company.erp.purchase.domain.exception.PurchaseDomainException;
import com.company.erp.purchase.domain.valueobject.MaterialId;
import com.company.erp.purchase.domain.valueobject.PurchaseOrderId;
import com.company.erp.purchase.domain.valueobject.PurchaseOrderStatus;
import com.company.erp.purchase.domain.valueobject.ReceivedQuantity;
import lombok.AccessLevel;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * 采购订单聚合根。
 *
 * <p>领域职责：维护订单状态流转与入库数量不变量。
 */
@Getter
public class PurchaseOrder {

    private PurchaseOrderId id;
    private String orderCode;
    private Long supplierId;
    private Long orgId;
    private PurchaseOrderStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @Getter(AccessLevel.NONE)
    private final List<PurchaseOrderItem> items = new ArrayList<>();

    protected PurchaseOrder() {
    }

    public static PurchaseOrder create(String orderCode, Long supplierId, Long orgId,
                                       List<PurchaseOrderItem> items) {
        if (orderCode == null || orderCode.isBlank()) {
            throw new PurchaseDomainException("订单编号不能为空");
        }
        if (supplierId == null || supplierId <= 0) {
            throw new PurchaseDomainException("供应商ID不能为空");
        }
        if (orgId == null || orgId <= 0) {
            throw new PurchaseDomainException("组织ID不能为空");
        }
        if (items == null || items.isEmpty()) {
            throw new PurchaseDomainException("采购订单必须包含明细");
        }
        PurchaseOrder order = new PurchaseOrder();
        order.orderCode = orderCode;
        order.supplierId = supplierId;
        order.orgId = orgId;
        order.status = PurchaseOrderStatus.DRAFT;
        order.createdAt = LocalDateTime.now();
        order.updatedAt = order.createdAt;
        order.items.addAll(items);
        return order;
    }

    public static PurchaseOrder reconstitute(PurchaseOrderId id, String orderCode, Long supplierId, Long orgId,
                                             PurchaseOrderStatus status, List<PurchaseOrderItem> items,
                                             LocalDateTime createdAt, LocalDateTime updatedAt) {
        PurchaseOrder order = new PurchaseOrder();
        order.id = id;
        order.orderCode = orderCode;
        order.supplierId = supplierId;
        order.orgId = orgId;
        order.status = status;
        order.createdAt = createdAt;
        order.updatedAt = updatedAt;
        order.items.addAll(items);
        return order;
    }

    /**
     * 审核：只有草稿状态可审核。
     */
    public void review(Long reviewerId) {
        if (reviewerId == null || reviewerId <= 0) {
            throw new PurchaseDomainException("审核人不能为空");
        }
        if (this.status != PurchaseOrderStatus.DRAFT) {
            throw new PurchaseDomainException("只有草稿状态的订单才能审核");
        }
        this.status = PurchaseOrderStatus.REVIEWED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 入库：只有已审核状态可入库，累加明细已入库数量。
     */
    public void receive(List<ReceivedQuantity> receivedItems) {
        if (this.status != PurchaseOrderStatus.REVIEWED) {
            throw new PurchaseDomainException("只有已审核的订单才能入库");
        }
        if (receivedItems == null || receivedItems.isEmpty()) {
            throw new PurchaseDomainException("入库明细不能为空");
        }
        for (ReceivedQuantity received : receivedItems) {
            PurchaseOrderItem item = findItem(received.materialId())
                    .orElseThrow(() -> new PurchaseDomainException(
                            "物料不在采购订单中: " + received.materialId().value()));
            item.receive(received.quantity());
        }
        if (isFullyReceived()) {
            this.status = PurchaseOrderStatus.RECEIVED;
        }
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 修改：只有草稿状态可修改，可变更订单编号、供应商与明细。
     */
    public void modify(String orderCode, Long supplierId, List<PurchaseOrderItem> newItems) {
        if (this.status != PurchaseOrderStatus.DRAFT) {
            throw new PurchaseDomainException("只有草稿状态的订单才能修改");
        }
        if (orderCode == null || orderCode.isBlank()) {
            throw new PurchaseDomainException("订单编号不能为空");
        }
        if (supplierId == null || supplierId <= 0) {
            throw new PurchaseDomainException("供应商ID不能为空");
        }
        if (newItems == null || newItems.isEmpty()) {
            throw new PurchaseDomainException("采购订单必须包含明细");
        }
        this.orderCode = orderCode;
        this.supplierId = supplierId;
        this.items.clear();
        this.items.addAll(newItems);
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 关闭：已入库订单不可关闭。
     */
    public void close() {
        if (this.status == PurchaseOrderStatus.RECEIVED) {
            throw new PurchaseDomainException("已入库订单不能关闭");
        }
        this.status = PurchaseOrderStatus.CLOSED;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isFullyReceived() {
        return items.stream()
                .allMatch(item -> item.getReceivedQuantity().value()
                        .compareTo(item.getQuantity().value()) >= 0);
    }

    private Optional<PurchaseOrderItem> findItem(MaterialId materialId) {
        return items.stream()
                .filter(item -> item.getMaterialId().equals(materialId))
                .findFirst();
    }

    public void assignId(PurchaseOrderId id) {
        this.id = id;
    }

    public List<PurchaseOrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }
}