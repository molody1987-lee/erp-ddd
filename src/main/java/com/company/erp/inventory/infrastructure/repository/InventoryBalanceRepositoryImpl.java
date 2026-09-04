package com.company.erp.inventory.infrastructure.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.company.erp.inventory.domain.aggregate.InventoryBalance;
import com.company.erp.inventory.domain.repository.InventoryBalanceRepository;
import com.company.erp.inventory.infrastructure.persistence.InventoryBalancePO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 库存结存仓储实现，负责聚合根与 PO 之间的装配。
 */
@Repository
@RequiredArgsConstructor
public class InventoryBalanceRepositoryImpl implements InventoryBalanceRepository {

    private final InventoryBalanceMapper mapper;

    @Override
    public Optional<InventoryBalance> findByMaterialAndOrg(Long materialId, Long orgId) {
        InventoryBalancePO po = mapper.selectOne(new LambdaQueryWrapper<InventoryBalancePO>()
                .eq(InventoryBalancePO::getMaterialId, materialId)
                .eq(InventoryBalancePO::getOrgId, orgId));
        if (po == null) {
            return Optional.empty();
        }
        return Optional.of(toDomain(po));
    }

    @Override
    public Optional<InventoryBalance> findForUpdateByMaterialAndOrg(Long materialId, Long orgId) {
        InventoryBalancePO po = mapper.selectOne(new LambdaQueryWrapper<InventoryBalancePO>()
                .eq(InventoryBalancePO::getMaterialId, materialId)
                .eq(InventoryBalancePO::getOrgId, orgId)
                .last("FOR UPDATE"));
        if (po == null) {
            return Optional.empty();
        }
        return Optional.of(toDomain(po));
    }

    @Override
    public Long save(InventoryBalance balance) {
        InventoryBalancePO po = toPO(balance);
        if (po.getId() == null) {
            mapper.insert(po);
            balance.assignId(po.getId());
        } else {
            mapper.updateById(po);
        }
        return po.getId();
    }

    private InventoryBalancePO toPO(InventoryBalance balance) {
        InventoryBalancePO po = new InventoryBalancePO();
        po.setId(balance.getId());
        po.setMaterialId(balance.getMaterialId());
        po.setOrgId(balance.getOrgId());
        po.setUnit(balance.getUnit());
        po.setCurrency(balance.getCurrency());
        po.setQuantityOnHand(balance.getQuantityOnHand());
        po.setTotalCost(balance.getTotalCost());
        po.setCreateTime(balance.getCreateTime());
        po.setUpdateTime(balance.getUpdateTime());
        return po;
    }

    private InventoryBalance toDomain(InventoryBalancePO po) {
        return InventoryBalance.reconstruct(
                po.getId(),
                po.getMaterialId(),
                po.getOrgId(),
                po.getUnit(),
                po.getCurrency(),
                po.getQuantityOnHand(),
                po.getTotalCost(),
                po.getCreateTime(),
                po.getUpdateTime());
    }
}