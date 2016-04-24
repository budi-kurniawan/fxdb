package com.brainysoftware.fxdb.dao;

import java.util.List;

import com.brainysoftware.fxdb.model.DatabaseTableColumn;
import com.brainysoftware.fxdb.model.SQLResult;

/**
 * 
 * @author Budi Kurniawan (http://brainysoftware.com)
 * 
 */
public interface DatabaseDAO {
    boolean databaseExists() throws DAOException;
    void createTables() throws DAOException;
    List<String> getTableNames() throws DAOException;
    void deleteDatabase() throws DAOException;
    SQLResult executeSQL(String sql) throws DAOException;
    void updateTableCell(String tableName, int rowNum, int colNum, String newValue) 
            throws DAOException;
    void createTable(String tableName, List<DatabaseTableColumn> dbTableColumns) throws DAOException;
    void deleteTable(String tableName) throws DAOException;
}
