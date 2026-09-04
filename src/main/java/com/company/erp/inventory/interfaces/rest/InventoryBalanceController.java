package com.company.erp.inventory.interfaces.rest;

import com.company.erp.common.web.CsvUtils;
import com.company.erp.common.web.R;
import com.company.erp.inventory.application.command.ImportInventoryBalanceCommand;
import com.company.erp.inventory.application.dto.InventoryBalanceDTO;
import com.company.erp.inventory.application.service.InventoryAppService;
import com.company.erp.inventory.application.service.InventoryBalanceQueryAppService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 库存结存 REST 接口。
 */
@RestController
@RequestMapping("/inventory-balances")
@RequiredArgsConstructor
public class InventoryBalanceController {

    private final InventoryBalanceQueryAppService queryService;
    private final InventoryAppService inventoryAppService;

    @GetMapping
    public R<InventoryBalanceDTO> get(@RequestParam Long materialId, @RequestParam Long orgId) {
        return queryService.getBalance(materialId, orgId)
                .map(R::ok)
                .orElseGet(() -> R.fail(404, "库存结存不存在"));
    }

    @GetMapping("/list")
    public R<List<InventoryBalanceDTO>> list(@RequestParam Long orgId) {
        return R.ok(queryService.listByOrg(orgId));
    }

    @GetMapping("/export")
    public void export(@RequestParam Long orgId, HttpServletResponse response) throws IOException {
        List<InventoryBalanceDTO> balances = queryService.listByOrg(orgId);
        List<String[]> rows = new ArrayList<>();
        rows.add(new String[]{"id", "materialId", "orgId", "quantityOnHand", "unit", "unitCost"});
        for (InventoryBalanceDTO b : balances) {
            rows.add(new String[]{String.valueOf(b.id()), String.valueOf(b.materialId()),
                    String.valueOf(b.orgId()), b.quantityOnHand().toPlainString(),
                    b.unit(), b.unitCost().toPlainString()});
        }
        response.setContentType("text/csv;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=inventory-balances.csv");
        response.getOutputStream().write(CsvUtils.render(rows));
    }

    @PostMapping("/import")
    public R<Integer> importBalances(@RequestParam("file") MultipartFile file) throws IOException {
        List<String[]> rows = CsvUtils.read(file.getInputStream());
        List<ImportInventoryBalanceCommand> commands = rows.stream()
                .map(r -> new ImportInventoryBalanceCommand(
                        Long.valueOf(r[0]), Long.valueOf(r[1]), r[2],
                        new BigDecimal(r[3]), new BigDecimal(r[4])))
                .toList();
        return R.ok(inventoryAppService.importBalances(commands));
    }
}