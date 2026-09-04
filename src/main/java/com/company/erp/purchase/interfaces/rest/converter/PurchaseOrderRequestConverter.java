package com.company.erp.purchase.interfaces.rest.converter;

import com.company.erp.purchase.application.command.ClosePurchaseOrderCommand;
import com.company.erp.purchase.application.command.CreatePurchaseOrderCommand;
import com.company.erp.purchase.application.command.ItemCommand;
import com.company.erp.purchase.application.command.ReceiveItemCommand;
import com.company.erp.purchase.application.command.ReceivePurchaseOrderCommand;
import com.company.erp.purchase.application.command.ReviewPurchaseOrderCommand;
import com.company.erp.purchase.interfaces.rest.dto.ClosePurchaseOrderRequest;
import com.company.erp.purchase.interfaces.rest.dto.CreatePurchaseOrderItemRequest;
import com.company.erp.purchase.interfaces.rest.dto.CreatePurchaseOrderRequest;
import com.company.erp.purchase.interfaces.rest.dto.ReceiveItemRequest;
import com.company.erp.purchase.interfaces.rest.dto.ReceivePurchaseOrderRequest;
import com.company.erp.purchase.interfaces.rest.dto.ReviewPurchaseOrderRequest;
import org.mapstruct.Mapper;

/**
 * 请求对象 → 命令 的结构转换器。
 */
@Mapper(componentModel = "spring")
public interface PurchaseOrderRequestConverter {

    CreatePurchaseOrderCommand toCommand(CreatePurchaseOrderRequest request);

    ItemCommand toItem(CreatePurchaseOrderItemRequest request);

    ReviewPurchaseOrderCommand toCommand(ReviewPurchaseOrderRequest request);

    ReceivePurchaseOrderCommand toCommand(ReceivePurchaseOrderRequest request);

    ReceiveItemCommand toItem(ReceiveItemRequest request);

    ClosePurchaseOrderCommand toCommand(ClosePurchaseOrderRequest request);
}