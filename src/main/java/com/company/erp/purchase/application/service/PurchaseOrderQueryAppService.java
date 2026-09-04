package com.company.erp.purchase.application.service;

import com.company.erp.purchase.application.dto.PurchaseOrderDTO;
import com.company.erp.purchase.application.query.PurchaseOrderQueryRepository;
import com.company.erp.purchase.domain.valueobject.PurchaseOrderId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * 采购订单查询应用服务。
 */
@Service
@RequiredArgsConstructor
public class PurchaseOrderQueryAppService {

    private final PurchaseOrderQueryRepository queryRepository;

    public Optional<PurchaseOrderDTO> getDetail(Long id) {
        return queryRepository.findDetail(new PurchaseOrderId(id));
    }

    public List<PurchaseOrderDTO> listByOrg(Long orgId) {
        return queryRepository.findByOrgId(orgId);
    }
}