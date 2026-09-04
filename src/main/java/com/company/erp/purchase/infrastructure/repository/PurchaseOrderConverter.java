package com.company.erp.purchase.infrastructure.repository;

import com.company.erp.purchase.application.dto.PurchaseOrderDTO;
import com.company.erp.purchase.application.dto.PurchaseOrderItemDTO;
import com.company.erp.purchase.domain.aggregate.PurchaseOrder;
import com.company.erp.purchase.domain.entity.PurchaseOrderItem;
import com.company.erp.purchase.domain.valueobject.MaterialId;
import com.company.erp.purchase.domain.valueobject.Money;
import com.company.erp.purchase.domain.valueobject.PurchaseOrderId;
import com.company.erp.purchase.domain.valueobject.PurchaseOrderStatus;
import com.company.erp.purchase.domain.valueobject.Quantity;
import com.company.erp.purchase.infrastructure.persistence.PurchaseOrderItemPO;
import com.company.erp.purchase.infrastructure.persistence.PurchaseOrderPO;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 采购订单领域模型与持久化对象之间的转换器。
 */
@Component
public class PurchaseOrderConverter {

    public PurchaseOrderPO toPO(PurchaseOrder order) {
        PurchaseOrderPO po = new PurchaseOrderPO();
        if (order.getId() != null) {
            po.setId(order.getId().value());
        }
        po.setOrderCode(order.getOrderCode());
        po.setSupplierId(order.getSupplierId());
        po.setOrgId(order.getOrgId());
        po.setStatus(order.getStatus().getCode());
        po.setCreateTime(order.getCreatedAt());
        po.setUpdateTime(order.getUpdatedAt());
        return po;
    }

    public PurchaseOrderItemPO toItemPO(PurchaseOrderItem item, Long orderId) {
        PurchaseOrderItemPO po = new PurchaseOrderItemPO();
        if (item.getId() != null) {
            po.setId(item.getId());
        }
        po.setOrderId(orderId);
        po.setMaterialId(item.getMaterialId().value());
        po.setQuantity(item.getQuantity().value());
        po.setUnit(item.getQuantity().unit());
        po.setUnitPrice(item.getUnitPrice().amount());
        po.setCurrency(item.getUnitPrice().currency());
        po.setReceivedQuantity(item.getReceivedQuantity().value());
        return po;
    }

    public PurchaseOrder toDomain(PurchaseOrderPO po, List<PurchaseOrderItemPO> itemPOs) {
        List<PurchaseOrderItem> items = itemPOs.stream().map(this::toItemDomain).toList();
        return PurchaseOrder.reconstitute(
                new PurchaseOrderId(po.getId()),
                po.getOrderCode(),
                po.getSupplierId(),
                po.getOrgId(),
                PurchaseOrderStatus.fromCode(po.getStatus()),
                items,
                po.getCreateTime(),
                po.getUpdateTime());
    }

    private PurchaseOrderItem toItemDomain(PurchaseOrderItemPO po) {
        return PurchaseOrderItem.reconstitute(
                po.getId(),
                new MaterialId(po.getMaterialId()),
                new Quantity(po.getQuantity(), po.getUnit()),
                new Money(po.getUnitPrice(), po.getCurrency()),
                new Quantity(po.getReceivedQuantity(), po.getUnit()));
    }

    public PurchaseOrderDTO toDTO(PurchaseOrderPO po, List<PurchaseOrderItemPO> itemPOs) {
        List<PurchaseOrderItemDTO> items = itemPOs.stream().map(this::toItemDTO).toList();
        return new PurchaseOrderDTO(
                po.getId(),
                po.getOrderCode(),
                po.getSupplierId(),
                po.getOrgId(),
                PurchaseOrderStatus.fromCode(po.getStatus()).name(),
                items,
                po.getCreateTime());
    }

    private PurchaseOrderItemDTO toItemDTO(PurchaseOrderItemPO po) {
        return new PurchaseOrderItemDTO(
                po.getId(),
                po.getMaterialId(),
                po.getQuantity(),
                po.getUnit(),
                po.getUnitPrice(),
                po.getCurrency(),
                po.getReceivedQuantity());
    }
}