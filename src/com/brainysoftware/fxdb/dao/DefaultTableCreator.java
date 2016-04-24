package com.brainysoftware.fxdb.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 
 * @author Budi Kurniawan (http://brainysoftware.com)
 * 
 */
public class DefaultTableCreator {
    public void createTables() throws DAOException {
        createVendorsTable();
        createBooksTable();
        createInputFilesTable();
        createExchangeRatesTable();
        createExpensesTable();
        createPrintEditionsTable();
    }
    
    private void createVendorsTable() {
        // id column has been changed to be not an identity
        String sql = "CREATE TABLE " + DAO.TABLE_NAME_VENDORS
                + " (id INT NOT NULL GENERATED ALWAYS AS IDENTITY, "
                + " name VARCHAR(200) UNIQUE,"
                + " currency VARCHAR(5))";
        createTable(sql);
    }
    
    private void createInputFilesTable() {
        String sql = "CREATE TABLE " + DAO.TABLE_NAME_INPUT_FILES
                + " (id INT NOT NULL GENERATED ALWAYS AS IDENTITY, "
                + "name VARCHAR(200) UNIQUE)";
        createTable(sql);
    }
    
    private void createBooksTable() {
        String sql = "CREATE TABLE " + DAO.TABLE_NAME_BOOKS
                + " (id INT NOT NULL GENERATED ALWAYS AS IDENTITY, "
                + "isbn VARCHAR(20) UNIQUE, "
                + "isbn10 VARCHAR(20), "
                + "asin VARCHAR(20), " // some books have no ASIN, so ASIN cannot be unique
                + "type CHAR(1), " // p (print) or e (electronic)
                + "title VARCHAR(200), "
                + "subtitle VARCHAR(200), "
                + "description VARCHAR(400), "
                + "price DECIMAL(5, 2), "
                + "pub_date DATE)";
        createTable(sql);
        
    }
    
   private void createExchangeRatesTable() {
        String sql = "CREATE TABLE " + DAO.TABLE_NAME_EXCHANGE_RATES
                + " (currency VARCHAR(5),"
                + "\"year\" INT," // use INT bec. java.time.YearMonth.getYear() returns int
                + "\"month\" SMALLINT, " // use INT bec. java.time.YearMonth.getMonth() returns int
                + "rate DECIMAL(10, 6),"
                + "PRIMARY KEY (currency, \"year\", \"month\"))";
        createTable(sql);
    }    
    
    private void createExpensesTable() {
        String sql = "CREATE TABLE " + DAO.TABLE_NAME_EXPENSES
                + " (id INT NOT NULL GENERATED ALWAYS AS IDENTITY, "
                + "book_id INT NOT NULL, "
                + "currency VARCHAR(5),"
                + "amount DECIMAL(15, 4), "
                + "\"year\" INT, " // use INT bec. java.time.YearMonth.getYear() returns int
                + "period SMALLINT, " // use INT bec. java.time.YearMonth.getMonthValue() returns int
                + "description VARCHAR(200),"
                + "input_file_id INT, "
                + "input_line_number INT, "
                + "insert_time TIMESTAMP NOT NULL)";
        createTable(sql);
    }

    private void createPrintEditionsTable() {
        String sql = "CREATE TABLE " + DAO.TABLE_NAME_PRINT_EDITIONS
                + " (id INT NOT NULL GENERATED ALWAYS AS IDENTITY, "
                + "book_id INT NOT NULL, "
                + "currency VARCHAR(5),"
                + "\"year\" INT, " // use INT bec. java.time.YearMonth.getYear() returns int
                + "free_quantity INT, "
                + "ship_quantity INT, "
                + "return_quantity INT, "
                + "ship_value DECIMAL(15, 4), "
                + "return_value DECIMAL(15, 4), "
                + "input_file_id INT, "
                + "input_line_number INT, "
                + "insert_time TIMESTAMP NOT NULL)";
        createTable(sql);
    }

    private void createTable(String sql) {
        try (Connection connection = DriverManager.getConnection(DAO.DB_URL);
                Statement statement = connection.createStatement()) {
            statement.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}