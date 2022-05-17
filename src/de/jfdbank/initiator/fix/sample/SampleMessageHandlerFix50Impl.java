/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.jfdbank.initiator.fix.sample;

import java.util.logging.Level;
import java.util.logging.Logger;
import de.jfdbank.initiator.fix.MessageHandlerFix50;
import quickfix.FieldNotFound;
import quickfix.IncorrectTagValue;
import quickfix.Message;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.SessionNotFound;
import quickfix.StringField;
import quickfix.UnsupportedMessageType;
import quickfix.field.AvgPx;
import quickfix.field.BusinessRejectReason;
import quickfix.field.ClOrdID;
import quickfix.field.CumQty;
import quickfix.field.DKReason;
import quickfix.field.ExecID;
import quickfix.field.ExecTransType;
import quickfix.field.ExecType;
import quickfix.field.LastPx;
import quickfix.field.LastQty;
import quickfix.field.LastShares;
import quickfix.field.LeavesQty;
import quickfix.field.MsgType;
import quickfix.field.OrdStatus;
import quickfix.field.OrdType;
import quickfix.field.OrderID;
import quickfix.field.OrderQty;
import quickfix.field.OrigClOrdID;
import quickfix.field.Price;
import quickfix.field.RefMsgType;
import quickfix.field.Side;
import quickfix.field.StopPx;
import quickfix.field.Symbol;
import quickfix.field.Text;
import de.jfdbank.initiator.InitiatorDemoDesktopApplication;
import de.jfdbank.initiator.Execution;
import de.jfdbank.initiator.Order;

public class SampleMessageHandlerFix50Impl implements MessageHandlerFix50 {
    
    private final InitiatorDemoDesktopApplication application;
    
    public SampleMessageHandlerFix50Impl(InitiatorDemoDesktopApplication application) {
        this.application = application;
    }
    
    ////////////////////////////////////////////////////////////////////////
    // FIX.5.0
    ////////////////////////////////////////////////////////////////////////

    /**
     * OrderCancelReject handling for FIX.5.0 message
     * @param message
     * @param sessionID
     * @throws FieldNotFound
     * @throws UnsupportedMessageType
     * @throws IncorrectTagValue
     */
    public void onMessage( quickfix.fix50.OrderCancelReject message, 
            SessionID sessionID )
    	throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {
    
        String clOrdIDString = message.getField(new ClOrdID()).getValue();
        Order order = application.getOrderTableModel().getOrder(clOrdIDString);
        if (order == null)
            return;
        if (order.getOrigClOrdID() != null)
            order = this.application.getOrderTableModel().getOrder(order.getOrigClOrdID());

        try {
            order.setMessage(message.getField(new Text()).getValue());
        } catch (FieldNotFound e) {
        }
        this.application.getOrderTableModel().updateOrder(order, message.getField(new OrigClOrdID()).getValue());
        
    }
    
    /**
     * ExecutionReport handling for FIX.5.0 message
     * @param message
     * @param sessionID
     * @throws FieldNotFound
     * @throws UnsupportedMessageType
     * @throws IncorrectTagValue
     */
    public void onMessage( quickfix.fix50.ExecutionReport message, 
            SessionID sessionID )
    	throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {
    
        String msgTypeString = null;
        String msgBeginString = null;
        String msgSenderCompID = null;
        try {
            msgTypeString = message.getHeader().getString(MsgType.FIELD);
            msgBeginString = message.getHeader().getString(quickfix.field.BeginString.FIELD);
            msgSenderCompID = message.getHeader().getString(quickfix.field.SenderCompID.FIELD);
            
            ExecID execID = (ExecID) message.getField(new ExecID());
            
//            if (alreadyProcessed(execID, sessionID))
//                return;

            Order order = this.application.getOrderTableModel().getOrder(message.getField(new ClOrdID()).getValue());
            if (order == null) {
                return;
            }
                        
            OrderID orderID = (OrderID)message.getField(new OrderID());
            order.setOrderID(orderID.getValue());
            
            // TODO: IMPORTANT: original identifier of the order as assigned in NewOrderSingle message by client in field ClOrdID
            String origClOrdIDString = order.getClOrdID();
            order.setOrigClOrdID(origClOrdIDString);
            
            StringField chgExDestinationCustomStringField = null;
            
            try {
                message.getField(new StringField(112233));
                
                if (chgExDestinationCustomStringField != null) {
                
                }
                            
            } catch(FieldNotFound exc) {
                
            }
            
            ExecTransType execTransTypeField = null;
            try {
                execTransTypeField = new ExecTransType();
                message.getField(execTransTypeField);
            } catch (FieldNotFound fnfe) {
                
            }
            
            ExecType execTypeField = null;
            try {
                execTypeField = new ExecType();
                
                message.getField(execTypeField);
                
            } catch (FieldNotFound fnfe) {
                
            }
            
            Symbol symbolField = null;
            try {
                symbolField = new Symbol();
                message.getField(symbolField);                
            } catch (FieldNotFound fnfe) {
                
            }
            
            OrderQty orderQtyField = null;
            try {
                orderQtyField = new OrderQty();
                message.getField(orderQtyField);
                
                order.setQuantity(orderQtyField.getValue());
            } catch (FieldNotFound fnfe) {
                
            }
            
            OrdType ordTypeField = null;
            try {
                ordTypeField = new OrdType();
                message.getField(ordTypeField);
            } catch (FieldNotFound fnfe) {
                
            }
            
            Price priceField = null;
            try {
                priceField = new Price();
                message.getField(priceField);
                
                order.setLimit(priceField.getValue());
            } catch (FieldNotFound fnfe) {
                // IMPORTANT:
                // normally, when this ExecutionReport is for OrderCancelReplaceRequest,
                // the price should be in the message.
            }
            
            StopPx stopPxField = null;
            try {
                stopPxField = new StopPx();
                message.getField(stopPxField);
                
                order.setStop(stopPxField.getValue());
            } catch (FieldNotFound fnfe) {
                
            }         
            
            // tag 63: before FIX50
            quickfix.field.SettlmntTyp settlmntTyp = null;            
            // tag 63: after FIX50
            quickfix.field.SettlType settlType = null;
            
            try {
                LeavesQty leavesQty = new LeavesQty();
                message.getField(leavesQty);
                
                order.setOpen(leavesQty.getValue());                
            } catch (FieldNotFound fnfe) {
                
            }            

            OrdStatus ordStatus = (OrdStatus) message.getField(new OrdStatus());
            
            order.setStatus(ordStatus.getValue());

            if (ordStatus.valueEquals(OrdStatus.NEW)) {
                Logger.getLogger(InitiatorDemoDesktopApplication.class.getName()).log(Level.INFO, "executionReport: OrdStatus.NEW");
                if (order.isNew()) {
                    order.setNew(false);
                }
            } else if (ordStatus.valueEquals(OrdStatus.PENDING_NEW)) {
                Logger.getLogger(InitiatorDemoDesktopApplication.class.getName()).log(Level.INFO, "executionReport: OrdStatus.PENDING_NEW");
                if (order.isNew()) {
                    order.setNew(false);
                }
            } else if (ordStatus.valueEquals(OrdStatus.PENDING_CANCEL)) {
                Logger.getLogger(InitiatorDemoDesktopApplication.class.getName()).log(Level.INFO, "executionReport: OrdStatus.PENDING_CANCEL");
                
            } else if (ordStatus.valueEquals(OrdStatus.CANCELED)) {
                Logger.getLogger(InitiatorDemoDesktopApplication.class.getName()).log(Level.INFO, "executionReport: OrdStatus.CANCELED");
                order.setCanceled(true);
                order.setOpen(0.0);
            } else if (ordStatus.valueEquals(OrdStatus.DONE_FOR_DAY)) {
                Logger.getLogger(InitiatorDemoDesktopApplication.class.getName()).log(Level.INFO, "executionReport: OrdStatus.DONE_FOR_DAY");
                order.setCanceled(true);
                order.setOpen(0.0);
            } else if (ordStatus.valueEquals(OrdStatus.PENDING_REPLACE) ||
                    ExecType.REPLACED == execTypeField.getValue()) {
                Logger.getLogger(InitiatorDemoDesktopApplication.class.getName()).log(Level.INFO, "executionReport: OrdStatus.PENDING_REPLACE");                
                
            } else if (ordStatus.valueEquals(OrdStatus.REPLACED)||
                    ExecType.REPLACED == execTypeField.getValue()) {                
                Logger.getLogger(InitiatorDemoDesktopApplication.class.getName()).log(Level.INFO, "executionReport: OrdStatus.REPLACED");
                
            } else if (ordStatus.valueEquals(OrdStatus.PARTIALLY_FILLED)) {
                Logger.getLogger(InitiatorDemoDesktopApplication.class.getName()).log(Level.INFO, "executionReport: OrdStatus.PARTIALLY_FILLED");
                
                double fillSize = 0;

                try {
                    LastQty lastQty = new LastQty();
                    message.getField(lastQty);
                    fillSize = lastQty.getValue();
                } catch (FieldNotFound fnf1) {
                    try {
                        LastShares lastShares = new LastShares();
                        message.getField(lastShares);
                        fillSize = lastShares.getValue();
                    } catch (FieldNotFound fnf2) {

                    }
                }
                if (fillSize > 0) {
                    // DK over the limit fills on buys
                    if (order.getType().getName().equals("Limit")) {
                        double limit = order.getLimit();
                        double lastPx = message.getField(new LastPx()).getValue();
                        String side = order.getSide().toString();
                        if ( side.equals("Buy") && lastPx > limit ){
                            try {
                                dk(message, DKReason.PRICE_EXCEEDS_LIMIT, 
                                        "Price exceeds limit", order);
                            } catch (Exception e) {}
                            return;
                        }
                    }
                    order.setOpen((order.getOpen() - fillSize));
                    order.setExecuted((int) message.getField(new CumQty()).getValue());
                    order.setAvgPx(message.getField(new AvgPx()).getValue());
                }
                
                if (fillSize > 0) {
                    Execution execution = new Execution();
                    execution.setExchangeID(sessionID + execID.getValue());
                    execution.setSymbol(symbolField.getValue());
                    execution.setQuantity(fillSize);
                    execution.setPrice(message.getField(new LastPx()).getValue());
                    Side side = (Side) message.getField(new Side());
                    execution.setSide(this.application.FIXSideToSide(side));
                    this.application.getExecutionTableModel().addExecution(execution);
                }
                                
            } else if (ordStatus.valueEquals(OrdStatus.FILLED)) {
                Logger.getLogger(InitiatorDemoDesktopApplication.class.getName()).log(Level.INFO, "executionReport: OrdStatus.FILLED");
                
                double fillSize = 0;
                try {
                    LastQty lastQty = new LastQty();
                    message.getField(lastQty);
                    fillSize = lastQty.getValue();
                } catch (FieldNotFound fnf1) {
                    try {
                        LastShares lastShares = new LastShares();
                        message.getField(lastShares);
                        fillSize = lastShares.getValue();
                    } catch (FieldNotFound fnf2) {

                    }
                }
                if (fillSize > 0) {
                    // DK over the limit fills on buys
                    if (order.getType().getName().equals("Limit")) {
                        double limit = order.getLimit();
                        double lastPx = message.getField(new LastPx()).getValue();
                        String side = order.getSide().toString();
                        if ( side.equals("Buy") && lastPx > limit ){
                            try {
                                dk(message, DKReason.PRICE_EXCEEDS_LIMIT, 
                                        "Price exceeds limit", order);
                            } catch (Exception e) {}
                            return;
                        }
                    }
                    order.setOpen((order.getOpen() - fillSize));
                    order.setExecuted((int) message.getField(new CumQty()).getValue());
                    order.setAvgPx(message.getField(new AvgPx()).getValue());
                }
                
                if (fillSize > 0) {
                    Execution execution = new Execution();
                    execution.setExchangeID(sessionID + execID.getValue());
                    execution.setSymbol(symbolField.getValue());
                    execution.setQuantity(fillSize);
                    execution.setPrice(message.getField(new LastPx()).getValue());
                    Side side = (Side) message.getField(new Side());
                    execution.setSide(this.application.FIXSideToSide(side));
                    this.application.getExecutionTableModel().addExecution(execution);
                }
                
            } else if (ordStatus.valueEquals(OrdStatus.REJECTED)) {
                Logger.getLogger(InitiatorDemoDesktopApplication.class.getName()).log(Level.INFO, "executionReport: OrdStatus.REJECTED");
                
                order.setRejected(true);
                order.setOpen(0.0);
            }

            try {
                order.setMessage(message.getField(new Text()).getValue());
            } catch (FieldNotFound e) {
            }

            this.application.getOrderTableModel().updateOrder(order, message.getField(new ClOrdID()).getValue());
            this.application.getObservableOrder().update(order);
            
        } catch (FieldNotFound ex) {
            Logger.getLogger(InitiatorDemoDesktopApplication.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
    
    private void dk(
            Message message, char dkReason, String reasonText, Order order) 
            throws FieldNotFound, SessionNotFound {

        OrderID orderID = (OrderID)message.getField(new OrderID());
        ExecID execID = (ExecID) message.getField(new ExecID());
        DKReason reason = new DKReason(dkReason);
        Symbol symbol = (Symbol) message.getField(new Symbol());
        Side side = (Side) message.getField(new Side());
        quickfix.fix42.DontKnowTrade dk = 
                new quickfix.fix42.DontKnowTrade(
                    orderID, execID, reason, symbol, side);
        Text text = new Text(reasonText);
        dk.set(text);
        Session.sendToTarget(dk, order.getSessionID());
    }
    
    /**
     * BusinessMessageReject handling for FIX.5.0 message
     * @param message
     * @param sessionID
     * @throws FieldNotFound
     * @throws UnsupportedMessageType
     * @throws IncorrectTagValue
     */
    public void onMessage( quickfix.fix50.BusinessMessageReject message,
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
