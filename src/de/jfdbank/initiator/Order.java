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

import quickfix.SessionID;
import quickfix.field.OrdType;
import quickfix.field.Side;

public class Order implements Cloneable {
    private SessionID sessionID = null;
    private String symbolString = null;
    private String securityIDString = null;
    private String securityIDSourceString = null;
    private Double quantity = 0.0;
    private String currency = null;
    private String exDestinationString = null;
    private Character exDestinationIDSourceString;
    private Double open = 0.0;
    private double executed = 0;
    private OrderSide side = OrderSide.BUY;
    private OrderType type = OrderType.MARKET;
    private OrderTIF tif = OrderTIF.DAY;
    private char status;
    private Double limit = null;
    private Double stop = null;
    private double avgPx = 0.0;
    private boolean rejected = false;
    private boolean canceled = false;
    private boolean isNew = true;
    private String message = null;
    private String clOrdID = null;
    private String origClOrdID = null;
    private String orderID = null; // assigned by the acceptor sent in ExecutionReport
    private static int nextID = 1;

    public Order() {
        clOrdID = generateID();
    }

    public Order(String clOrdID) {
        this.clOrdID = clOrdID;
    }

    public Object clone() {
        try {
            Order order = (Order) super.clone();
            order.setOrigClOrdID(getClOrdID());
            order.setClOrdID(order.generateID());
            return order;
        } catch (CloneNotSupportedException e) {}
        return null;
    }

    public String generateID() {
        return Long.toString(System.currentTimeMillis() + (nextID++));
    }

    public SessionID getSessionID() {
        return sessionID;
    }

    public void setSessionID(SessionID sessionID) {
        this.sessionID = sessionID;
    }

    public String getSymbolString() {
        return symbolString;
    }

    public void setSymbolString(String symbolString) {
        this.symbolString = symbolString;
    }
    
    public String getSecurityIDString() {
        return securityIDString;
    }

    public void setSecurityIDString(String securityIDString) {
        this.securityIDString = securityIDString;
    }
    
    public String getSecurityIDSourceString() {
        return securityIDSourceString;
    }

    public void setSecurityIDSourceString(String securityIDSourceString) {
        this.securityIDSourceString = securityIDSourceString;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }
    
    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
    
    public String getExDestinationString() {
        return exDestinationString;
    }

    public void setExDestinationString(String exDestinationString) {
        this.exDestinationString = exDestinationString;
    }

    public Character getExDestinationIDSourceString() {
        return exDestinationIDSourceString;
    }

    public void setExDestinationIDSourceString(char exDestinationIDSourceString) {
        this.exDestinationIDSourceString = exDestinationIDSourceString;
    }    

    public Double getOpen() {
        return open;
    }

    public void setOpen(Double open) {
        this.open = open;
    }

    public double getExecuted() {
        return executed;
    }

    public void setExecuted(double executed) { this.executed = executed; }

    public OrderSide getSide() {
        return side;
    }

    public void setSide(OrderSide side) {
        this.side = side;
    }

    public OrderType getType() {
        return type;
    }

    public void setType(OrderType type) {
        this.type = type;
    }

    public OrderTIF getTIF() {
        return tif;
    }

    public void setTIF(OrderTIF tif) {
        this.tif = tif;
    }

    public Double getLimit() {
        return limit;
    }

    public void setLimit(Double limit) {
        this.limit = limit;
    }

    public void setLimit(String limit) {
        if (limit == null || limit.equals("")) {
            this.limit = null;
        } else {
            this.limit = Double.parseDouble(limit);
        }
    }

    public Double getStop() {
        return stop;
    }

    public void setStop(Double stop) {
        this.stop = stop;
    }

    public void setStop(String stop) {
        if (stop == null || stop.equals("")) {
            this.stop = null;
        } else {
            this.stop = Double.parseDouble(stop);
        }
    }

    public void setAvgPx(double avgPx) {
        this.avgPx = avgPx;
    }

    public double getAvgPx() {
        return avgPx;
    }

    public void setRejected(boolean rejected) {
        this.rejected = rejected;
    }

    public boolean getRejected() {
        return rejected;
    }

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }

    public boolean getCanceled() {
        return canceled;
    }

    public void setNew(boolean isNew) {
        this.isNew = isNew;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setClOrdID(String clOrdID) {
        this.clOrdID = clOrdID;
    }

    public String getClOrdID() {
        return clOrdID;
    }

    public void setOrigClOrdID(String origClOrdID) {
        this.origClOrdID = origClOrdID;
    }

    public String getOrigClOrdID() {
        return origClOrdID;
    }
    
    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getOrderID() {
        return orderID;
    }
    
    public String getStatus() {
        if (status == '0') return "New";
        if (status == '1') return "Partially filled";
        if (status == '2') return "Filled";
        if (status == '3') return "Done for day";
        if (status == '4') return "Canceled";
        if (status == '5') return "Replaced";
        if (status == '6') return "Pending Cancel";
        if (status == '7') return "Stopped";
        if (status == '8') return "Rejected";
        if (status == '9') return "Suspended";
        if (status == 'A') return "Pending New";
        if (status == 'B') return "Calculated";
        if (status == 'C') return "Expired";
        if (status == 'D') return "Accepted for bidding";
        if (status == 'E') return "Pending Replace";
        return "<UNKNOWN>";
     }
    
    public char getFIXStatus() {
        return status;
    }

    public void setStatus(char status) {
        this.status = status;
    }    
}