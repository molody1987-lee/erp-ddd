package com.company.erp.purchase.infrastructure.query;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.company.erp.purchase.application.dto.PurchaseOrderDTO;
import com.company.erp.purchase.application.query.PurchaseOrderQueryRepository;
import com.company.erp.purchase.domain.valueobject.PurchaseOrderId;
import com.company.erp.purchase.infrastructure.persistence.PurchaseOrderItemPO;
import com.company.erp.purchase.infrastructure.persistence.PurchaseOrderPO;
import com.company.erp.purchase.infrastructure.repository.PurchaseOrderConverter;
import com.company.erp.purchase.infrastructure.repository.PurchaseOrderItemMapper;
import com.company.erp.purchase.infrastructure.repository.PurchaseOrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 采购订单查询仓储实现（直接投影到读模型）。
 */
@Repository
@RequiredArgsConstructor
public class PurchaseOrderQueryRepositoryImpl implements PurchaseOrderQueryRepository {

    private final PurchaseOrderMapper orderMapper;
    private final PurchaseOrderItemMapper itemMapper;
    private final PurchaseOrderConverter converter;

    @Override
    public Optional<PurchaseOrderDTO> findDetail(PurchaseOrderId id) {
        PurchaseOrderPO po = orderMapper.selectById(id.value());
        if (po == null) {
            return Optional.empty();
        }
        List<PurchaseOrderItemPO> items = selectItems(id.value());
        return Optional.of(converter.toDTO(po, items));
    }

    @Override
    public List<PurchaseOrderDTO> findByOrgId(Long orgId) {
        List<PurchaseOrderPO> pos = orderMapper.selectList(
                new LambdaQueryWrapper<PurchaseOrderPO>()
                        .eq(PurchaseOrderPO::getOrgId, orgId)
                        .orderByDesc(PurchaseOrderPO::getCreateTime));
        return pos.stream()
                .map(po -> converter.toDTO(po, selectItems(po.getId())))
                .toList();
    }

    private List<PurchaseOrderItemPO> selectItems(Long orderId) {
        return itemMapper.selectList(
                new LambdaQueryWrapper<PurchaseOrderItemPO>()
                        .eq(PurchaseOrderItemPO::getOrderId, orderId));
    }
}