package com.company.erp.purchase.infrastructure.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.company.erp.purchase.domain.aggregate.PurchaseOrder;
import com.company.erp.purchase.domain.entity.PurchaseOrderItem;
import com.company.erp.purchase.domain.repository.PurchaseOrderRepository;
import com.company.erp.purchase.domain.valueobject.MaterialId;
import com.company.erp.purchase.domain.valueobject.Money;
import com.company.erp.purchase.domain.valueobject.OrganizationId;
import com.company.erp.purchase.domain.valueobject.PurchaseOrderId;
import com.company.erp.purchase.domain.valueobject.PurchaseOrderStatus;
import com.company.erp.purchase.domain.valueobject.Quantity;
import com.company.erp.purchase.domain.valueobject.SupplierId;
import com.company.erp.purchase.infrastructure.persistence.PurchaseOrderItemPO;
import com.company.erp.purchase.infrastructure.persistence.PurchaseOrderPO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 采购订单仓储实现，负责聚合根与两张表 PO 之间的装配。
 */
@Repository
@RequiredArgsConstructor
public class PurchaseOrderRepositoryImpl implements PurchaseOrderRepository {

    private final PurchaseOrderMapper orderMapper;
    private final PurchaseOrderItemMapper itemMapper;

    @Override
    public Optional<PurchaseOrder> findById(PurchaseOrderId id) {
        PurchaseOrderPO po = orderMapper.selectById(id.value());
        if (po == null) {
            return Optional.empty();
        }
        List<PurchaseOrderItemPO> itemPOs = itemMapper.selectList(
                new LambdaQueryWrapper<PurchaseOrderItemPO>()
                        .eq(PurchaseOrderItemPO::getOrderId, po.getId()));
        return Optional.of(toDomain(po, itemPOs));
    }

    @Override
    public PurchaseOrderId save(PurchaseOrder order) {
        if (order.getId() == null) {
            PurchaseOrderPO po = toPO(order);
            orderMapper.insert(po);
            order.assignId(new PurchaseOrderId(po.getId()));
            insertItems(order, po.getId());
        } else {
            orderMapper.updateById(toPO(order));
            itemMapper.delete(new LambdaQueryWrapper<PurchaseOrderItemPO>()
                    .eq(PurchaseOrderItemPO::getOrderId, order.getId().value()));
            insertItems(order, order.getId().value());
        }
        return order.getId();
    }

    private void insertItems(PurchaseOrder order, Long orderId) {
        for (PurchaseOrderItem item : order.getItems()) {
            PurchaseOrderItemPO itemPO = toItemPO(item, orderId);
            itemMapper.insert(itemPO);
        }
    }

    private PurchaseOrderPO toPO(PurchaseOrder order) {
        PurchaseOrderPO po = new PurchaseOrderPO();
        po.setId(order.getId() == null ? null : order.getId().value());
        po.setOrderCode(order.getOrderCode());
        po.setSupplierId(order.getSupplierId().value());
        po.setOrgId(order.getOrgId().value());
        po.setStatus(order.getStatus().code());
        po.setTotalAmount(order.getTotalAmount().amount());
        po.setCurrency(order.getCurrency());
        po.setRemark(order.getRemark());
        po.setCreateTime(order.getCreateTime());
        po.setCreatedBy(order.getCreatedBy());
        po.setUpdatedBy(order.getUpdatedBy());
        return po;
    }

    private PurchaseOrderItemPO toItemPO(PurchaseOrderItem item, Long orderId) {
        PurchaseOrderItemPO po = new PurchaseOrderItemPO();
        po.setOrderId(orderId);
        po.setMaterialId(item.getMaterialId().value());
        po.setQuantity(item.getQuantity().value());
        po.setUnit(item.getQuantity().unit());
        po.setReceivedQuantity(item.getReceivedQuantity().value());
        po.setUnitPrice(item.getUnitPrice().amount());
        po.setCurrency(item.getUnitPrice().currency());
        return po;
    }

    private PurchaseOrder toDomain(PurchaseOrderPO po, List<PurchaseOrderItemPO> itemPOs) {
        List<PurchaseOrderItem> items = itemPOs.stream().map(this::toDomainItem).toList();
        return PurchaseOrder.reconstruct(
                new PurchaseOrderId(po.getId()),
                po.getOrderCode(),
                new SupplierId(po.getSupplierId()),
                new OrganizationId(po.getOrgId()),
                items,
                PurchaseOrderStatus.fromCode(po.getStatus()),
                Money.of(po.getTotalAmount(), po.getCurrency()),
                po.getCurrency(),
                po.getRemark(),
                po.getCreateTime(),
                po.getCreatedBy(),
                po.getUpdatedBy());
    }

    private PurchaseOrderItem toDomainItem(PurchaseOrderItemPO po) {
        return new PurchaseOrderItem(
                po.getId(),
                new MaterialId(po.getMaterialId()),
                Quantity.of(po.getQuantity(), po.getUnit()),
                Money.of(po.getUnitPrice(), po.getCurrency()),
                Quantity.of(po.getReceivedQuantity() == null ? java.math.BigDecimal.ZERO : po.getReceivedQuantity(), po.getUnit()));
    }
}