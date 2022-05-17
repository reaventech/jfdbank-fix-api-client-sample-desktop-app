/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.jfdbank.initiator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import quickfix.DefaultMessageFactory;
import quickfix.FieldNotFound;
import quickfix.FixVersions;
import quickfix.Message;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.SessionNotFound;
import quickfix.StringField;
import quickfix.field.AvgPx;
import quickfix.field.BeginString;
import quickfix.field.BusinessRejectReason;
import quickfix.field.ClOrdID;
import quickfix.field.CumQty;
import quickfix.field.DKReason;
import quickfix.field.DeliverToCompID;
import quickfix.field.ExecID;
import quickfix.field.ExecTransType;
import quickfix.field.ExecType;
import quickfix.field.LastPx;
import quickfix.field.LastQty;
import quickfix.field.LastShares;
import quickfix.field.LeavesQty;
import quickfix.field.MsgSeqNum;
import quickfix.field.MsgType;
import quickfix.field.OrdStatus;
import quickfix.field.OrdType;
import quickfix.field.OrderID;
import quickfix.field.OrderQty;
import quickfix.field.OrigClOrdID;
import quickfix.field.Price;
import quickfix.field.RefMsgType;
import quickfix.field.RefSeqNum;
import quickfix.field.SenderCompID;
import quickfix.field.SessionRejectReason;
import quickfix.field.Side;
import quickfix.field.StopPx;
import quickfix.field.Symbol;
import quickfix.field.TargetCompID;
import quickfix.field.Text;
import de.jfdbank.initiator.fix.sample.SampleMessageHandlerFix43Impl;
import de.jfdbank.initiator.fix.sample.SampleMessageHandlerFix44Impl;
import de.jfdbank.initiator.fix.sample.SampleMessageHandlerFix50Impl;
import de.jfdbank.initiator.fix.jfd.JFDTradeMessageHandlerFix43Impl;


public class MessageProcessor implements Runnable {
        private final quickfix.Message message;
        private final SessionID sessionID;
        
        private final InitiatorDemoDesktopApplication application;
        private DefaultMessageFactory messageFactory = new DefaultMessageFactory();
        static private HashMap<SessionID, HashSet<ExecID>> execIDs = new HashMap<SessionID, HashSet<ExecID>>();

        public MessageProcessor(InitiatorDemoDesktopApplication application, quickfix.Message message, SessionID sessionID) {
            this.message = message;
            this.sessionID = sessionID;
            this.application = application;
        }

        public void run() {
            try {
                MsgType msgType = new MsgType();
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
        
                if (this.application.isAvailable()) {
                    if (this.application.isMissingField()) {
                        // For OpenFIX certification testing
                        sendBusinessReject(message, BusinessRejectReason.CONDITIONALLY_REQUIRED_FIELD_MISSING, "Conditionally required field missing");
                    }
                    else {
                        if (FixVersions.BEGINSTRING_FIX43.compareTo(msgBeginString) == 0) {                            
                            if (MsgType.BUSINESS_MESSAGE_REJECT.compareTo(msgTypeString) == 0) {
                                quickfix.fix43.BusinessMessageReject businessMessageReject = (quickfix.fix43.BusinessMessageReject)message;

                                SampleMessageHandlerFix43Impl impl = new SampleMessageHandlerFix43Impl(this.application);

                                impl.onMessage(businessMessageReject, sessionID);
                            }
                            if (MsgType.ORDER_CANCEL_REJECT.compareTo(msgTypeString) == 0) {
                                quickfix.fix43.OrderCancelReject orderCancelReject = (quickfix.fix43.OrderCancelReject)message;

                                SampleMessageHandlerFix43Impl impl = new SampleMessageHandlerFix43Impl(this.application);

                                impl.onMessage(orderCancelReject, sessionID);
                            }                                
                            if (MsgType.EXECUTION_REPORT.compareTo(msgTypeString) == 0) {
                                quickfix.fix43.ExecutionReport executionReport = (quickfix.fix43.ExecutionReport)message;

                                SampleMessageHandlerFix43Impl impl = new SampleMessageHandlerFix43Impl(this.application);

                                impl.onMessage(executionReport, sessionID);
                            }
                        }  
                        if (FixVersions.BEGINSTRING_FIX44.compareTo(msgBeginString) == 0) {                            
                            if (MsgType.BUSINESS_MESSAGE_REJECT.compareTo(msgTypeString) == 0) {
                                quickfix.fix44.BusinessMessageReject businessMessageReject = (quickfix.fix44.BusinessMessageReject)message;

                                SampleMessageHandlerFix44Impl impl = new SampleMessageHandlerFix44Impl(this.application);

                                impl.onMessage(businessMessageReject, sessionID);
                            }
                            if (MsgType.ORDER_CANCEL_REJECT.compareTo(msgTypeString) == 0) {
                                quickfix.fix44.OrderCancelReject orderCancelReject = (quickfix.fix44.OrderCancelReject)message;

                                SampleMessageHandlerFix44Impl impl = new SampleMessageHandlerFix44Impl(this.application);

                                impl.onMessage(orderCancelReject, sessionID);
                            }                                
                            if (MsgType.EXECUTION_REPORT.compareTo(msgTypeString) == 0) {
                                quickfix.fix44.ExecutionReport executionReport = (quickfix.fix44.ExecutionReport)message;

                                SampleMessageHandlerFix44Impl impl = new SampleMessageHandlerFix44Impl(this.application);

                                impl.onMessage(executionReport, sessionID);
                            }
                        }                          
                        if (FixVersions.BEGINSTRING_FIXT11.compareTo(msgBeginString) == 0) {
                            if (MsgType.BUSINESS_MESSAGE_REJECT.compareTo(msgTypeString) == 0) {
                                quickfix.fix50.BusinessMessageReject businessMessageReject = (quickfix.fix50.BusinessMessageReject)message;

                                SampleMessageHandlerFix50Impl impl = new SampleMessageHandlerFix50Impl(this.application);

                                impl.onMessage(businessMessageReject, sessionID);
                            }
                            if (MsgType.ORDER_CANCEL_REJECT.compareTo(msgTypeString) == 0) {
                                quickfix.fix50.OrderCancelReject orderCancelReject = (quickfix.fix50.OrderCancelReject)message;

                                SampleMessageHandlerFix50Impl impl = new SampleMessageHandlerFix50Impl(this.application);

                                impl.onMessage(orderCancelReject, sessionID);
                            }                                
                            if (MsgType.EXECUTION_REPORT.compareTo(msgTypeString) == 0) {
                                quickfix.fix50.ExecutionReport executionReport = (quickfix.fix50.ExecutionReport)message;

                                SampleMessageHandlerFix50Impl impl = new SampleMessageHandlerFix50Impl(this.application);

                                impl.onMessage(executionReport, sessionID);
                            }      
                        }
                    }
                } else {
                    sendBusinessReject(message, BusinessRejectReason.APPLICATION_NOT_AVAILABLE, "Application not available");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        private void sendSessionReject(Message message, int rejectReason) throws FieldNotFound,
            SessionNotFound {
            Message reply = createMessage(message, MsgType.REJECT);
            reverseRoute(message, reply);
            String refSeqNum = message.getHeader().getString(MsgSeqNum.FIELD);
            reply.setString(RefSeqNum.FIELD, refSeqNum);
            reply.setString(RefMsgType.FIELD, message.getHeader().getString(MsgType.FIELD));
            reply.setInt(SessionRejectReason.FIELD, rejectReason);
            Session.sendToTarget(reply);
        }

        private void sendBusinessReject(Message message, int rejectReason, String rejectText)
                throws FieldNotFound, SessionNotFound {
            Message reply = createMessage(message, MsgType.BUSINESS_MESSAGE_REJECT);
            reverseRoute(message, reply);
            String refSeqNum = message.getHeader().getString(MsgSeqNum.FIELD);
            reply.setString(RefSeqNum.FIELD, refSeqNum);
            reply.setString(RefMsgType.FIELD, message.getHeader().getString(MsgType.FIELD));
            reply.setInt(BusinessRejectReason.FIELD, rejectReason);
            reply.setString(Text.FIELD, rejectText);
            Session.sendToTarget(reply);
        }
        
        private void reverseRoute(Message message, Message reply) throws FieldNotFound {
            reply.getHeader().setString(SenderCompID.FIELD,
                    message.getHeader().getString(TargetCompID.FIELD));
            reply.getHeader().setString(TargetCompID.FIELD,
                    message.getHeader().getString(SenderCompID.FIELD));
        }

        private boolean alreadyProcessed(ExecID execID, SessionID sessionID) {
            HashSet<ExecID> set = execIDs.get(sessionID);
            if (set == null) {
                set = new HashSet<>();
                set.add(execID);
                execIDs.put(sessionID, set);
                return false;
            } else {
                if (set.contains(execID))
                    return true;
                set.add(execID);
                return false;
            }
        }    
        
        private Message createMessage(Message message, String msgType) throws FieldNotFound {
            return messageFactory.create(message.getHeader().getString(BeginString.FIELD), msgType);
        }
    }
