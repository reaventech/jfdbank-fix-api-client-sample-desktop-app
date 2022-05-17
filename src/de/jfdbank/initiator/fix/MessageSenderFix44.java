/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.jfdbank.initiator.fix;

import quickfix.SessionID;
import de.jfdbank.initiator.Order;

public interface MessageSenderFix44 {
    
    void sendMessageToTarget(quickfix.Message message, SessionID sessionID);
    
    void sendMarketDataRequest(SessionID sessionID, String symbolText, boolean isSecurityID);
    
    void sendNewOrderSingleRequest(Order order);    

    void sendCancelOrderRequest(Order order);  
}
