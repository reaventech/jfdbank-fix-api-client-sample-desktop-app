/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.jfdbank.initiator.fix.sample;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.SessionNotFound;
import quickfix.field.AggregatedBook;
import quickfix.field.LocateReqd;
import quickfix.field.MDEntryType;
import quickfix.field.MDReqID;
import quickfix.field.MDUpdateType;
import quickfix.field.MarketDepth;
import quickfix.field.Price;
import quickfix.field.StopPx;
import quickfix.field.SubscriptionRequestType;
import quickfix.field.Symbol;
import de.jfdbank.initiator.InitiatorDemoDesktopApplication;
import de.jfdbank.initiator.Order;
import de.jfdbank.initiator.OrderSide;
import de.jfdbank.initiator.OrderType;
import de.jfdbank.initiator.fix.MessageSenderFix50;

public class SampleMessageSenderFix50Impl implements MessageSenderFix50 {

    private final InitiatorDemoDesktopApplication application;
    
    public SampleMessageSenderFix50Impl(InitiatorDemoDesktopApplication application) {
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

        MDReqID mDReqID = new MDReqID("testMDReqID");
        
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
        
        quickfix.fix50.MarketDataRequest marketDataRequestMessage = new quickfix.fix50.MarketDataRequest(
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
        quickfix.fix50.MarketDataRequest.NoMDEntryTypes noMDEntryTypesBid = new quickfix.fix50.MarketDataRequest.NoMDEntryTypes();        
        noMDEntryTypesBid.set(mDEntryTypeBid);

        quickfix.fix50.MarketDataRequest.NoMDEntryTypes noMDEntryTypesOffer = new quickfix.fix50.MarketDataRequest.NoMDEntryTypes();        
        noMDEntryTypesOffer.set(mDEntryTypeOffer);        
        
        marketDataRequestMessage.addGroup(noMDEntryTypesBid);
        marketDataRequestMessage.addGroup(noMDEntryTypesOffer);
        ///////////////////////////////////////////////////////////////////////
        
        // key: 146 NoRelatedSym, value: Number of symbols (instruments) requested. One symbol per MarketDataRequest is recommended
        quickfix.fix50.MarketDataRequest.NoRelatedSym noRelatedSym = new quickfix.fix50.MarketDataRequest.NoRelatedSym();
        // key: 55 Symbol, value: 'EURUSD'
        Symbol symbol = new Symbol();
        symbol.setValue(symbolText);        
        noRelatedSym.set(symbol);
        
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
        
        quickfix.fix50.NewOrderSingle newOrderSingle = new quickfix.fix50.NewOrderSingle();
        
        // key: 100 ExDestination
        quickfix.field.ExDestination exDestination = new quickfix.field.ExDestination();
        exDestination.setValue(order.getExDestinationString());

        // key: 1133 ExDestinationIDSource
        quickfix.field.ExDestinationIDSource exDestinationIDSource = new quickfix.field.ExDestinationIDSource();
        exDestinationIDSource.setValue(order.getExDestinationIDSourceString());


        quickfix.field.HandlInst handlInst = new quickfix.field.HandlInst();
        // only used in DECIDE – possible values:
        //- 1 = automatic processing
        //- 2 = manual processing
        //- 3 = manual processing
        handlInst.setValue(quickfix.field.HandlInst.AUTOMATED_EXECUTION_ORDER_PRIVATE_NO_BROKER_INTERVENTION);

        // unique identifier of the order – assigned by the client
        quickfix.field.ClOrdID clOrdID = new quickfix.field.ClOrdID();
        clOrdID.setValue(order.getClOrdID());

        quickfix.field.Symbol symbol = new quickfix.field.Symbol();
        symbol.setValue(order.getSymbolString());

        if (!order.getSecurityIDString().isBlank()) {
            quickfix.field.SecurityID securityID = new quickfix.field.SecurityID();
            securityID.setValue(order.getSecurityIDString());

            newOrderSingle.set(securityID);
        }
        if (!order.getSecurityIDSourceString().isBlank()) {
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

        newOrderSingle.set(exDestination);
        newOrderSingle.set(exDestinationIDSource);
        newOrderSingle.set(handlInst);
        newOrderSingle.set(clOrdID);
        newOrderSingle.set(symbol);
        newOrderSingle.set(side);
        newOrderSingle.set(orderQty);
        newOrderSingle.set(transactTime);
        newOrderSingle.set(ordType);
        newOrderSingle.setField(ordStatusAsCharField);
        newOrderSingle.set(currency);
        newOrderSingle.set(timeInForce);
        newOrderSingle.set(expireDate);
        
        sendMessageToTarget(newOrderSingle, sessionID);
    }    
    
    @Override
    public void sendCancelOrderRequest(Order order) {
        SessionID sessionID = order.getSessionID();
        
        quickfix.fix50.OrderCancelRequest cancelRequest = new quickfix.fix50.OrderCancelRequest();
        
        // key: 100 ExDestination
        quickfix.field.ExDestination exDestination = new quickfix.field.ExDestination();
        exDestination.setValue(order.getExDestinationString());

        // key: 1133 ExDestinationIDSource
        quickfix.field.ExDestinationIDSource exDestinationIDSource = new quickfix.field.ExDestinationIDSource();
        exDestinationIDSource.setValue(order.getExDestinationIDSourceString());

        // unique identifier of the order as assigned by trading venue – must be
        // the identical value of OrderID from preceding ExecutionReport – accept Order
        quickfix.field.OrderID orderID = new quickfix.field.OrderID();
        orderID.setValue(order.getOrderID());            

        // original identifier of the order as assigned in NewOrderSingle message by client in field ClOrdID
        quickfix.field.OrigClOrdID origClOrdID = new quickfix.field.OrigClOrdID();
        origClOrdID.setValue(order.getOrigClOrdID());

        // unique identifier of the order – assigned by the client
        quickfix.field.ClOrdID clOrdID = new quickfix.field.ClOrdID();
        clOrdID.setValue(order.getClOrdID());

        quickfix.field.Symbol symbol = new quickfix.field.Symbol();
        symbol.setValue(order.getSymbolString());

        if (!order.getSecurityIDString().isBlank()) {
            quickfix.field.SecurityID securityID = new quickfix.field.SecurityID();
            securityID.setValue(order.getSecurityIDString());

            cancelRequest.set(securityID);
        }
        if (!order.getSecurityIDSourceString().isBlank()) {
            quickfix.field.SecurityIDSource securityIDSource = new quickfix.field.SecurityIDSource();
            securityIDSource.setValue(order.getSecurityIDSourceString());

            cancelRequest.set(securityIDSource);
        }

        quickfix.field.Side side = this.application.sideToFIXSide(order.getSide());

        quickfix.field.OrderQty orderQty = new quickfix.field.OrderQty();
        orderQty.setValue(order.getQuantity());

        // date and time of execution – please see Note on Timestamp Fields
        quickfix.field.TransactTime transactTime = new quickfix.field.TransactTime();

        quickfix.field.OrdType ordType = this.application.typeToFIXType(order.getType());

        cancelRequest.setField(exDestination);
        cancelRequest.set(orderID);
        cancelRequest.set(origClOrdID);
        cancelRequest.set(clOrdID);
        cancelRequest.set(symbol);
        cancelRequest.set(side);
        cancelRequest.setField(ordType);
        cancelRequest.set(orderQty);
        cancelRequest.set(transactTime);      
        
        sendMessageToTarget(cancelRequest, sessionID);        

        this.application.orderTableModel.addID(order, order.getClOrdID());
    }          
}
