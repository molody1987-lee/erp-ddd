package com.company.erp.purchase.application.service;

import com.company.erp.purchase.application.dto.PageResult;
import com.company.erp.purchase.application.dto.PurchaseOrderDTO;
import com.company.erp.purchase.application.query.PurchaseOrderPageQuery;
import com.company.erp.purchase.application.query.PurchaseOrderQueryRepository;
import com.company.erp.purchase.domain.exception.DomainException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 采购订单查询服务（读模型）。
 */
@Service
@RequiredArgsConstructor
public class PurchaseOrderQueryService {

    private final PurchaseOrderQueryRepository queryRepository;

    public PurchaseOrderDTO getDetail(Long orderId) {
        return queryRepository.findById(orderId)
                .orElseThrow(() -> new DomainException("采购订单不存在: " + orderId));
    }

    public PageResult<PurchaseOrderDTO> page(PurchaseOrderPageQuery query) {
        return queryRepository.page(query);
    }
}