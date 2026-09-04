package com.company.erp.purchase.application.service;

import com.company.erp.purchase.application.command.CreatePurchaseOrderCommand;
import com.company.erp.purchase.application.command.ReceivePurchaseOrderCommand;
import com.company.erp.purchase.application.command.ReviewPurchaseOrderCommand;
import com.company.erp.purchase.domain.aggregate.PurchaseOrder;
import com.company.erp.purchase.domain.entity.PurchaseOrderItem;
import com.company.erp.purchase.domain.event.PurchaseOrderEventPublisher;
import com.company.erp.purchase.domain.event.PurchaseOrderReceivedEvent;
import com.company.erp.purchase.domain.exception.PurchaseDomainException;
import com.company.erp.purchase.domain.repository.PurchaseOrderRepository;
import com.company.erp.purchase.domain.valueobject.MaterialId;
import com.company.erp.purchase.domain.valueobject.Money;
import com.company.erp.purchase.domain.valueobject.PurchaseOrderId;
import com.company.erp.purchase.domain.valueobject.Quantity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 采购订单应用服务（用例编排与事务边界）。
 */
@Service
@RequiredArgsConstructor
public class PurchaseOrderAppService {

    private final PurchaseOrderRepository repository;
    private final PurchaseOrderEventPublisher eventPublisher;

    @Transactional
    public PurchaseOrderId createOrder(CreatePurchaseOrderCommand command) {
        List<PurchaseOrderItem> items = command.items().stream()
                .map(item -> PurchaseOrderItem.create(
                        new MaterialId(item.materialId()),
                        new Quantity(item.quantity(), item.unit()),
                        new Money(item.unitPrice(), item.currency())))
                .toList();
        PurchaseOrder order = PurchaseOrder.create(command.orderCode(), command.supplierId(),
                command.orgId(), items);
        return repository.save(order);
    }

    @Transactional
    public void reviewOrder(ReviewPurchaseOrderCommand command) {
        PurchaseOrder order = repository.findById(command.orderId())
                .orElseThrow(() -> new PurchaseDomainException("采购订单不存在: " + command.orderId().value()));
        order.review(command.reviewerId());
        repository.save(order);
    }

    @Transactional
    public void receiveOrder(ReceivePurchaseOrderCommand command) {
        PurchaseOrder order = repository.findById(command.orderId())
                .orElseThrow(() -> new PurchaseDomainException("采购订单不存在: " + command.orderId().value()));

        Map<Long, BigDecimal> unitPriceByMaterial = order.getItems().stream()
                .collect(Collectors.toMap(
                        item -> item.getMaterialId().value(),
                        item -> item.getUnitPrice().amount()));

        order.receive(command.receivedItems());
        repository.save(order);

        List<PurchaseOrderReceivedEvent.Item> eventItems = command.receivedItems().stream()
                .map(received -> new PurchaseOrderReceivedEvent.Item(
                        received.materialId().value(),
                        received.quantity().value(),
                        received.quantity().unit(),
                        unitPriceByMaterial.get(received.materialId().value())))
                .toList();

        PurchaseOrderReceivedEvent event = new PurchaseOrderReceivedEvent(
                UUID.randomUUID().toString(),
                order.getOrgId(),
                order.getId().value(),
                eventItems,
                LocalDateTime.now());

        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    eventPublisher.publish(event);
                }
            });
        } else {
            eventPublisher.publish(event);
        }
    }
}