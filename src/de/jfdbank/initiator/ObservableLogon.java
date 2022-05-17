/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.jfdbank.initiator;

import java.util.Observable;
import quickfix.SessionID;


public class ObservableLogon extends Observable {
    public void logon(SessionID sessionID) {
        setChanged();
        notifyObservers(new LogonEvent(sessionID, true));
        clearChanged();
    }

    public void logoff(SessionID sessionID) {
        setChanged();
        notifyObservers(new LogonEvent(sessionID, false));
        clearChanged();
    }
}
