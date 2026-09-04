package com.company.erp.purchase.interfaces.rest;

import com.company.erp.purchase.application.dto.PageResult;
import com.company.erp.purchase.application.dto.PurchaseOrderDTO;
import com.company.erp.purchase.application.query.PurchaseOrderPageQuery;
import com.company.erp.purchase.application.service.PurchaseOrderApplicationService;
import com.company.erp.purchase.application.service.PurchaseOrderQueryService;
import com.company.erp.purchase.interfaces.rest.converter.PurchaseOrderRequestConverter;
import com.company.erp.purchase.interfaces.rest.dto.ClosePurchaseOrderRequest;
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

/**
 * 采购订单 REST 接口。
 */
@RestController
@RequestMapping("/api/purchase-orders")
@RequiredArgsConstructor
public class PurchaseOrderController {

    private final PurchaseOrderApplicationService applicationService;
    private final PurchaseOrderQueryService queryService;
    private final PurchaseOrderRequestConverter converter;

    @PostMapping
    public CommonResponse<Long> create(@Valid @RequestBody CreatePurchaseOrderRequest request) {
        Long orderId = applicationService.create(converter.toCommand(request));
        return CommonResponse.ok(orderId);
    }

    @PostMapping("/{id}/review")
    public CommonResponse<Void> review(@PathVariable("id") Long id,
                                       @RequestBody(required = false) ReviewPurchaseOrderRequest request) {
        ReviewPurchaseOrderRequest req = request == null ? new ReviewPurchaseOrderRequest() : request;
        req.setOrderId(id);
        applicationService.review(converter.toCommand(req));
        return CommonResponse.ok(null);
    }

    @PostMapping("/{id}/receive")
    public CommonResponse<Void> receive(@PathVariable("id") Long id,
                                        @Valid @RequestBody ReceivePurchaseOrderRequest request) {
        request.setOrderId(id);
        applicationService.receive(converter.toCommand(request));
        return CommonResponse.ok(null);
    }

    @PostMapping("/{id}/close")
    public CommonResponse<Void> close(@PathVariable("id") Long id,
                                      @RequestBody(required = false) ClosePurchaseOrderRequest request) {
        ClosePurchaseOrderRequest req = request == null ? new ClosePurchaseOrderRequest() : request;
        req.setOrderId(id);
        applicationService.close(converter.toCommand(req));
        return CommonResponse.ok(null);
    }

    @GetMapping("/{id}")
    public CommonResponse<PurchaseOrderDTO> getDetail(@PathVariable("id") Long id) {
        return CommonResponse.ok(queryService.getDetail(id));
    }

    @GetMapping
    public CommonResponse<PageResult<PurchaseOrderDTO>> page(
            @RequestParam(defaultValue = "1") long pageNum,
            @RequestParam(defaultValue = "20") long pageSize,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Long supplierId) {
        PurchaseOrderPageQuery query = new PurchaseOrderPageQuery(pageNum, pageSize, status, supplierId);
        return CommonResponse.ok(queryService.page(query));
    }
}