package com.brainysoftware.fxdb.ui.eventmgt;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Budi Kurniawan (http://brainysoftware.com)
 * 
 */
public class EventManager {
    
    private static EventManager instance;
    private List<TableDeleteAttemptedListener> tableDeleteAttemptedListeners = 
            new ArrayList<>();
    private List<TableCreatedListener> tableCreatedListeners = new ArrayList<>();
    
    private EventManager() {
    }
    
    public static EventManager getInstance() {
        if (instance == null) {
            synchronized(EventManager.class) {
                if (instance == null) {
                    instance = new EventManager();
                }
            }
        }
        return instance;
    }
    
    public void registerTableDeleteAttemptedListener(TableDeleteAttemptedListener listener) {
        tableDeleteAttemptedListeners.add(listener);
    }
    
    public void fireTableDeleteAttemptedEvent(TableDeleteAttemptedEvent event) {
        tableDeleteAttemptedListeners.stream().forEach(listener -> {
            listener.tableDeleteAttempted(event);
        });
    }
    
    public void registerTableCreatedListener(TableCreatedListener listener) {
        tableCreatedListeners.add(listener);
    }
    
    public void fireTableCreatedEvent(TableCreatedEvent event) {
        tableCreatedListeners.stream().forEach(listener -> {
            listener.tableCreated(event);
        });
    }
    
}