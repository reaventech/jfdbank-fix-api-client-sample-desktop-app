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

package de.jfdbank.initiator.swingui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import quickfix.SessionID;
import de.jfdbank.initiator.InitiatorDemoDesktopApplication;
import de.jfdbank.initiator.LogonEvent;
import de.jfdbank.initiator.OrderTableModel;

public class MarketDataRequestEntryPanel extends JPanel implements Observer {
    private boolean symbolEntered = false;
    private boolean sessionEntered = false;

    private JTextField symbolTextField = new JTextField();

    private JComboBox sessionComboBox = new JComboBox();

    private JButton submitButton = new JButton("MarketDataRequest");

    private OrderTableModel orderTableModel = null;
    private transient InitiatorDemoDesktopApplication application = null;

    private GridBagConstraints constraints = new GridBagConstraints();

    public MarketDataRequestEntryPanel(final OrderTableModel orderTableModel,
                           final InitiatorDemoDesktopApplication application) {
        setName("MarketDataRequestEntryPanel");
        this.orderTableModel = orderTableModel;
        this.application = application;

        application.addLogonObserver(this);

        SubmitActivator activator = new SubmitActivator();
        symbolTextField.addKeyListener(activator);
        sessionComboBox.addItemListener(activator);

        setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        setLayout(new GridBagLayout());
        createComponents();
    }

    public void addActionListener(ActionListener listener) {
        submitButton.addActionListener(listener);
    }

    private void createComponents() {
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1;

        int x = 0;
        int y = 0;

        add(new JLabel("Symbol"), x, y);

        symbolTextField.setName("SymbolTextField");
        add(symbolTextField, x=0, ++y);

        ++x;
        
        constraints.insets = new Insets(3, 0, 0, 0);
        constraints.gridwidth = GridBagConstraints.RELATIVE;
        sessionComboBox.setName("SessionComboBox");
        add(sessionComboBox, 0, ++y);
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        submitButton.setName("MarketDataRequestButton");
        add(submitButton, x, y);

        submitButton.setEnabled(false);
        submitButton.addActionListener(new SubmitListener());
        activateSubmit();
    }

    private JComponent add(JComponent component, int x, int y) {
        constraints.gridx = x;
        constraints.gridy = y;
        add(component, constraints);
        return component;
    }

    private void activateSubmit() {
        boolean activate = symbolEntered 
                           && sessionEntered;

            submitButton.setEnabled(activate);
    }

    public void update(Observable o, Object arg) {
        LogonEvent logonEvent = (LogonEvent)arg;
        if(logonEvent.isLoggedOn())
            sessionComboBox.addItem(logonEvent.getSessionID());
        else
            sessionComboBox.removeItem(logonEvent.getSessionID());
    }

    private class SubmitListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {

            String symbolText = symbolTextField.getText();

            boolean isSecurityID = symbolText.length() == 12;
           
            SessionID sessionID = (SessionID)sessionComboBox.getSelectedItem();
            application.sendMarketDataRequest(sessionID, symbolText, isSecurityID);

        }
    }

    private class SubmitActivator
        implements KeyListener, ItemListener {
        public void keyReleased(KeyEvent e) {
            Object obj = e.getSource();
            if(obj == symbolTextField) {
                symbolEntered = testField(obj);
            }
            activateSubmit();
        }

        public void itemStateChanged(ItemEvent e) {
            sessionEntered = sessionComboBox.getSelectedItem() != null;
            activateSubmit();
        }

        private boolean testField(Object o) {
            String value = ((JTextField)o).getText();
            value = value.trim();
            return value.length() > 0;
        }

        public void keyTyped(KeyEvent e) {}
        public void keyPressed(KeyEvent e) {}
    }
}
