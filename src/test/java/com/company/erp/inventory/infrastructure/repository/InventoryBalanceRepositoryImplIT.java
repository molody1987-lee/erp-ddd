package com.company.erp.inventory.infrastructure.repository;

import com.company.erp.inventory.domain.aggregate.InventoryBalance;
import com.company.erp.inventory.domain.valueobject.InventoryQuantity;
import com.company.erp.support.AbstractIntegrationTest;
import com.company.erp.support.PersistenceTestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = PersistenceTestConfig.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
class InventoryBalanceRepositoryImplIT extends AbstractIntegrationTest {

    @Autowired
    private InventoryBalanceRepositoryImpl repository;

    @Test
    void shouldSaveAndQueryByMaterialAndOrg() {
        InventoryBalance balance = InventoryBalance.create(100L, 1L, "PCS");
        balance.receive(InventoryQuantity.of(new BigDecimal("10"), "PCS"), new BigDecimal("5.00"));

        Long id = repository.save(balance);

        assertThat(id).isNotNull();
        Optional<InventoryBalance> found = repository.findByMaterialAndOrg(100L, 1L);
        assertThat(found).isPresent();
        assertThat(found.orElseThrow().getQuantityOnHand()).isEqualByComparingTo(new BigDecimal("10"));
    }

    @Test
    void shouldLockExistingBalanceForUpdate() {
        InventoryBalance balance = InventoryBalance.create(100L, 2L, "PCS");
        repository.save(balance);

        Optional<InventoryBalance> locked = repository.findForUpdateByMaterialAndOrg(100L, 2L);

        assertThat(locked).isPresent();
    }

    @Test
    void shouldPersistQuantityAfterUpdate() {
        InventoryBalance balance = InventoryBalance.create(100L, 3L, "PCS");
        repository.save(balance);

        balance.receive(InventoryQuantity.of(new BigDecimal("5"), "PCS"), new BigDecimal("8.00"));
        repository.save(balance);

        assertThat(repository.findByMaterialAndOrg(100L, 3L).orElseThrow().getQuantityOnHand())
                .isEqualByComparingTo(new BigDecimal("5"));
    }
}