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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;

import javax.swing.JFrame;
import javax.swing.UIManager;
import org.quickfixj.jmx.JmxExporter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import quickfix.DefaultMessageFactory;
import quickfix.FileStoreFactory;
import quickfix.Initiator;
import quickfix.JdbcLogFactory;
import quickfix.JdbcStoreFactory;
import quickfix.LogFactory;
import quickfix.MessageFactory;
import quickfix.MessageStoreFactory;
import quickfix.ScreenLogFactory;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.SessionSettings;
import quickfix.SocketInitiator;
import de.jfdbank.initiator.swingui.FixInitiatorFrame;

/**
 * Entry point for the InitiatorDemoDesktopApplicationMain application.
 */
public class InitiatorDemoDesktopApplicationMain {
    private static final CountDownLatch shutdownLatch = new CountDownLatch(1);

    /** enable logging for this class */
    private static Logger log = LoggerFactory.getLogger(InitiatorDemoDesktopApplicationMain.class);
    private static InitiatorDemoDesktopApplicationMain initiatorDemoDesktopApplication;
    private boolean initiatorStarted = false;
    private Initiator initiator = null;
    private JFrame frame = null;
    
    public InitiatorDemoDesktopApplicationMain(String[] args) throws Exception {
        InputStream inputStream = null;
        if (args.length == 0) {
            inputStream = new BufferedInputStream( 
                            new FileInputStream( 
                                // when testing acceptor is running on staging
                                new File( "config/fix.initiator.client.JFDBankFIXAPI.staging.cfg" )
                            )
                        );
        } else if (args.length == 1) {
            inputStream = new FileInputStream(args[0]);
        }
        if (inputStream == null) {
            System.out.println("usage: " + InitiatorDemoDesktopApplicationMain.class.getName() + " [configFile].");
            return;
        }
        SessionSettings settings = new SessionSettings(inputStream);
        inputStream.close();
        
        boolean logHeartbeats = Boolean.valueOf(System.getProperty("logHeartbeats", "true"));
        
        OrderTableModel orderTableModel = new OrderTableModel();
        ExecutionTableModel executionTableModel = new ExecutionTableModel();
        InitiatorDemoDesktopApplication application = new InitiatorDemoDesktopApplication(orderTableModel, executionTableModel);
        
        
        //MessageStoreFactory messageStoreFactory = new FileStoreFactory(settings);
        MessageStoreFactory messageStoreFactory = new JdbcStoreFactory(settings);
        
        LogFactory logFactory = new ScreenLogFactory(true, true, true, logHeartbeats);
        //LogFactory logFactory = new JdbcLogFactory(settings);
        
        MessageFactory messageFactory = new DefaultMessageFactory();

        initiator = new SocketInitiator(application, messageStoreFactory, settings, logFactory, messageFactory);
        
        JmxExporter exporter = new JmxExporter();
        exporter.register(initiator);        

        frame = new FixInitiatorFrame(orderTableModel, executionTableModel, application);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public synchronized void logon() {
        if (!initiatorStarted) {
            try {
                initiator.start();
                initiatorStarted = true;
            } catch (Exception e) {
                log.error("Logon failed", e);
            }
        } else {
            Iterator<SessionID> sessionIds = initiator.getSessions().iterator();
            while (sessionIds.hasNext()) {
                SessionID sessionId = (SessionID) sessionIds.next();
                Session.lookupSession(sessionId).logon();
            }
        }
    }

    public void logout() {
        Iterator<SessionID> sessionIds = initiator.getSessions().iterator();
        while (sessionIds.hasNext()) {
            SessionID sessionId = (SessionID) sessionIds.next();
            Session.lookupSession(sessionId).logout("user requested");
        }
    }

    public void stop() {
        shutdownLatch.countDown();
    }

    public JFrame getFrame() {
        return frame;
    }

    public static InitiatorDemoDesktopApplicationMain get() {
        return initiatorDemoDesktopApplication;
    }

    public static void main(String args[]) throws Exception {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            log.info(e.getMessage(), e);
        }
        initiatorDemoDesktopApplication = new InitiatorDemoDesktopApplicationMain(args);
        if (!System.getProperties().containsKey("openfix")) {
            initiatorDemoDesktopApplication.logon();
        }
        shutdownLatch.await();
    }

}