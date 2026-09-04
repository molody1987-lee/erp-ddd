package com.company.erp.inventory.infrastructure.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.company.erp.inventory.domain.aggregate.InventoryBalance;
import com.company.erp.inventory.domain.repository.InventoryBalanceRepository;
import com.company.erp.inventory.infrastructure.persistence.InventoryBalancePO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 库存结存仓储实现。
 */
@Repository
@RequiredArgsConstructor
public class InventoryBalanceRepositoryImpl implements InventoryBalanceRepository {

    private final InventoryBalanceMapper mapper;
    private final InventoryBalanceConverter converter;

    @Override
    public Optional<InventoryBalance> findByMaterialAndOrg(Long materialId, Long orgId) {
        InventoryBalancePO po = mapper.selectOne(new LambdaQueryWrapper<InventoryBalancePO>()
                .eq(InventoryBalancePO::getMaterialId, materialId)
                .eq(InventoryBalancePO::getOrgId, orgId));
        return po == null ? Optional.empty() : Optional.of(converter.toDomain(po));
    }

    @Override
    public Optional<InventoryBalance> findForUpdateByMaterialAndOrg(Long materialId, Long orgId) {
        InventoryBalancePO po = mapper.selectForUpdate(materialId, orgId);
        return po == null ? Optional.empty() : Optional.of(converter.toDomain(po));
    }

    @Override
    public Long save(InventoryBalance balance) {
        InventoryBalancePO po = converter.toPO(balance);
        if (po.getId() == null) {
            mapper.insert(po);
            balance.assignId(po.getId());
        } else {
            mapper.updateById(po);
        }
        return po.getId();
    }
}