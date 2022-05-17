/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.jfdbank.initiator;

import java.util.Observable;

public class ObservableOrder extends Observable {
    public void update(Order order) {
        setChanged();
        notifyObservers(order);
        clearChanged();
    }
}
