package com.brainysoftware.fxdb.service;

import java.util.List;

import com.brainysoftware.fxdb.dao.DAOException;
import com.brainysoftware.fxdb.dao.DAOFactory;
import com.brainysoftware.fxdb.model.DatabaseTableColumn;

/**
 * 
 * @author Budi Kurniawan (http://brainysoftware.com)
 * 
 */
public class DatabaseService {
    public void createTable(String tableName, List<DatabaseTableColumn> dbTableColumns) 
    		throws Exception {
    	try {
        	DAOFactory.getDatabaseDAO().createTable(tableName, dbTableColumns);
    	} catch (DAOException e) {
    		throw new Exception(e.getMessage());
    	}
    }
    
    public void deleteTable(String tableName) throws Exception {
        try {
            DAOFactory.getDatabaseDAO().deleteTable(tableName);
        } catch (DAOException e) {
            throw new Exception(e.getMessage());
        }
    }
}
