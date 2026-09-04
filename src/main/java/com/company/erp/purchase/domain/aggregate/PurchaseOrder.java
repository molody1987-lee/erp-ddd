package com.company.erp.purchase.domain.aggregate;

import com.company.erp.purchase.domain.entity.PurchaseOrderItem;
import com.company.erp.purchase.domain.event.DomainEvent;
import com.company.erp.purchase.domain.event.PurchaseOrderClosedEvent;
import com.company.erp.purchase.domain.event.PurchaseOrderReceivedEvent;
import com.company.erp.purchase.domain.event.PurchaseOrderReviewedEvent;
import com.company.erp.purchase.domain.exception.DomainException;
import com.company.erp.purchase.domain.valueobject.Money;
import com.company.erp.purchase.domain.valueobject.OrganizationId;
import com.company.erp.purchase.domain.valueobject.PurchaseOrderId;
import com.company.erp.purchase.domain.valueobject.PurchaseOrderStatus;
import com.company.erp.purchase.domain.valueobject.ReceivedItem;
import com.company.erp.purchase.domain.valueobject.ReceivedQuantity;
import com.company.erp.purchase.domain.valueobject.SupplierId;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 采购订单聚合根，是采购上下文外部访问的唯一入口。
 * 负责保护状态流转不变量：草稿 → 已审核 →（部分入库）→ 已入库 / 已关闭。
 */
@Getter
public class PurchaseOrder {

    private PurchaseOrderId id;
    private final String orderCode;
    private final SupplierId supplierId;
    private final OrganizationId orgId;
    private final List<PurchaseOrderItem> items;
    private PurchaseOrderStatus status;
    private final Money totalAmount;
    private final String currency;
    private final String remark;
    private final LocalDateTime createTime;
    private final Long createdBy;
    private Long updatedBy;

    /** 聚合内临时记录的领域事件，由应用层在事务提交后发布。 */
    private final List<DomainEvent> events = new ArrayList<>();

    private PurchaseOrder(PurchaseOrderId id, String orderCode, SupplierId supplierId, OrganizationId orgId,
                          List<PurchaseOrderItem> items, PurchaseOrderStatus status, Money totalAmount,
                          String currency, String remark, LocalDateTime createTime, Long createdBy, Long updatedBy) {
        this.id = id;
        this.orderCode = orderCode;
        this.supplierId = supplierId;
        this.orgId = orgId;
        this.items = items;
        this.status = status;
        this.totalAmount = totalAmount;
        this.currency = currency;
        this.remark = remark;
        this.createTime = createTime;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
    }

    /** 创建草稿采购订单。 */
    public static PurchaseOrder create(String orderCode, SupplierId supplierId, OrganizationId orgId,
                                       String currency, List<PurchaseOrderItem> items, String remark, Long createdBy) {
        if (orderCode == null || orderCode.isBlank()) {
            throw new DomainException("订单编码不能为空");
        }
        if (currency == null || currency.isBlank()) {
            throw new DomainException("币种不能为空");
        }
        if (items == null || items.isEmpty()) {
            throw new DomainException("采购订单至少需要一个明细行");
        }
        Money total = items.stream()
                .map(PurchaseOrderItem::amount)
                .reduce(Money.zero(currency), Money::add);
        return new PurchaseOrder(null, orderCode, supplierId, orgId, List.copyOf(items),
                PurchaseOrderStatus.DRAFT, total, currency, remark, LocalDateTime.now(), createdBy, createdBy);
    }

    /** 从持久化恢复聚合，不产生事件。 */
    public static PurchaseOrder reconstruct(PurchaseOrderId id, String orderCode, SupplierId supplierId,
                                            OrganizationId orgId, List<PurchaseOrderItem> items,
                                            PurchaseOrderStatus status, Money totalAmount, String currency,
                                            String remark, LocalDateTime createTime, Long createdBy, Long updatedBy) {
        return new PurchaseOrder(id, orderCode, supplierId, orgId, List.copyOf(items), status, totalAmount,
                currency, remark, createTime, createdBy, updatedBy);
    }

    /** 持久化时由仓储回填雪花 ID。 */
    public void assignId(PurchaseOrderId id) {
        if (this.id != null) {
            throw new DomainException("订单ID已分配");
        }
        this.id = id;
    }

    /** 审核：草稿 → 已审核。 */
    public void review(Long operatorId) {
        requirePersisted();
        if (status != PurchaseOrderStatus.DRAFT) {
            throw new DomainException("只有草稿状态的订单才能审核");
        }
        this.status = PurchaseOrderStatus.REVIEWED;
        this.updatedBy = operatorId;
        recordEvent(new PurchaseOrderReviewedEvent(id.value(), orderCode, operatorId));
    }

    /** 入库：已审核/部分入库 → 部分入库/已入库。 */
    public void receive(List<ReceivedQuantity> receivedItems) {
        requirePersisted();
        if (status != PurchaseOrderStatus.REVIEWED && status != PurchaseOrderStatus.PARTIAL_RECEIVED) {
            throw new DomainException("只有已审核或部分入库状态的订单才能入库");
        }
        if (receivedItems == null || receivedItems.isEmpty()) {
            throw new DomainException("入库数量不能为空");
        }
        List<ReceivedItem> eventItems = new ArrayList<>();
        for (ReceivedQuantity rq : receivedItems) {
            PurchaseOrderItem item = items.stream()
                    .filter(i -> i.getMaterialId().equals(rq.materialId()))
                    .findFirst()
                    .orElseThrow(() -> new DomainException("订单中不存在该物料: " + rq.materialId().value()));
            item.receive(rq.quantity());
            eventItems.add(new ReceivedItem(rq.materialId(), rq.quantity(), item.getUnitPrice()));
        }
        boolean allReceived = items.stream().allMatch(PurchaseOrderItem::isFullyReceived);
        this.status = allReceived ? PurchaseOrderStatus.RECEIVED : PurchaseOrderStatus.PARTIAL_RECEIVED;
        recordEvent(new PurchaseOrderReceivedEvent(id.value(), orgId.value(), orderCode, eventItems));
    }

    /** 关闭：已审核/部分入库 → 已关闭。 */
    public void close(Long operatorId, String reason) {
        requirePersisted();
        if (status != PurchaseOrderStatus.REVIEWED && status != PurchaseOrderStatus.PARTIAL_RECEIVED) {
            throw new DomainException("只有已审核或部分入库状态的订单才能关闭");
        }
        this.status = PurchaseOrderStatus.CLOSED;
        this.updatedBy = operatorId;
        recordEvent(new PurchaseOrderClosedEvent(id.value(), orderCode, operatorId, reason));
    }

    public List<DomainEvent> getEvents() {
        return List.copyOf(events);
    }

    public void clearEvents() {
        events.clear();
    }

    public Long idValue() {
        return id == null ? null : id.value();
    }

    private void requirePersisted() {
        if (id == null) {
            throw new DomainException("订单尚未持久化");
        }
    }

    private void recordEvent(DomainEvent event) {
        events.add(event);
    }
}