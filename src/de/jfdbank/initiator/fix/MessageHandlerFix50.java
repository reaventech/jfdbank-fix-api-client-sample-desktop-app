/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.jfdbank.initiator.fix;

import quickfix.FieldNotFound;
import quickfix.IncorrectTagValue;
import quickfix.SessionID;
import quickfix.UnsupportedMessageType;
import quickfix.fix50.OrderCancelReject;
import quickfix.fix50.ExecutionReport;

public interface MessageHandlerFix50 {

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
    void onMessage(OrderCancelReject message, SessionID sessionID) throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue;

    /**
     * ExecutionReport handling for FIX.5.0 message
     * @param message
     * @param sessionID
     * @throws FieldNotFound
     * @throws UnsupportedMessageType
     * @throws IncorrectTagValue
     */
    void onMessage(ExecutionReport message, SessionID sessionID) throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue;
    
    /**
     * BusinessMessageReject handling for FIX.5.0 message
     * @param message
     * @param sessionID
     * @throws FieldNotFound
     * @throws UnsupportedMessageType
     * @throws IncorrectTagValue
     */
    void onMessage(quickfix.fix50.BusinessMessageReject message, SessionID sessionID) throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue;           
}
