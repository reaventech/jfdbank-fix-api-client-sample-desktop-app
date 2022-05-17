/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.jfdbank.initiator.fix.sample;

import quickfix.Session;
import quickfix.SessionID;
import quickfix.SessionNotFound;
import quickfix.field.ClOrdID;
import quickfix.field.HandlInst;
import quickfix.field.OrderQty;
import quickfix.field.OrigClOrdID;
import quickfix.field.Symbol;
import quickfix.field.TransactTime;
import de.jfdbank.initiator.InitiatorDemoDesktopApplication;
import de.jfdbank.initiator.Order;
import de.jfdbank.initiator.fix.MessageSenderFix44;

public class SampleMessageSenderFix44Impl implements MessageSenderFix44 {

    private final InitiatorDemoDesktopApplication application;
    
    public SampleMessageSenderFix44Impl(InitiatorDemoDesktopApplication application) {
        this.application = application;
    }
    
    @Override
    public void sendMessageToTarget(quickfix.Message message, SessionID sessionID) {
        try {
            Session.sendToTarget(message, sessionID);
        } catch (SessionNotFound e) {
            System.out.println(e);
        }
    }

    @Override
    public void sendMarketDataRequest(SessionID sessionID, String symbolText, boolean isSecurityID) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
     
    @Override
    public void sendNewOrderSingleRequest(Order order) {
        quickfix.fix44.NewOrderSingle newOrderSingle = new quickfix.fix44.NewOrderSingle(
                new ClOrdID(order.getClOrdID()), 
                this.application.sideToFIXSide(order.getSide()),
                new TransactTime(), 
                this.application.typeToFIXType(order.getType()));
        
        newOrderSingle.set(new OrderQty(order.getQuantity()));
        newOrderSingle.set(new Symbol(order.getSymbolString()));
        newOrderSingle.set(new HandlInst('1'));
        
        sendMessageToTarget(this.application.populateOrder(order, newOrderSingle), order.getSessionID());
    }
        
    @Override
    public void sendCancelOrderRequest(Order order) {
        String id = order.generateID();
        quickfix.fix44.OrderCancelRequest message = new quickfix.fix44.OrderCancelRequest(
                new OrigClOrdID(order.getClOrdID()), 
                new ClOrdID(id),
                this.application.sideToFIXSide(order.getSide()), 
                new TransactTime());
        
        message.setField(new OrderQty(order.getQuantity()));
        message.setField(new Symbol(order.getSymbolString()));

        this.application.orderTableModel.addID(order, id);
        
        sendMessageToTarget(message, order.getSessionID());
    }    
}
