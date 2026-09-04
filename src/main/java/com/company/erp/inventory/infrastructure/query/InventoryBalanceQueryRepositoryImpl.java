package com.company.erp.inventory.infrastructure.query;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.company.erp.inventory.application.dto.InventoryBalanceDTO;
import com.company.erp.inventory.application.query.InventoryBalanceQueryRepository;
import com.company.erp.inventory.infrastructure.persistence.InventoryBalancePO;
import com.company.erp.inventory.infrastructure.repository.InventoryBalanceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 库存结存查询仓储实现（直接投影到读模型）。
 */
@Repository
@RequiredArgsConstructor
public class InventoryBalanceQueryRepositoryImpl implements InventoryBalanceQueryRepository {

    private final InventoryBalanceMapper mapper;

    @Override
    public Optional<InventoryBalanceDTO> findBalance(Long materialId, Long orgId) {
        InventoryBalancePO po = mapper.selectOne(new LambdaQueryWrapper<InventoryBalancePO>()
                .eq(InventoryBalancePO::getMaterialId, materialId)
                .eq(InventoryBalancePO::getOrgId, orgId));
        return po == null ? Optional.empty() : Optional.of(toDTO(po));
    }

    @Override
    public List<InventoryBalanceDTO> findByOrgId(Long orgId) {
        return mapper.selectList(new LambdaQueryWrapper<InventoryBalancePO>()
                        .eq(InventoryBalancePO::getOrgId, orgId))
                .stream()
                .map(this::toDTO)
                .toList();
    }

    private InventoryBalanceDTO toDTO(InventoryBalancePO po) {
        return new InventoryBalanceDTO(
                po.getId(),
                po.getMaterialId(),
                po.getOrgId(),
                po.getQuantityOnHand(),
                po.getUnit(),
                po.getUnitCost());
    }
}