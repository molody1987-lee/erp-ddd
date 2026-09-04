package com.company.erp.purchase.application.service;

import com.company.erp.purchase.application.command.ClosePurchaseOrderCommand;
import com.company.erp.purchase.application.command.CreatePurchaseOrderCommand;
import com.company.erp.purchase.application.command.ItemCommand;
import com.company.erp.purchase.application.command.ReceivePurchaseOrderCommand;
import com.company.erp.purchase.application.command.ReviewPurchaseOrderCommand;
import com.company.erp.purchase.application.port.DomainEventPublisher;
import com.company.erp.purchase.application.port.OrderCodeGenerator;
import com.company.erp.purchase.domain.aggregate.PurchaseOrder;
import com.company.erp.purchase.domain.entity.PurchaseOrderItem;
import com.company.erp.purchase.domain.event.DomainEvent;
import com.company.erp.purchase.domain.event.PurchaseOrderCreatedEvent;
import com.company.erp.purchase.domain.exception.DomainException;
import com.company.erp.purchase.domain.repository.PurchaseOrderRepository;
import com.company.erp.purchase.domain.service.PurchaseOrderValidator;
import com.company.erp.purchase.domain.valueobject.MaterialId;
import com.company.erp.purchase.domain.valueobject.Money;
import com.company.erp.purchase.domain.valueobject.OrganizationId;
import com.company.erp.purchase.domain.valueobject.PurchaseOrderId;
import com.company.erp.purchase.domain.valueobject.Quantity;
import com.company.erp.purchase.domain.valueobject.ReceivedQuantity;
import com.company.erp.purchase.domain.valueobject.SupplierId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;

/**
 * 采购订单应用服务，负责用例编排与事务边界。
 */
@Service
@RequiredArgsConstructor
public class PurchaseOrderApplicationService {

    private final PurchaseOrderRepository repository;
    private final PurchaseOrderValidator validator = new PurchaseOrderValidator();
    private final DomainEventPublisher publisher;
    private final OrderCodeGenerator orderCodeGenerator;

    @Transactional
    public Long create(CreatePurchaseOrderCommand cmd) {
        List<PurchaseOrderItem> items = cmd.items().stream()
                .map(i -> toItem(i, cmd.currency()))
                .toList();
        validator.validate(items);

        PurchaseOrder order = PurchaseOrder.create(
                orderCodeGenerator.nextCode(),
                new SupplierId(cmd.supplierId()),
                new OrganizationId(cmd.orgId()),
                cmd.currency(),
                items,
                cmd.remark(),
                cmd.createdBy());
        PurchaseOrderId id = repository.save(order);

        publishAfterCommit(new PurchaseOrderCreatedEvent(
                id.value(), order.getOrderCode(), cmd.supplierId(), cmd.orgId(),
                order.getTotalAmount().amount(), cmd.currency()));
        return id.value();
    }

    @Transactional
    public void review(ReviewPurchaseOrderCommand cmd) {
        PurchaseOrder order = load(cmd.orderId());
        order.review(cmd.operatorId());
        repository.save(order);
        publishEventsAfterCommit(order);
    }

    @Transactional
    public void receive(ReceivePurchaseOrderCommand cmd) {
        PurchaseOrder order = load(cmd.orderId());
        List<ReceivedQuantity> received = cmd.items().stream()
                .map(i -> new ReceivedQuantity(
                        new MaterialId(i.materialId()),
                        Quantity.of(i.quantity(), i.unit())))
                .toList();
        order.receive(received);
        repository.save(order);
        publishEventsAfterCommit(order);
    }

    @Transactional
    public void close(ClosePurchaseOrderCommand cmd) {
        PurchaseOrder order = load(cmd.orderId());
        order.close(cmd.operatorId(), cmd.reason());
        repository.save(order);
        publishEventsAfterCommit(order);
    }

    private PurchaseOrder load(Long orderId) {
        return repository.findById(new PurchaseOrderId(orderId))
                .orElseThrow(() -> new DomainException("采购订单不存在: " + orderId));
    }

    private PurchaseOrderItem toItem(ItemCommand item, String currency) {
        return new PurchaseOrderItem(
                new MaterialId(item.materialId()),
                Quantity.of(item.quantity(), item.unit()),
                Money.of(item.unitPrice(), currency));
    }

    private void publishEventsAfterCommit(PurchaseOrder order) {
        List<DomainEvent> events = order.getEvents();
        if (events.isEmpty()) {
            return;
        }
        order.clearEvents();
        events.forEach(this::publishAfterCommit);
    }

    /** 事务提交后发布事件，避免脏读。 */
    private void publishAfterCommit(DomainEvent event) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    publisher.publish(event);
                }
            });
        } else {
            publisher.publish(event);
        }
    }
}