package com.company.erp.purchase.infrastructure.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.company.erp.purchase.application.dto.PageResult;
import com.company.erp.purchase.application.dto.PurchaseOrderDTO;
import com.company.erp.purchase.application.dto.PurchaseOrderItemDTO;
import com.company.erp.purchase.application.query.PurchaseOrderPageQuery;
import com.company.erp.purchase.application.query.PurchaseOrderQueryRepository;
import com.company.erp.purchase.domain.valueobject.PurchaseOrderStatus;
import com.company.erp.purchase.infrastructure.persistence.PurchaseOrderItemPO;
import com.company.erp.purchase.infrastructure.persistence.PurchaseOrderPO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 采购订单查询仓储实现，直接投影到读模型，不加载聚合根。
 */
@Repository
@RequiredArgsConstructor
public class PurchaseOrderQueryRepositoryImpl implements PurchaseOrderQueryRepository {

    private final PurchaseOrderMapper orderMapper;
    private final PurchaseOrderItemMapper itemMapper;
    private final PurchaseOrderDtoConverter dtoConverter;

    @Override
    public Optional<PurchaseOrderDTO> findById(Long id) {
        PurchaseOrderPO po = orderMapper.selectById(id);
        if (po == null) {
            return Optional.empty();
        }
        return Optional.of(toDTO(po));
    }

    @Override
    public PageResult<PurchaseOrderDTO> page(PurchaseOrderPageQuery query) {
        LambdaQueryWrapper<PurchaseOrderPO> wrapper = new LambdaQueryWrapper<PurchaseOrderPO>()
                .eq(query.status() != null, PurchaseOrderPO::getStatus, query.status())
                .eq(query.supplierId() != null, PurchaseOrderPO::getSupplierId, query.supplierId())
                .orderByDesc(PurchaseOrderPO::getCreateTime);
        Page<PurchaseOrderPO> result = orderMapper.selectPage(new Page<>(query.pageNum(), query.pageSize()), wrapper);
        List<PurchaseOrderDTO> records = result.getRecords().stream().map(this::toDTO).toList();
        return new PageResult<>(records, result.getTotal(), query.pageNum(), query.pageSize());
    }

    private PurchaseOrderDTO toDTO(PurchaseOrderPO po) {
        List<PurchaseOrderItemPO> itemPOs = itemMapper.selectList(
                new LambdaQueryWrapper<PurchaseOrderItemPO>()
                        .eq(PurchaseOrderItemPO::getOrderId, po.getId()));
        List<PurchaseOrderItemDTO> items = itemPOs.stream().map(dtoConverter::toItemDTO).toList();
        PurchaseOrderDTO dto = dtoConverter.toDTO(po, items);
        dto.setStatusText(PurchaseOrderStatus.fromCode(po.getStatus()).description());
        return dto;
    }
}