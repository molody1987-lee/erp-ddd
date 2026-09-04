package com.company.erp.purchase.interfaces.rest;

import com.company.erp.common.web.CsvUtils;
import com.company.erp.common.web.R;
import com.company.erp.purchase.application.command.CreatePurchaseOrderCommand;
import com.company.erp.purchase.application.command.CreatePurchaseOrderItemCommand;
import com.company.erp.purchase.application.command.ModifyPurchaseOrderCommand;
import com.company.erp.purchase.application.command.ReceivePurchaseOrderCommand;
import com.company.erp.purchase.application.command.ReviewPurchaseOrderCommand;
import com.company.erp.purchase.application.dto.PurchaseOrderDTO;
import com.company.erp.purchase.application.service.PurchaseOrderAppService;
import com.company.erp.purchase.application.service.PurchaseOrderQueryAppService;
import com.company.erp.purchase.domain.valueobject.MaterialId;
import com.company.erp.purchase.domain.valueobject.PurchaseOrderId;
import com.company.erp.purchase.domain.valueobject.Quantity;
import com.company.erp.purchase.domain.valueobject.ReceivedQuantity;
import com.company.erp.purchase.interfaces.rest.dto.CreatePurchaseOrderRequest;
import com.company.erp.purchase.interfaces.rest.dto.ModifyPurchaseOrderRequest;
import com.company.erp.purchase.interfaces.rest.dto.ReceivePurchaseOrderRequest;
import com.company.erp.purchase.interfaces.rest.dto.ReviewPurchaseOrderRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

    @PutMapping("/{id}")
    public R<Void> modify(@PathVariable Long id, @Valid @RequestBody ModifyPurchaseOrderRequest request) {
        List<CreatePurchaseOrderItemCommand> items = request.items().stream()
                .map(item -> new CreatePurchaseOrderItemCommand(
                        item.materialId(), item.quantity(), item.unit(),
                        item.unitPrice(), item.currency()))
                .toList();
        appService.modifyOrder(new ModifyPurchaseOrderCommand(
                new PurchaseOrderId(id), request.orderCode(), request.supplierId(), items));
        return R.ok();
    }

    @PostMapping("/{id}/cancel")
    public R<Void> cancel(@PathVariable Long id) {
        appService.cancelOrder(new PurchaseOrderId(id));
        return R.ok();
    }

    @GetMapping("/export")
    public void export(@RequestParam Long orgId, HttpServletResponse response) throws IOException {
        List<PurchaseOrderDTO> orders = queryService.listByOrg(orgId);
        List<String[]> rows = new ArrayList<>();
        rows.add(new String[]{"id", "orderCode", "supplierId", "orgId", "status"});
        for (PurchaseOrderDTO o : orders) {
            rows.add(new String[]{String.valueOf(o.id()), o.orderCode(),
                    String.valueOf(o.supplierId()), String.valueOf(o.orgId()), o.status()});
        }
        response.setContentType("text/csv;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=purchase-orders.csv");
        response.getOutputStream().write(CsvUtils.render(rows));
    }

    @PostMapping("/import")
    public R<Integer> importOrders(@RequestParam("file") MultipartFile file) throws IOException {
        List<String[]> rows = CsvUtils.read(file.getInputStream());
        Map<String, List<CreatePurchaseOrderItemCommand>> itemsByOrder = new LinkedHashMap<>();
        Map<String, Long[]> headerByOrder = new HashMap<>();
        for (String[] r : rows) {
            String orderCode = r[0];
            headerByOrder.putIfAbsent(orderCode, new Long[]{Long.valueOf(r[1]), Long.valueOf(r[2])});
            itemsByOrder.computeIfAbsent(orderCode, k -> new ArrayList<>())
                    .add(new CreatePurchaseOrderItemCommand(
                            Long.valueOf(r[3]), new BigDecimal(r[4]), r[5],
                            new BigDecimal(r[6]), r[7]));
        }
        List<CreatePurchaseOrderCommand> commands = new ArrayList<>();
        for (Map.Entry<String, List<CreatePurchaseOrderItemCommand>> entry : itemsByOrder.entrySet()) {
            Long[] header = headerByOrder.get(entry.getKey());
            commands.add(new CreatePurchaseOrderCommand(
                    entry.getKey(), header[0], header[1], entry.getValue()));
        }
        return R.ok(appService.importOrders(commands));
    }
}