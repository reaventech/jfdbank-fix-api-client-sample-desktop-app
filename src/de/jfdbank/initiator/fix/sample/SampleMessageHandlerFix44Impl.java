/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.jfdbank.initiator.fix.sample;

import de.jfdbank.initiator.fix.MessageHandlerFix44;
import quickfix.FieldNotFound;
import quickfix.IncorrectTagValue;
import quickfix.SessionID;
import quickfix.UnsupportedMessageType;
import quickfix.field.BusinessRejectReason;
import quickfix.field.ClOrdID;
import quickfix.field.MsgType;
import quickfix.field.RefMsgType;
import de.jfdbank.initiator.InitiatorDemoDesktopApplication;
import de.jfdbank.initiator.Order;

public class SampleMessageHandlerFix44Impl implements MessageHandlerFix44 {
    private final InitiatorDemoDesktopApplication application;
    
    public SampleMessageHandlerFix44Impl(InitiatorDemoDesktopApplication application) {
        this.application = application;
    }    
    
    ////////////////////////////////////////////////////////////////////////
    // FIX.4.4
    ////////////////////////////////////////////////////////////////////////

    /**
     * OrderCancelReject handling for FIX.4.4 message
     * @param message
     * @param sessionID
     * @throws FieldNotFound
     * @throws UnsupportedMessageType
     * @throws IncorrectTagValue
     */
    public void onMessage( quickfix.fix44.OrderCancelReject message, 
            SessionID sessionID )
    	throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {
    
    }
    
    /**
     * ExecutionReport handling for FIX.4.4 message
     * @param message
     * @param sessionID
     * @throws FieldNotFound
     * @throws UnsupportedMessageType
     * @throws IncorrectTagValue
     */
    public void onMessage( quickfix.fix44.ExecutionReport message, 
            SessionID sessionID )
    	throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {
    
    }

    /**
     * BusinessMessageReject handling for FIX.4.4 message
     * @param message
     * @param sessionID
     * @throws FieldNotFound
     * @throws UnsupportedMessageType
     * @throws IncorrectTagValue
     */
    public void onMessage( quickfix.fix44.BusinessMessageReject message,
            SessionID sessionID )
    	throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {

        Order order = null;
        
        ClOrdID clOrdID = new ClOrdID();
        
        try {
            message.getField(clOrdID);
            
            order = this.application.getOrderTableModel().getOrder(clOrdID.getValue());
            
        } catch(FieldNotFound fnfe) {
            
        }
                        
        if (order == null) {
            return;
        }
        
        RefMsgType refMsgType = new RefMsgType();
        
        try {
            message.getField(refMsgType);
            
            
        } catch (FieldNotFound fnfe) {
            
        }
        
        BusinessRejectReason businessRejectReason = new BusinessRejectReason();
        
        try {
            message.getField(businessRejectReason);
            
            
        } catch (FieldNotFound fnfe) {
            
        }
        
        if (MsgType.ORDER_SINGLE.compareTo(refMsgType.getValue()) == 0) {
            
            order.setRejected(true);
            
            if (businessRejectReason.getValue() == BusinessRejectReason.OTHER) {
                
            }
            if (businessRejectReason.getValue() == BusinessRejectReason.UNKNOWN_ID) {
                
            }
            if (businessRejectReason.getValue() == BusinessRejectReason.UNKNOWN_SECURITY) {
                
            }
            if (businessRejectReason.getValue() == BusinessRejectReason.UNSUPPORTED_MESSAGE_TYPE) {
                
            }
            if (businessRejectReason.getValue() == BusinessRejectReason.APPLICATION_NOT_AVAILABLE) {
                
            }
            if (businessRejectReason.getValue() == BusinessRejectReason.CONDITIONALLY_REQUIRED_FIELD_MISSING) {
                
            }
            if (businessRejectReason.getValue() == BusinessRejectReason.NOT_AUTHORIZED) {
                
            }
            if (businessRejectReason.getValue() == BusinessRejectReason.DELIVERTO_FIRM_NOT_AVAILABLE_AT_THIS_TIME) {
                
            }
            if (businessRejectReason.getValue() == BusinessRejectReason.INVALID_PRICE_INCREMENT) {
                
            }
        }

        this.application.getOrderTableModel().updateOrder(order, message.getField(new ClOrdID()).getValue());
        this.application.getObservableOrder().update(order);
        
    }        
  
}
