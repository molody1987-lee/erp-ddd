package com.company.erp.inventory.application.service;

import com.company.erp.inventory.application.dto.InventoryBalanceDTO;
import com.company.erp.inventory.application.query.InventoryBalanceQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 库存结存查询应用服务。
 */
@Service
@RequiredArgsConstructor
public class InventoryBalanceQueryAppService {

    private final InventoryBalanceQueryRepository queryRepository;

    public Optional<InventoryBalanceDTO> getBalance(Long materialId, Long orgId) {
        return queryRepository.findBalance(materialId, orgId);
    }
}