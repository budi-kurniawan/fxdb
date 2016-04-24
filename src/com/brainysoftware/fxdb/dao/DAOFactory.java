package com.brainysoftware.fxdb.dao;

/**
 * 
 * @author Budi Kurniawan (http://brainysoftware.com)
 * 
 */
public class DAOFactory {
    public static DatabaseDAO getDatabaseDAO() {
        return new DatabaseDAOImpl();
    }
}