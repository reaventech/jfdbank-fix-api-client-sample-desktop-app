/*******************************************************************************
 * Copyright (c) quickfixengine.org  All rights reserved. 
 * 
 * This file is part of the QuickFIX FIX Engine 
 * 
 * This file may be distributed under the terms of the quickfixengine.org 
 * license as defined by quickfixengine.org and appearing in the file 
 * LICENSE included in the packaging of this file. 
 * 
 * This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING 
 * THE WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A 
 * PARTICULAR PURPOSE. 
 * 
 * See http://www.quickfixengine.org/LICENSE for licensing information. 
 * 
 * Contact ask@quickfixengine.org if any conditions of this licensing 
 * are not clear to you.
 ******************************************************************************/

package de.jfdbank.initiator;

import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import quickfix.Application;
import quickfix.DoNotSend;
import quickfix.FieldNotFound;
import quickfix.FixVersions;
import quickfix.IncorrectDataFormat;
import quickfix.IncorrectTagValue;
import quickfix.InvalidMessage;
import quickfix.Message;
import quickfix.RejectLogon;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.SessionNotFound;
import quickfix.UnsupportedMessageType;
import quickfix.field.LocateReqd;
import quickfix.field.MsgType;
import quickfix.field.OrdType;
import quickfix.field.OrderQty;
import quickfix.field.Price;
import quickfix.field.ResetSeqNumFlag;
import quickfix.field.Side;
import quickfix.field.StopPx;
import quickfix.field.TimeInForce;
import de.jfdbank.initiator.fix.sample.SampleMessageSenderFix43Impl;
import de.jfdbank.initiator.fix.sample.SampleMessageSenderFix44Impl;
import de.jfdbank.initiator.fix.sample.SampleMessageSenderFix50Impl;

public class InitiatorDemoDesktopApplication implements Application {
    
    public OrderTableModel orderTableModel = null;
    public ExecutionTableModel executionTableModel = null;
    private ObservableOrder observableOrder = new ObservableOrder();
    private ObservableLogon observableLogon = new ObservableLogon();
    private boolean isAvailable = true;
    private boolean isMissingField;
    
    static private TwoWayMap sideMap = new TwoWayMap();
    static private TwoWayMap typeMap = new TwoWayMap();
    static private TwoWayMap tifMap = new TwoWayMap();
    

    public InitiatorDemoDesktopApplication(OrderTableModel orderTableModel,
            ExecutionTableModel executionTableModel) {
        this.orderTableModel = orderTableModel;
        this.executionTableModel = executionTableModel;
    }
    
    public OrderTableModel getOrderTableModel() {
        return this.orderTableModel;
    }
    
    public ExecutionTableModel getExecutionTableModel() {
        return this.executionTableModel;
    }
    
    public ObservableOrder getObservableOrder() {
        return this.observableOrder;
    }

    @Override
    public void onCreate(SessionID sessionID) {
        SessionID sessID = sessionID;
        Session.lookupSession(sessionID).getLog().onEvent("onCreate() sessionID " + sessionID);
    }

    @Override
    public void onLogon(SessionID sessionID) {
        observableLogon.logon(sessionID);
    }

    @Override
    public void onLogout(SessionID sessionID) {
        observableLogon.logoff(sessionID);
    }

    /**
     * toAdmin provides you with a peek at the administrative messages that are 
     * being sent from your FIX engine to the counter party. 
     * This is normally not useful for an application however it is provided 
     * for any logging you may wish to do. 
     * Notice that the quickfix.Message is mutable. 
     * This allows you to add fields to an administrative message before it is sent out.
     * @param message
     * @param sessionID
     */
    @Override
    public void toAdmin(quickfix.Message message, SessionID sessionID) {
        
        String msgType = null;
        String msgBeginString = null;
        String senderCompIDString = null;
        try {
            msgType = message.getHeader().getString(MsgType.FIELD);
            msgBeginString = message.getHeader().getString(quickfix.field.BeginString.FIELD);
            senderCompIDString = message.getHeader().getString(quickfix.field.SenderCompID.FIELD);
        } catch (FieldNotFound ex) {
            Logger.getLogger(InitiatorDemoDesktopApplication.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if(MsgType.LOGON.compareTo(msgType) == 0 && FixVersions.BEGINSTRING_FIX43.compareTo(msgBeginString) == 0)
        {
            // for JFD TRADE FIX
            if (senderCompIDString.equals("JFD-TRADE-DEV-DESKTOP-CLIENT")) {
                message.setString(quickfix.field.Username.FIELD, "your_username");
                message.setString(quickfix.field.Password.FIELD, "your_password");
            }
            // for JFD PRICE FEED FIX
            else if (senderCompIDString.equals("JFD-PRICE-FEED-DEV-DESKTOP-CLIENT")) {
                message.setString(quickfix.field.Username.FIELD, "your_username");
                message.setString(quickfix.field.Password.FIELD, "your_password");
            }            
            
            // 141 ResetSeqNumFlag N Set to ‘Y’ to Indicate both sides of a FIX session should reset sequence numbers. Must be ‘Y’ on pricing session
            ResetSeqNumFlag resetSeqNumFlag = new ResetSeqNumFlag(true);
            message.setField(resetSeqNumFlag);
        }
        if(MsgType.LOGON.compareTo(msgType) == 0 && FixVersions.BEGINSTRING_FIX44.compareTo(msgBeginString) == 0)
        {
            // for JFD TRADE FIX
            if (senderCompIDString.equals("JFD-TRADE-DEV-DESKTOP-CLIENT")) {
                message.setString(quickfix.field.Username.FIELD, "your_username");
                message.setString(quickfix.field.Password.FIELD, "your_password");
            }
            // for JFD PRICE FEED FIX
            else if (senderCompIDString.equals("JFD-PRICE-FEED-DEV-DESKTOP-CLIENT")) {
                message.setString(quickfix.field.Username.FIELD, "your_username");
                message.setString(quickfix.field.Password.FIELD, "your_password");
            }
            
            // 141 ResetSeqNumFlag N Set to ‘Y’ to Indicate both sides of a FIX session should reset sequence numbers. Must be ‘Y’ on pricing session
            ResetSeqNumFlag resetSeqNumFlag = new ResetSeqNumFlag(true);
            message.setField(resetSeqNumFlag);
        }  
        if(MsgType.LOGON.compareTo(msgType) == 0 && FixVersions.BEGINSTRING_FIXT11.compareTo(msgBeginString) == 0)
        {
                        // for JFD TRADE FIX
            if (senderCompIDString.equals("JFD-TRADE-DEV-DESKTOP-CLIENT")) {
                message.setString(quickfix.field.Username.FIELD, "your_username");
                message.setString(quickfix.field.Password.FIELD, "your_password");
            }
            // for JFD PRICE FEED FIX
            else if (senderCompIDString.equals("JFD-PRICE-FEED-DEV-DESKTOP-CLIENT")) {
                message.setString(quickfix.field.Username.FIELD, "your_username");
                message.setString(quickfix.field.Password.FIELD, "your_password");
            }
            
            // 141 ResetSeqNumFlag N Set to ‘Y’ to Indicate both sides of a FIX session should reset sequence numbers. Must be ‘Y’ on pricing session
            ResetSeqNumFlag resetSeqNumFlag = new ResetSeqNumFlag(true);
            message.setField(resetSeqNumFlag);
        }          
    }

    /**
     * toApp is a callback for application messages that are being sent to a counterparty. 
     * If you throw a DoNotSend exception in this method, 
     * the application will not send the message. 
     * This is mostly useful if the application has been asked to resend a message 
     * such as an order that is no longer relevant for the current market. 
     * Messages that are being resent are marked with the PossDupFlag in the header set to true; 
     * If a DoNotSend exception is thrown and the flag is set to true, 
     * a sequence reset will be sent in place of the message. 
     * If it is set to false, the message will simply not be sent. 
     * Notice that the quickfix.Message is mutable. 
     * This allows you to add fields to an application message before it is sent out.
     * @param message
     * @param sessionID
     * @throws DoNotSend
     */
    @Override
    public void toApp(quickfix.Message message, SessionID sessionID) throws DoNotSend {
    }

    /**
     * fromAdmin notifies you when an administrative message is sent from a counterparty to your FIX engine. 
     * This can be useful for doing extra validation on Logon messages such as for checking passwords. 
     * Throwing a RejectLogon exception will disconnect the counterparty.
     * @param message
     * @param sessionID
     * @throws FieldNotFound
     * @throws IncorrectDataFormat
     * @throws IncorrectTagValue
     * @throws RejectLogon
     */
    @Override
    public void fromAdmin(quickfix.Message message, SessionID sessionID) throws FieldNotFound,
            IncorrectDataFormat, IncorrectTagValue, RejectLogon {
        
        String msgTypeString = null;
        String msgBeginString = null;
        String msgSenderCompID = null;
        try {
            msgTypeString = message.getHeader().getString(MsgType.FIELD);
            msgBeginString = message.getHeader().getString(quickfix.field.BeginString.FIELD);
            msgSenderCompID = message.getHeader().getString(quickfix.field.SenderCompID.FIELD);
        } catch (FieldNotFound ex) {
            Logger.getLogger(InitiatorDemoDesktopApplication.class.getName()).log(Level.SEVERE, null, ex);
        }             
    }

    /**
     * fromApp is one of the core entry points for your FIX application. 
     * Every application level request will come through here. 
     * If, for example, your application is a sell-side OMS, 
     * this is where you will get your new order requests. 
     * If you were a buy side, you would get your execution reports here. 
     * If a FieldNotFound exception is thrown, the counterparty will receive 
     * a reject indicating a conditionally required field is missing. 
     * The Message class will throw this exception when trying to retrieve 
     * a missing field, so you will rarely need the throw this explicitly. 
     * You can also throw an UnsupportedMessageType exception. 
     * This will result in the counterparty getting a reject informing them 
     * your application cannot process those types of messages. 
     * An IncorrectTagValue can also be thrown if a field contains a value that is out of range or you do not support.
     * @param message
     * @param sessionID
     * @throws FieldNotFound
     * @throws IncorrectDataFormat
     * @throws IncorrectTagValue
     * @throws UnsupportedMessageType
     */
    @Override
    public void fromApp(quickfix.Message message, SessionID sessionID) throws FieldNotFound,
            IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType {
        
        
        
        try {
            SwingUtilities.invokeLater(new MessageProcessor(this, message, sessionID));
        } catch (Exception e) {
        }
    }    
    
    public Message createMessageFromString(String messageString) {
        Message message = new Message();
        try {
            char DEFAULT_DELIMETER = ' ';
            char SOH_DELIMETER = (char) 0x01;
    
            messageString = messageString.replace(DEFAULT_DELIMETER, SOH_DELIMETER);
    
            message.fromString(messageString, null, true);            
        } catch (InvalidMessage ex) {
            Logger.getLogger(InitiatorDemoDesktopApplication.class.getName()).log(Level.SEVERE, null, ex);
        }
        return message;
    }

    public void sendMessageToTarget(quickfix.Message message, SessionID sessionID) {
        try {
            Session.sendToTarget(message, sessionID);
        } catch (SessionNotFound e) {
            System.out.println(e);
        }
    }
    
    public void sendMarketDataRequest(SessionID sessionID, String symbolText, boolean isSecurityID) {
        String msgBeginString = null;
        String msgTargetCompID = null;
        try {
            msgBeginString = sessionID.getBeginString();
            msgTargetCompID = sessionID.getTargetCompID();
        } catch (Exception ex) {
            Logger.getLogger(InitiatorDemoDesktopApplication.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        switch (msgBeginString) {
            case FixVersions.BEGINSTRING_FIX43:
                SampleMessageSenderFix43Impl sampleMessageSenderFix43Impl = new SampleMessageSenderFix43Impl(this);
                sampleMessageSenderFix43Impl.sendMarketDataRequest(sessionID, symbolText, isSecurityID);
                break;
            case FixVersions.BEGINSTRING_FIX44:
                SampleMessageSenderFix44Impl sampleMessageSenderFix44Impl = new SampleMessageSenderFix44Impl(this);
                sampleMessageSenderFix44Impl.sendMarketDataRequest(sessionID, symbolText, isSecurityID);
                break;
            case FixVersions.BEGINSTRING_FIXT11:
                SampleMessageSenderFix50Impl sampleMessageSenderFix50Impl = new SampleMessageSenderFix50Impl(this);
                sampleMessageSenderFix50Impl.sendMarketDataRequest(sessionID, symbolText, isSecurityID);
                break;
        }
    }       

    public void sendNewOrderSingleRequest(Order order) {
        
        String msgBeginString = null;
        String msgTargetCompID = null;
        try {
            msgBeginString = order.getSessionID().getBeginString();
            msgTargetCompID = order.getSessionID().getTargetCompID();
        } catch (Exception ex) {
            Logger.getLogger(InitiatorDemoDesktopApplication.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        switch (msgBeginString) {
            case FixVersions.BEGINSTRING_FIX43:
                SampleMessageSenderFix43Impl sampleMessageSenderFix43Impl = new SampleMessageSenderFix43Impl(this);
                sampleMessageSenderFix43Impl.sendNewOrderSingleRequest(order);                    
                break;
            case FixVersions.BEGINSTRING_FIX44:
                SampleMessageSenderFix44Impl sampleMessageSenderFix44Impl = new SampleMessageSenderFix44Impl(this);
                sampleMessageSenderFix44Impl.sendNewOrderSingleRequest(order);                     
                break;
            case FixVersions.BEGINSTRING_FIXT11:
                SampleMessageSenderFix50Impl sampleMessageSenderFix50Impl = new SampleMessageSenderFix50Impl(this);
                sampleMessageSenderFix50Impl.sendNewOrderSingleRequest(order);                     
                break;
        }
    }     
    
    public void sendCancelOrderRequest(Order order) {
        String msgBeginString = null;
        String msgTargetCompID = null;
        try {
            msgBeginString = order.getSessionID().getBeginString();
            msgTargetCompID = order.getSessionID().getTargetCompID();
        } catch (Exception ex) {
            Logger.getLogger(InitiatorDemoDesktopApplication.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        switch (msgBeginString) {
            case FixVersions.BEGINSTRING_FIX43:
                SampleMessageSenderFix43Impl sampleMessageSenderFix43Impl = new SampleMessageSenderFix43Impl(this);
                sampleMessageSenderFix43Impl.sendCancelOrderRequest(order);                    
                break;
            case FixVersions.BEGINSTRING_FIX44:
                SampleMessageSenderFix44Impl sampleMessageSenderFix44Impl = new SampleMessageSenderFix44Impl(this);
                sampleMessageSenderFix44Impl.sendCancelOrderRequest(order);                     
                break;
            case FixVersions.BEGINSTRING_FIXT11:
                SampleMessageSenderFix50Impl sampleMessageSenderFix50Impl = new SampleMessageSenderFix50Impl(this);
                sampleMessageSenderFix50Impl.sendCancelOrderRequest(order);                     
                break;
        }        
    }  
    
    public quickfix.Message populateOrder(Order order, quickfix.Message newOrderSingle) {

        OrderType type = order.getType();

        if (type == OrderType.LIMIT)
            newOrderSingle.setField(new Price(order.getLimit()));
        else if (type == OrderType.STOP) {
            newOrderSingle.setField(new StopPx(order.getStop()));
        } else if (type == OrderType.STOP_LIMIT) {
            newOrderSingle.setField(new Price(order.getLimit()));
            newOrderSingle.setField(new StopPx(order.getStop()));
        }

        if (order.getSide() == OrderSide.SHORT_SELL
                || order.getSide() == OrderSide.SHORT_SELL_EXEMPT) {
            newOrderSingle.setField(new LocateReqd(false));
        }

        newOrderSingle.setField(tifToFIXTif(order.getTIF()));
        return newOrderSingle;
    }     
    
    public Message populateCancelReplace(Order order, Order newOrder, quickfix.Message message) {

        if (order.getQuantity() != newOrder.getQuantity())
            message.setField(new OrderQty(newOrder.getQuantity()));
        if (!order.getLimit().equals(newOrder.getLimit()))
            message.setField(new Price(newOrder.getLimit()));
        return message;
    }

    public Side sideToFIXSide(OrderSide side) {
        return (Side) sideMap.getFirst(side);
    }

    public OrderSide FIXSideToSide(Side side) {
        return (OrderSide) sideMap.getSecond(side);
    }

    public OrdType typeToFIXType(OrderType type) {
        return (OrdType) typeMap.getFirst(type);
    }

    public OrderType FIXTypeToType(OrdType type) {
        return (OrderType) typeMap.getSecond(type);
    }

    public TimeInForce tifToFIXTif(OrderTIF tif) {
        return (TimeInForce) tifMap.getFirst(tif);
    }

    public OrderTIF FIXTifToTif(TimeInForce tif) {
        return (OrderTIF) typeMap.getSecond(tif);
    }

    public void addLogonObserver(Observer observer) {
        observableLogon.addObserver(observer);
    }

    public void deleteLogonObserver(Observer observer) {
        observableLogon.deleteObserver(observer);
    }

    public void addOrderObserver(Observer observer) {
        observableOrder.addObserver(observer);
    }

    public void deleteOrderObserver(Observer observer) {
        observableOrder.deleteObserver(observer);
    }


    static {
        sideMap.put(OrderSide.BUY, new Side(Side.BUY));
        sideMap.put(OrderSide.SELL, new Side(Side.SELL));
        sideMap.put(OrderSide.SHORT_SELL, new Side(Side.SELL_SHORT));
        sideMap.put(OrderSide.SHORT_SELL_EXEMPT, new Side(Side.SELL_SHORT_EXEMPT));
        sideMap.put(OrderSide.CROSS, new Side(Side.CROSS));
        sideMap.put(OrderSide.CROSS_SHORT, new Side(Side.CROSS_SHORT));

        typeMap.put(OrderType.MARKET, new OrdType(OrdType.MARKET));
        typeMap.put(OrderType.LIMIT, new OrdType(OrdType.LIMIT));
        typeMap.put(OrderType.STOP, new OrdType(OrdType.STOP_STOP_LOSS));
        typeMap.put(OrderType.STOP_LIMIT, new OrdType(OrdType.STOP_LIMIT));

        tifMap.put(OrderTIF.DAY, new TimeInForce(TimeInForce.DAY));
        tifMap.put(OrderTIF.GTC, new TimeInForce(TimeInForce.GOOD_TILL_CANCEL));
        tifMap.put(OrderTIF.OPG, new TimeInForce(TimeInForce.AT_THE_OPENING));
        tifMap.put(OrderTIF.IOC, new TimeInForce(TimeInForce.IMMEDIATE_OR_CANCEL));        
        tifMap.put(OrderTIF.FOC, new TimeInForce(TimeInForce.FILL_OR_KILL));        
        tifMap.put(OrderTIF.GTX, new TimeInForce(TimeInForce.GOOD_TILL_CROSSING));
        tifMap.put(OrderTIF.GTD, new TimeInForce(TimeInForce.GOOD_TILL_DATE));
    }

    public boolean isMissingField() {
        return isMissingField;
    }

    public void setMissingField(boolean isMissingField) {
        this.isMissingField = isMissingField;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean isAvailable) {
        this.isAvailable = isAvailable;
    }
}
