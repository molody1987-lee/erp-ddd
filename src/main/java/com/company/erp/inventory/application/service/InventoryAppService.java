package com.company.erp.inventory.application.service;

import com.company.erp.inventory.application.acl.InventoryReceiveItem;
import com.company.erp.inventory.application.acl.PurchaseOrderReceivedEventTranslator;
import com.company.erp.inventory.domain.aggregate.InventoryBalance;
import com.company.erp.inventory.domain.repository.InventoryBalanceRepository;
import com.company.erp.inventory.domain.service.InventoryDistributedLock;
import com.company.erp.inventory.domain.service.InventoryEventIdempotencyStore;
import com.company.erp.purchase.domain.event.PurchaseOrderReceivedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 库存应用服务（消费采购入库事件，更新库存结存）。
 */
@Service
@RequiredArgsConstructor
public class InventoryAppService {

    private final InventoryBalanceRepository repository;
    private final InventoryEventIdempotencyStore idempotencyStore;
    private final InventoryDistributedLock distributedLock;
    private final PurchaseOrderReceivedEventTranslator translator;

    @Transactional
    public void handlePurchaseReceived(PurchaseOrderReceivedEvent event) {
        if (!idempotencyStore.markIfAbsent(event.eventId())) {
            return;
        }
        try {
            for (InventoryReceiveItem item : translator.translate(event)) {
                String lockKey = event.orgId() + "_" + item.materialId();
                distributedLock.execute(lockKey, () -> applyReceive(event.orgId(), item));
            }
        } catch (RuntimeException e) {
            idempotencyStore.remove(event.eventId());
            throw e;
        }
    }

    private void applyReceive(Long orgId, InventoryReceiveItem item) {
        InventoryBalance balance = repository.findForUpdateByMaterialAndOrg(item.materialId(), orgId)
                .orElseGet(() -> InventoryBalance.create(item.materialId(), orgId, item.quantity().unit()));
        balance.receive(item.quantity(), item.unitCost());
        repository.save(balance);
    }
}