package com.company.erp.inventory.interfaces.rest;

import com.company.erp.common.web.R;
import com.company.erp.inventory.application.dto.InventoryBalanceDTO;
import com.company.erp.inventory.application.service.InventoryBalanceQueryAppService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 库存结存 REST 接口。
 */
@RestController
@RequestMapping("/inventory-balances")
@RequiredArgsConstructor
public class InventoryBalanceController {

    private final InventoryBalanceQueryAppService queryService;

    @GetMapping
    public R<InventoryBalanceDTO> get(@RequestParam Long materialId, @RequestParam Long orgId) {
        return queryService.getBalance(materialId, orgId)
                .map(R::ok)
                .orElseGet(() -> R.fail(404, "库存结存不存在"));
    }
}