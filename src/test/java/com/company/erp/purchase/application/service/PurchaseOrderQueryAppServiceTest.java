package com.company.erp.purchase.application.service;

import com.company.erp.purchase.application.dto.PurchaseOrderDTO;
import com.company.erp.purchase.application.query.PurchaseOrderQueryRepository;
import com.company.erp.purchase.domain.valueobject.PurchaseOrderId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PurchaseOrderQueryAppServiceTest {

    @Mock
    private PurchaseOrderQueryRepository queryRepository;
    @InjectMocks
    private PurchaseOrderQueryAppService queryService;

    @Test
    void shouldReturnOrderDetail() {
        PurchaseOrderDTO dto = dto(1L);
        when(queryRepository.findDetail(new PurchaseOrderId(1L))).thenReturn(Optional.of(dto));

        Optional<PurchaseOrderDTO> result = queryService.getDetail(1L);

        assertThat(result).contains(dto);
    }

    @Test
    void shouldReturnEmptyWhenNotFound() {
        when(queryRepository.findDetail(new PurchaseOrderId(2L))).thenReturn(Optional.empty());

        assertThat(queryService.getDetail(2L)).isEmpty();
    }

    @Test
    void shouldQueryListByOrg() {
        List<PurchaseOrderDTO> list = List.of(dto(1L), dto(2L));
        when(queryRepository.findByOrgId(1L)).thenReturn(list);

        assertThat(queryService.listByOrg(1L)).hasSize(2);
        verify(queryRepository).findByOrgId(1L);
    }

    private PurchaseOrderDTO dto(Long id) {
        return new PurchaseOrderDTO(id, "PO-" + id, 10L, 1L, "DRAFT", List.of(), null);
    }
}