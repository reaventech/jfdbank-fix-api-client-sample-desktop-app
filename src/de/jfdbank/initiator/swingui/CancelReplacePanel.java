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

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import de.jfdbank.initiator.InitiatorDemoDesktopApplication;
import de.jfdbank.initiator.Order;



public class CancelReplacePanel extends JPanel {
    private JLabel quantityLabel = new JLabel("Quantity");
    private JLabel limitPriceLabel = new JLabel("Limit");
    private IntegerNumberTextField  quantityTextField =
        new IntegerNumberTextField();
    private DoubleNumberTextField limitPriceTextField =
        new DoubleNumberTextField();
    private JButton cancelButton = new JButton("Cancel");
    private Order order = null;

    private GridBagConstraints constraints = new GridBagConstraints();

    private InitiatorDemoDesktopApplication application;

    public CancelReplacePanel(final InitiatorDemoDesktopApplication application) {
        this.application = application;
        cancelButton.addActionListener(new CancelListener());

        setLayout(new GridBagLayout());
        createComponents();
    }

    public void addActionListener(ActionListener listener) {
        cancelButton.addActionListener(listener);
    }

    private void createComponents() {
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1;

        int x = 0;
        int y = 0;

        constraints.insets = new Insets(0, 0, 5, 5);
        add(cancelButton, x, y);

        constraints.weightx = 0;
        add(quantityLabel, ++x, y);
        constraints.weightx = 5;
        add(quantityTextField, ++x, y);
        constraints.weightx = 0;
        add(limitPriceLabel, ++x, y);
        constraints.weightx = 5;
        add(limitPriceTextField, ++x, y);
    }

    public void setEnabled(boolean enabled) {
        cancelButton.setEnabled(enabled);

        quantityTextField.setEnabled(enabled);
        limitPriceTextField.setEnabled(enabled);

        Color labelColor = enabled ? Color.black : Color.gray;
        Color bgColor = enabled ? Color.white : Color.gray;
        quantityTextField.setBackground(bgColor);
        limitPriceTextField.setBackground(bgColor);
        quantityLabel.setForeground(labelColor);
        limitPriceLabel.setForeground(labelColor);
    }

    public void update() {
        setOrder(this.order);
    }

    public void setOrder(Order order) {
        if(order == null)
            return;
        this.order = order;
        quantityTextField.setText
        (Double.toString(order.getOpen()));

        Double limit = order.getLimit();
        if(limit != null)
            limitPriceTextField.setText(order.getLimit().toString());
        setEnabled(order.getOpen() > 0);
    }

    private JComponent add(JComponent component, int x, int y) {
        constraints.gridx = x;
        constraints.gridy = y;
        add(component, constraints);
        return component;
    }

    private class CancelListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            application.sendCancelOrderRequest(order);
        }
    }
}
