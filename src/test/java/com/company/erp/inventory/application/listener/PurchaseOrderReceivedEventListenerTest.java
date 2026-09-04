package com.company.erp.inventory.application.listener;

import com.company.erp.inventory.application.acl.PurchaseOrderReceivedEventAcl;
import com.company.erp.inventory.application.command.ReceiveInventoryCommand;
import com.company.erp.inventory.application.service.InventoryReceiveApplicationService;
import com.company.erp.purchase.domain.event.PurchaseOrderReceivedEvent;
import com.company.erp.purchase.domain.valueobject.MaterialId;
import com.company.erp.purchase.domain.valueobject.Money;
import com.company.erp.purchase.domain.valueobject.Quantity;
import com.company.erp.purchase.domain.valueobject.ReceivedItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PurchaseOrderReceivedEventListenerTest {

    @Mock
    private PurchaseOrderReceivedEventAcl acl;

    @Mock
    private InventoryReceiveApplicationService service;

    @InjectMocks
    private PurchaseOrderReceivedEventListener listener;

    @Test
    void 消费事件应转换为命令并交给应用服务处理() {
        PurchaseOrderReceivedEvent event = new PurchaseOrderReceivedEvent(
                10L, 2L, "PO1",
                List.of(new ReceivedItem(new MaterialId(100L),
                        Quantity.of(new BigDecimal("10"), "PCS"),
                        Money.of(new BigDecimal("5.00"), "CNY"))));
        ReceiveInventoryCommand cmd = new ReceiveInventoryCommand(
                event.getEventId(), 10L, 100L, 2L,
                new BigDecimal("10"), "PCS", new BigDecimal("5.00"), "CNY");
        when(acl.toCommands(event)).thenReturn(List.of(cmd));

        listener.onMessage(event);

        verify(service).receive(List.of(cmd));
    }
}