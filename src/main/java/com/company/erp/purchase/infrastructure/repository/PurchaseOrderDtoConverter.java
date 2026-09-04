package com.company.erp.purchase.infrastructure.repository;

import com.company.erp.purchase.application.dto.PurchaseOrderDTO;
import com.company.erp.purchase.application.dto.PurchaseOrderItemDTO;
import com.company.erp.purchase.infrastructure.persistence.PurchaseOrderItemPO;
import com.company.erp.purchase.infrastructure.persistence.PurchaseOrderPO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * 采购订单读模型转换器（PO → DTO）。
 */
@Mapper(componentModel = "spring")
public interface PurchaseOrderDtoConverter {

    @Mapping(target = "items", source = "items")
    @Mapping(target = "statusText", ignore = true)
    PurchaseOrderDTO toDTO(PurchaseOrderPO po, List<PurchaseOrderItemDTO> items);

    PurchaseOrderItemDTO toItemDTO(PurchaseOrderItemPO itemPO);
}