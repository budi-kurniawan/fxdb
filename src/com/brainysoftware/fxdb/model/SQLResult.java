package com.brainysoftware.fxdb.model;
/**
 * 
 * @author Budi Kurniawan (http://brainysoftware.com)
 * 
 */
public class SQLResult {
    public enum Status {
        SUCCESS_WITH_ROW_SET,
        SUCCESS_WITHOUT_ROW_SET,
        FAILED
    }
    
    private Status status;
    private String[][] rowSet;
    private String[] columnNames;
    private String errorMessage;
    private int rowsAffected;

    public SQLResult(Status status, String[][] rowSet, String[] columnNames,
            String errorMessage, int rowsAffected) {
        this.status = status;
        this.rowSet = rowSet;
        this.columnNames = columnNames;
        this.errorMessage = errorMessage;
        this.rowsAffected = rowsAffected;
    }
    public Status getStatus() {
        return status;
    }

    public String[][] getRowSet() {
        return rowSet;
    }

    public String[] getColumnNames() {
        return columnNames;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public int getRowsAffected() {
        return rowsAffected;
    }
}
