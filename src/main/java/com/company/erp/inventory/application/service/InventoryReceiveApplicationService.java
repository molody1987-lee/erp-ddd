package com.company.erp.inventory.application.service;

import com.company.erp.inventory.application.command.ReceiveInventoryCommand;
import com.company.erp.inventory.application.port.InventoryDistributedLock;
import com.company.erp.inventory.application.port.InventoryEventIdempotencyStore;
import com.company.erp.inventory.domain.aggregate.InventoryBalance;
import com.company.erp.inventory.domain.repository.InventoryBalanceRepository;
import com.company.erp.inventory.domain.valueobject.InventoryQuantity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

/**
 * 库存接收应用服务，负责采购入库用例编排与事务边界。
 * <p>
 * 兜底策略：以事件幂等处理重投、以分布式锁串行化同一物料/组织的并发读写、
 * 以数据库唯一索引兜住首次入库的并发插入。
 */
@Service
@RequiredArgsConstructor
public class InventoryReceiveApplicationService {

    private final InventoryBalanceRepository repository;
    private final InventoryEventIdempotencyStore idempotencyStore;
    private final InventoryDistributedLock lock;

    @Transactional
    public void receive(List<ReceiveInventoryCommand> commands) {
        if (commands == null || commands.isEmpty()) {
            return;
        }
        String eventId = commands.get(0).eventId();
        if (!idempotencyStore.markIfAbsent(eventId)) {
            return; // 该事件已处理，幂等忽略
        }
        try {
            // 按 (orgId, materialId) 排序，保证多处并发加锁顺序一致，避免死锁
            List<ReceiveInventoryCommand> ordered = commands.stream()
                    .sorted(Comparator.comparingLong(ReceiveInventoryCommand::orgId)
                            .thenComparingLong(ReceiveInventoryCommand::materialId))
                    .toList();
            for (ReceiveInventoryCommand cmd : ordered) {
                String resourceKey = "inventory:material_" + cmd.orgId() + "_" + cmd.materialId();
                lock.execute(resourceKey, () -> apply(cmd));
            }
        } catch (RuntimeException e) {
            // 处理失败时移除幂等标记，保证 RocketMQ 重投后能重新处理
            idempotencyStore.remove(eventId);
            throw e;
        }
    }

    private void apply(ReceiveInventoryCommand cmd) {
        // 悲观行锁锁定现有结存行直至事务提交，彻底消除并发 UPDATE 的丢失更新
        InventoryBalance balance = repository.findForUpdateByMaterialAndOrg(cmd.materialId(), cmd.orgId())
                .orElseGet(() -> InventoryBalance.create(cmd.materialId(), cmd.orgId(), cmd.unit(), cmd.currency()));
        balance.receive(InventoryQuantity.of(cmd.quantity(), cmd.unit()), cmd.unitCost());
        repository.save(balance);
    }
}