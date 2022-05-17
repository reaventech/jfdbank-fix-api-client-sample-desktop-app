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

import java.util.HashMap;
import java.util.Map;

public class OrderTIF {
    static private final Map<String, OrderTIF> known = new HashMap<>();
    static public final OrderTIF DAY = new OrderTIF("Day"); // 0
    static public final OrderTIF GTC = new OrderTIF("GTC"); // 1
    static public final OrderTIF OPG = new OrderTIF("OPG"); // 2
    static public final OrderTIF IOC = new OrderTIF("IOC"); // 3
    static public final OrderTIF FOC = new OrderTIF("FOC"); // 4
    static public final OrderTIF GTX = new OrderTIF("GTX"); // 5
    static public final OrderTIF GTD = new OrderTIF("GTD"); // 6

    static private final OrderTIF[] array = { DAY, IOC, FOC, OPG, GTC, GTX, GTD };

    private final String name;

    private OrderTIF(String name) {
        this.name = name;
        synchronized (OrderTIF.class) {
            known.put(name, this);
        }
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return name;
    }

    static public Object[] toArray() {
        return array;
    }

    public static OrderTIF parse(String type) throws IllegalArgumentException {
        OrderTIF result = known.get(type);
        if (result == null) {
            throw new IllegalArgumentException
            ("OrderTIF: " + type + " is unknown.");
        }
        return result;
    }
}