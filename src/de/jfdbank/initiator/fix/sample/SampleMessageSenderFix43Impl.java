/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.jfdbank.initiator.fix.sample;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import quickfix.CharField;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.SessionNotFound;
import quickfix.field.AggregatedBook;
import quickfix.field.ClOrdID;
import quickfix.field.LocateReqd;
import quickfix.field.MDEntryType;
import quickfix.field.MDReqID;
import quickfix.field.MDUpdateType;
import quickfix.field.MarketDepth;
import quickfix.field.OrdType;
import quickfix.field.OrderID;
import quickfix.field.OrderQty;
import quickfix.field.OrigClOrdID;
import quickfix.field.Price;
import quickfix.field.SecurityID;
import quickfix.field.SecurityIDSource;
import quickfix.field.StopPx;
import quickfix.field.SubscriptionRequestType;
import quickfix.field.Symbol;
import quickfix.field.TransactTime;
import de.jfdbank.initiator.InitiatorDemoDesktopApplication;
import de.jfdbank.initiator.Order;
import de.jfdbank.initiator.OrderSide;
import de.jfdbank.initiator.OrderType;
import de.jfdbank.initiator.fix.MessageSenderFix43;

public class SampleMessageSenderFix43Impl implements MessageSenderFix43 {

    private final InitiatorDemoDesktopApplication application;
    
    public SampleMessageSenderFix43Impl(InitiatorDemoDesktopApplication application) {
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
        MDReqID mDReqID = new MDReqID(Long.toString(System.currentTimeMillis()));
        
        // key: 263 SubscriptionRequestType, value: Snapshot (0)        
        // key: 263 SubscriptionRequestType, value: Snapshot + Updates (1)
        // key: 263 SubscriptionRequestType, value: Cancel subscription (2)
        SubscriptionRequestType subscriptionRequestType = new SubscriptionRequestType();
        subscriptionRequestType.setValue('1');
        
        // key: 264 MarketDepth, value: Top of book (1)
        // key: 264 MarketDepth, value: Full book (0)
        // Applicable only to order book snapshot requests. Should be ignored otherwise. ???
        MarketDepth marketDepth = new MarketDepth();
        marketDepth.setValue(1);
        
        quickfix.fix43.MarketDataRequest marketDataRequestMessage = new quickfix.fix43.MarketDataRequest(
                mDReqID, subscriptionRequestType, marketDepth);
        
        ///////////////////////////////////////////////////////////////////////
        
        // key: 265 MDUpdateType, value: Full Refresh (0)
        // key: 265 MDUpdateType, value: Incremental Refresh (1)
        MDUpdateType mdUpdateType = new MDUpdateType(0);
        marketDataRequestMessage.set(mdUpdateType);  
        
        // key: 266 AggregatedBook, value: one book entry per side per price (Y)
        // key: 266 AggregatedBook, value: Multiple entries per side per price allowed (N)
        AggregatedBook aggregatedBook = new AggregatedBook(false);
        marketDataRequestMessage.set(aggregatedBook);  
        
        // key: 269 MarketDepth, value: Bid (0) (Both should be specified)
        // key: 269 MarketDepth, value: Offer (1) (Both should be specified)
        MDEntryType mDEntryTypeBid = new MDEntryType();
        mDEntryTypeBid.setValue(MDEntryType.BID);        
        MDEntryType mDEntryTypeOffer = new MDEntryType();
        mDEntryTypeOffer.setValue(MDEntryType.OFFER);
        
        // key: 267 NoMDEntryTypes, value: 2 ??
        // IMPORTANT: in FIX.4.2 integer, in FIX.4.3+ NUMINGROUP
        quickfix.fix43.MarketDataRequest.NoMDEntryTypes noMDEntryTypesBid = new quickfix.fix43.MarketDataRequest.NoMDEntryTypes();        
        noMDEntryTypesBid.set(mDEntryTypeBid);

        quickfix.fix43.MarketDataRequest.NoMDEntryTypes noMDEntryTypesOffer = new quickfix.fix43.MarketDataRequest.NoMDEntryTypes();        
        noMDEntryTypesOffer.set(mDEntryTypeOffer);        
        
        marketDataRequestMessage.addGroup(noMDEntryTypesBid);
        marketDataRequestMessage.addGroup(noMDEntryTypesOffer);
        ///////////////////////////////////////////////////////////////////////
        
        // key: 146 NoRelatedSym, value: Number of symbols (instruments) requested. One symbol per MarketDataRequest is recommended
        quickfix.fix43.MarketDataRequest.NoRelatedSym noRelatedSym = new quickfix.fix43.MarketDataRequest.NoRelatedSym();
        if (! isSecurityID) {
            // key: 55 Symbol, value: 'EURUSD'
            Symbol symbol = new Symbol();
            symbol.setValue(symbolText);        
            noRelatedSym.set(symbol);
        }
        else {
            // key: 55 Symbol, value: 'EURUSD'
            SecurityID securityID = new SecurityID();
            securityID.setValue(symbolText);             
            noRelatedSym.set(securityID);    
            
            SecurityIDSource securityIDSource = new SecurityIDSource();
            securityIDSource.setValue("4"); 
            noRelatedSym.set(securityIDSource);    
        }
        
        marketDataRequestMessage.addGroup(noRelatedSym);        
        
//        // key: 460 Product, value: 4 CURRENCY, 5 EQUITY, 7 INDEX
//        // Indicates the type of product the security is associated with
//        // https://www.onixs.biz/fix-dictionary/4.3/tagNum_460.html        
//        //Product product = new Product(4);
//        //marketDataRequestMessage.set(product);
//        marketDataRequestMessage.setInt(460, 4);
        
        sendMessageToTarget(marketDataRequestMessage, sessionID);
    }    
    
    @Override
    public void sendNewOrderSingleRequest(Order order) {
        SessionID sessionID = order.getSessionID();
        
        quickfix.fix43.NewOrderSingle newOrderSingle = new quickfix.fix43.NewOrderSingle();
        
        // key: 100 ExDestination        
        if (order.getExDestinationString() != null && !order.getExDestinationString().isBlank()) {
            quickfix.field.ExDestination exDestination = new quickfix.field.ExDestination();
            exDestination.setValue(order.getExDestinationString());
            
            newOrderSingle.set(exDestination);
        }

//        // key: 1133 ExDestinationIDSource
//        if (order.getExDestinationIDSourceString() != null) {
//            quickfix.field.ExDestinationIDSource exDestinationIDSource = new quickfix.field.ExDestinationIDSource();
//            exDestinationIDSource.setValue(order.getExDestinationIDSourceString());
//            
//            newOrderSingle.set(exDestinationIDSource);
//        }

        quickfix.field.HandlInst handlInst = new quickfix.field.HandlInst();
        // only used in DECIDE – possible values:
        //- 1 = automatic processing
        //- 2 = manual processing
        //- 3 = manual processing
        handlInst.setValue(quickfix.field.HandlInst.AUTOMATED_EXECUTION_ORDER_PRIVATE_NO_BROKER_INTERVENTION);

        // unique identifier of the order – assigned by the client
        quickfix.field.ClOrdID clOrdID = new quickfix.field.ClOrdID();
        clOrdID.setValue(order.getClOrdID());
        
        if (order.getSymbolString() != null && !order.getSymbolString().isBlank()) {
            quickfix.field.Symbol symbol = new quickfix.field.Symbol();
            symbol.setValue(order.getSymbolString());
            
            newOrderSingle.set(symbol);
        }

        if (order.getSecurityIDString() != null && !order.getSecurityIDString().isBlank()) {
            quickfix.field.SecurityID securityID = new quickfix.field.SecurityID();
            securityID.setValue(order.getSecurityIDString());

            newOrderSingle.set(securityID);
        }
        if (order.getSecurityIDSourceString() != null && !order.getSecurityIDSourceString().isBlank()) {
            quickfix.field.SecurityIDSource securityIDSource = new quickfix.field.SecurityIDSource();
            securityIDSource.setValue(order.getSecurityIDSourceString());

            newOrderSingle.set(securityIDSource);
        }

        quickfix.field.Side side = this.application.sideToFIXSide(order.getSide());

        quickfix.field.OrderQty orderQty = new quickfix.field.OrderQty();
        orderQty.setValue(order.getQuantity());

        // date and time of execution – please see Note on Timestamp Fields
        quickfix.field.TransactTime transactTime = new quickfix.field.TransactTime();

        quickfix.field.OrdType ordType = this.application.typeToFIXType(order.getType());

        // status of order – see Field OrdStatus only mandatory for QUOTRIX!
        quickfix.field.OrdStatus ordStatus = new quickfix.field.OrdStatus();
        ordStatus.setValue(quickfix.field.OrdStatus.NEW);

        // IS NOT DEFINED in FIX 4.3
        // NOTE THAT: quickfix does not accept quickfix.field.OrdStatus in NewOrderSingle, so we set it as CharField
        quickfix.CharField ordStatusAsCharField = new quickfix.CharField(quickfix.field.OrdStatus.FIELD, quickfix.field.OrdStatus.NEW);

        // this is obtained from populateOrder() method
        if (order.getType() == OrderType.LIMIT) {
            newOrderSingle.setField(new Price(order.getLimit()));
        }
        else if (order.getType() == OrderType.STOP) {
            newOrderSingle.setField(new StopPx(order.getStop()));
        } else if (order.getType() == OrderType.STOP_LIMIT) {
            newOrderSingle.setField(new Price(order.getLimit()));
            newOrderSingle.setField(new StopPx(order.getStop()));
        }

        if (order.getSide() == OrderSide.SHORT_SELL || order.getSide() == OrderSide.SHORT_SELL_EXEMPT) {
            newOrderSingle.setField(new LocateReqd(false));
        }            

        // Identifies the currency used for price (3-character ISO 4217 currency code value)
        quickfix.field.Currency currency = new quickfix.field.Currency();
        if (order.getCurrency() != null && !order.getCurrency().isBlank()) {
            currency.setValue(order.getCurrency());  
        }

        // specifies how long the order remains in effect – see Field TimeInForce
        quickfix.field.TimeInForce timeInForce = this.application.tifToFIXTif(order.getTIF());

        // string field representing date of order expiration (the local market date at
        // which the order can be traded last), must always be given (independent of
        // TimeInForce) format: 'YYYYMMDD'. 
        // DECIDE accepts tag 126 too if 59=6 and tag 432 is missing
        quickfix.field.ExpireDate expireDate = new quickfix.field.ExpireDate();
        expireDate.setValue(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE).replaceAll("-", ""));

        newOrderSingle.set(handlInst);
        newOrderSingle.set(clOrdID);
        newOrderSingle.set(side);
        newOrderSingle.set(orderQty);
        newOrderSingle.set(transactTime);
        newOrderSingle.set(ordType);
        //newOrderSingle.setField(ordStatusAsCharField); // IS NOT DEFINED in FIX 4.3
        newOrderSingle.set(currency);
        newOrderSingle.set(timeInForce);
        newOrderSingle.set(expireDate);
        
        sendMessageToTarget(newOrderSingle, sessionID);
    }    
    
    @Override
    public void sendCancelOrderRequest(Order order) {
        
        quickfix.fix43.OrderCancelRequest message = new quickfix.fix43.OrderCancelRequest(
                new OrigClOrdID(order.getClOrdID()), 
                new ClOrdID(order.getClOrdID()),
                this.application.sideToFIXSide(order.getSide()), 
                new TransactTime());
        
        CharField ordTypeAsCharField = new CharField(OrdType.FIELD, this.application.typeToFIXType(order.getType()).getValue());
        message.setField(ordTypeAsCharField);
        message.setField(new OrderID(order.getOrderID()));
        message.setField(new OrderQty(order.getQuantity()));
        if (order.getSymbolString() != null && !order.getSymbolString().isBlank()) {
            message.setField(new Symbol(order.getSymbolString()));
        }
        if (order.getSecurityIDString() != null && !order.getSecurityIDString().isBlank()) {
            message.setField(new SecurityID(order.getSecurityIDString()));
        }
        if (order.getSecurityIDSourceString() != null && !order.getSecurityIDSourceString().isBlank()) {
            message.setField(new SecurityIDSource(order.getSecurityIDSourceString()));
        }

        //this.application.orderTableModel.addID(order, id);
        
        sendMessageToTarget(message, order.getSessionID());
    }            
}
