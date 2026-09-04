package com.company.erp.purchase.infrastructure.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.company.erp.purchase.domain.aggregate.PurchaseOrder;
import com.company.erp.purchase.domain.entity.PurchaseOrderItem;
import com.company.erp.purchase.domain.repository.PurchaseOrderRepository;
import com.company.erp.purchase.domain.valueobject.PurchaseOrderId;
import com.company.erp.purchase.infrastructure.persistence.PurchaseOrderItemPO;
import com.company.erp.purchase.infrastructure.persistence.PurchaseOrderPO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 采购订单仓储实现。
 */
@Repository
@RequiredArgsConstructor
public class PurchaseOrderRepositoryImpl implements PurchaseOrderRepository {

    private final PurchaseOrderMapper orderMapper;
    private final PurchaseOrderItemMapper itemMapper;
    private final PurchaseOrderConverter converter;

    @Override
    public PurchaseOrderId save(PurchaseOrder order) {
        PurchaseOrderPO po = converter.toPO(order);
        if (po.getId() == null) {
            orderMapper.insert(po);
            order.assignId(new PurchaseOrderId(po.getId()));
            for (PurchaseOrderItem item : order.getItems()) {
                itemMapper.insert(converter.toItemPO(item, po.getId()));
            }
        } else {
            orderMapper.updateById(po);
            for (PurchaseOrderItem item : order.getItems()) {
                PurchaseOrderItemPO itemPO = converter.toItemPO(item, po.getId());
                if (itemPO.getId() == null) {
                    itemMapper.insert(itemPO);
                } else {
                    itemMapper.updateById(itemPO);
                }
            }
        }
        return order.getId();
    }

    @Override
    public Optional<PurchaseOrder> findById(PurchaseOrderId id) {
        PurchaseOrderPO po = orderMapper.selectById(id.value());
        if (po == null) {
            return Optional.empty();
        }
        List<PurchaseOrderItemPO> items = itemMapper.selectList(
                new LambdaQueryWrapper<PurchaseOrderItemPO>()
                        .eq(PurchaseOrderItemPO::getOrderId, id.value()));
        return Optional.of(converter.toDomain(po, items));
    }

    @Override
    public void deleteItems(PurchaseOrderId orderId) {
        itemMapper.delete(new LambdaQueryWrapper<PurchaseOrderItemPO>()
                .eq(PurchaseOrderItemPO::getOrderId, orderId.value()));
    }
}