package com.brainysoftware.fxdb.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * 
 * @author Budi Kurniawan (http://brainysoftware.com)
 * 
 */
public abstract class DAO {
    public static final String DB_LOCATION = "./data/default_db";
    public static final String DB_URL = "jdbc:derby:" + DB_LOCATION + ";create=true";
    public static final String DB_URL_NO_CREATE 
            = "jdbc:derby:" + DB_LOCATION + ";create=false";
    public static final String TABLE_NAME_BOOKS = "books";
    public static final String TABLE_NAME_VENDORS = "vendors";
    public static final String TABLE_NAME_ROYALTIES = "royalties";
    public static final String TABLE_NAME_CURRENCIES = "currencies";
    public static final String TABLE_NAME_EXCHANGE_RATES = "exchange_rates";
    public static final String TABLE_NAME_INPUT_FILES = "input_files";
    public static final String TABLE_NAME_EXPENSES = "expenses";
    public static final String TABLE_NAME_PRINT_EDITIONS = "print_editions";
    
    protected Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }
}
