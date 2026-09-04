package com.company.erp.purchase.interfaces.rest;

import com.company.erp.purchase.application.command.CreatePurchaseOrderCommand;
import com.company.erp.purchase.application.command.CreatePurchaseOrderItemCommand;
import com.company.erp.purchase.application.command.ReceivePurchaseOrderCommand;
import com.company.erp.purchase.application.command.ReviewPurchaseOrderCommand;
import com.company.erp.purchase.application.dto.PurchaseOrderDTO;
import com.company.erp.purchase.application.service.PurchaseOrderAppService;
import com.company.erp.purchase.application.service.PurchaseOrderQueryAppService;
import com.company.erp.purchase.domain.valueobject.MaterialId;
import com.company.erp.purchase.domain.valueobject.PurchaseOrderId;
import com.company.erp.purchase.domain.valueobject.Quantity;
import com.company.erp.purchase.domain.valueobject.ReceivedQuantity;
import com.company.erp.common.web.R;
import com.company.erp.purchase.interfaces.rest.dto.CreatePurchaseOrderRequest;
import com.company.erp.purchase.interfaces.rest.dto.ReceivePurchaseOrderRequest;
import com.company.erp.purchase.interfaces.rest.dto.ReviewPurchaseOrderRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 采购订单 REST 接口。
 */
@RestController
@RequestMapping("/purchase-orders")
@RequiredArgsConstructor
public class PurchaseOrderController {

    private final PurchaseOrderAppService appService;
    private final PurchaseOrderQueryAppService queryService;

    @PostMapping
    public R<Long> create(@Valid @RequestBody CreatePurchaseOrderRequest request) {
        List<CreatePurchaseOrderItemCommand> items = request.items().stream()
                .map(item -> new CreatePurchaseOrderItemCommand(
                        item.materialId(), item.quantity(), item.unit(),
                        item.unitPrice(), item.currency()))
                .toList();
        CreatePurchaseOrderCommand command = new CreatePurchaseOrderCommand(
                request.orderCode(), request.supplierId(), request.orgId(), items);
        return R.ok(appService.createOrder(command).value());
    }

    @PostMapping("/{id}/review")
    public R<Void> review(@PathVariable Long id, @Valid @RequestBody ReviewPurchaseOrderRequest request) {
        appService.reviewOrder(new ReviewPurchaseOrderCommand(new PurchaseOrderId(id), request.reviewerId()));
        return R.ok();
    }

    @PostMapping("/{id}/receive")
    public R<Void> receive(@PathVariable Long id, @Valid @RequestBody ReceivePurchaseOrderRequest request) {
        List<ReceivedQuantity> receivedItems = request.items().stream()
                .map(item -> new ReceivedQuantity(
                        new MaterialId(item.materialId()),
                        new Quantity(item.quantity(), item.unit())))
                .toList();
        appService.receiveOrder(new ReceivePurchaseOrderCommand(new PurchaseOrderId(id), receivedItems));
        return R.ok();
    }

    @GetMapping("/{id}")
    public R<PurchaseOrderDTO> detail(@PathVariable Long id) {
        return queryService.getDetail(id)
                .map(R::ok)
                .orElseGet(() -> R.fail(404, "采购订单不存在: " + id));
    }

    @GetMapping
    public R<List<PurchaseOrderDTO>> list(@RequestParam Long orgId) {
        return R.ok(queryService.listByOrg(orgId));
    }
}