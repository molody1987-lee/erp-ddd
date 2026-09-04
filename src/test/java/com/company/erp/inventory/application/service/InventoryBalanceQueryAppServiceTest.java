package com.company.erp.inventory.application.service;

import com.company.erp.inventory.application.dto.InventoryBalanceDTO;
import com.company.erp.inventory.application.query.InventoryBalanceQueryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InventoryBalanceQueryAppServiceTest {

    @Mock
    private InventoryBalanceQueryRepository queryRepository;
    @InjectMocks
    private InventoryBalanceQueryAppService queryService;

    @Test
    void shouldReturnBalance() {
        InventoryBalanceDTO dto = new InventoryBalanceDTO(1L, 100L, 1L,
                new BigDecimal("10"), "PCS", new BigDecimal("5.00"));
        when(queryRepository.findBalance(100L, 1L)).thenReturn(Optional.of(dto));

        Optional<InventoryBalanceDTO> result = queryService.getBalance(100L, 1L);

        assertThat(result).contains(dto);
    }

    @Test
    void shouldReturnEmptyWhenNotFound() {
        when(queryRepository.findBalance(100L, 1L)).thenReturn(Optional.empty());

        assertThat(queryService.getBalance(100L, 1L)).isEmpty();
    }
}