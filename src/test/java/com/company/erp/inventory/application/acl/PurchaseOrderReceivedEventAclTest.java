package com.company.erp.inventory.application.acl;

import com.company.erp.inventory.application.command.ReceiveInventoryCommand;
import com.company.erp.purchase.domain.event.PurchaseOrderReceivedEvent;
import com.company.erp.purchase.domain.valueobject.MaterialId;
import com.company.erp.purchase.domain.valueobject.Money;
import com.company.erp.purchase.domain.valueobject.Quantity;
import com.company.erp.purchase.domain.valueobject.ReceivedItem;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PurchaseOrderReceivedEventAclTest {

    private final PurchaseOrderReceivedEventAcl acl = new PurchaseOrderReceivedEventAcl();

    @Test
    void 应将采购入库事件转换为库存接收命令() {
        PurchaseOrderReceivedEvent event = new PurchaseOrderReceivedEvent(
                10L, 2L, "PO1",
                List.of(new ReceivedItem(new MaterialId(100L),
                        Quantity.of(new BigDecimal("10"), "PCS"),
                        Money.of(new BigDecimal("5.00"), "CNY"))));

        List<ReceiveInventoryCommand> commands = acl.toCommands(event);

        assertThat(commands).hasSize(1);
        ReceiveInventoryCommand cmd = commands.get(0);
        assertThat(cmd.eventId()).isEqualTo(event.getEventId());
        assertThat(cmd.orderId()).isEqualTo(10L);
        assertThat(cmd.orgId()).isEqualTo(2L);
        assertThat(cmd.materialId()).isEqualTo(100L);
        assertThat(cmd.quantity()).isEqualByComparingTo(new BigDecimal("10"));
        assertThat(cmd.unit()).isEqualTo("PCS");
        assertThat(cmd.unitCost()).isEqualByComparingTo(new BigDecimal("5.00"));
        assertThat(cmd.currency()).isEqualTo("CNY");
    }

    @Test
    void 多明细应转换为多条命令() {
        PurchaseOrderReceivedEvent event = new PurchaseOrderReceivedEvent(
                10L, 2L, "PO1",
                List.of(
                        new ReceivedItem(new MaterialId(100L),
                                Quantity.of(new BigDecimal("10"), "PCS"),
                                Money.of(new BigDecimal("5.00"), "CNY")),
                        new ReceivedItem(new MaterialId(200L),
                                Quantity.of(new BigDecimal("2"), "PCS"),
                                Money.of(new BigDecimal("3.00"), "CNY"))));

        assertThat(acl.toCommands(event)).hasSize(2);
    }
}