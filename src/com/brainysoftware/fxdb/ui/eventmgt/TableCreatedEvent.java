package com.brainysoftware.fxdb.ui.eventmgt;

import java.util.EventObject;

/**
 * 
 * @author Budi Kurniawan (http://brainysoftware.com)
 * 
 */
public class TableCreatedEvent extends EventObject {
    private static final long serialVersionUID = 1L;
    private String tableName;
    public TableCreatedEvent(Object source, String tableName) {
        super(source);
        this.tableName = tableName;
    }
    public String getTableName() {
        return tableName;
    }
}
