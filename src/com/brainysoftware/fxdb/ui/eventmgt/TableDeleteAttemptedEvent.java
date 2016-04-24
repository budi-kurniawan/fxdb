package com.brainysoftware.fxdb.ui.eventmgt;

import java.util.EventObject;

/**
 * 
 * @author Budi Kurniawan (http://brainysoftware.com)
 * 
 */
public class TableDeleteAttemptedEvent extends EventObject {
    private static final long serialVersionUID = 1L;
    private String tableName;
    public TableDeleteAttemptedEvent(Object source, String tableName) {
        super(source);
        this.tableName = tableName;
    }
    public String getTableName() {
        return tableName;
    }
}
