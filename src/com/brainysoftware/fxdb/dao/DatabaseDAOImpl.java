package com.brainysoftware.fxdb.dao;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import com.brainysoftware.fxdb.model.DatabaseTableColumn;
import com.brainysoftware.fxdb.model.SQLResult;

/**
 * 
 * @author Budi Kurniawan (http://brainysoftware.com)
 * 
 */
public class DatabaseDAOImpl extends DAO implements DatabaseDAO {

    @Override
    public boolean databaseExists() throws DAOException {
        try (Connection connection = DriverManager.getConnection(DB_URL_NO_CREATE)) {
            return (connection != null);
        } catch (SQLException ex) {
            return false;
        }
    }

    @Override
    public void createTables() throws DAOException {
        if (databaseExists()) {
            throw new DAOException("Database already exists");
        }
        new DefaultTableCreator().createTables();
    }

    @Override
    public List<String> getTableNames() throws DAOException {
        String sql = "SELECT tablename FROM SYS.SYSTABLES WHERE tabletype='T'";
        List<String> tableNames = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(DB_URL);
                PreparedStatement pStatement = connection.prepareStatement(sql);
                ResultSet resultSet = pStatement.executeQuery()) {
            while (resultSet.next()) {
                tableNames.add(resultSet.getString(1));
            }
        } catch (SQLException ex) {
        }
        return tableNames;
    }
    
    @Override
    public void deleteDatabase() throws DAOException {
        // TODO
        // cannot create db right after deleteDatabase()
        // maybe should delete all the tables prior to deleting the directory
        try {
            Files.delete(Paths.get(DB_LOCATION));
        } catch (IOException ex) {
            throw new DAOException("Error deleting database: " + ex.getMessage());
        }
    }
    
    @Override
    public SQLResult executeSQL(String sql) throws DAOException {
        SQLResult sqlResult = null;
        if (sql.trim().toUpperCase().startsWith("SELECT ")) {
            try (Connection connection = getConnection();
                    Statement statement = connection.createStatement();
                    ResultSet resultSet = statement.executeQuery(sql)) {
                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();
                String[] columnNames = new String[columnCount];
                for (int i = 0; i < columnCount; i++) {
                    columnNames[i] = metaData.getColumnName(i + 1);
                }
                List<String[]> rows = new ArrayList<>();
                int rowCount = 0;
                while (resultSet.next()) {
                    String[] row = new String[columnCount];
                    for (int i = 0; i < columnCount; i++) {
                        row[i] = resultSet.getString(i + 1);
                    }
                    rowCount++;
                    rows.add(row);
                }
                String[][] rowSet = new String[rowCount][columnCount];
                rowSet = rows.toArray(rowSet);
                sqlResult = new SQLResult(SQLResult.Status.SUCCESS_WITH_ROW_SET,
                        rowSet,
                        columnNames,
                        null, 0);
            } catch (SQLException e) {
                sqlResult = new SQLResult(SQLResult.Status.FAILED,
                    null, null, e.getMessage(), -1);
            }
        } else {
            try (Connection connection = getConnection();
                    Statement statement = connection.createStatement()) {
                int columnsAffected = statement.executeUpdate(sql);
                sqlResult = new SQLResult(SQLResult.Status.SUCCESS_WITHOUT_ROW_SET, 
                        null, null, null, columnsAffected);
            } catch (SQLException e) {
                sqlResult = new SQLResult(SQLResult.Status.FAILED,
                    null, null, e.getMessage(), -1);
            }
        }
        return sqlResult;
    }
    
    public void updateTableCell(String tableName, int rowNumber, 
            int columnNumber, String newValue) throws DAOException { // rowNum is 0-based
        // bec. Derby doesn't support ROW_ID, use updatable scrollable ResutlSet
        // https://db.apache.org/derby/docs/10.9/devguide/rdevconceptssur.html
        try (Connection connection = getConnection();
                Statement statement = connection.createStatement(
                        ResultSet.TYPE_SCROLL_INSENSITIVE, 
                        ResultSet.CONCUR_UPDATABLE);
                ResultSet resultSet = statement.executeQuery("SELECT * FROM " + tableName)) {
            resultSet.absolute(rowNumber + 1);
            resultSet.updateString(columnNumber + 1, newValue);
            resultSet.updateRow();
        } catch (SQLException e) {
            throw new DAOException(e.getMessage());
        }
    }
    
    public void createTable(String tableName, List<DatabaseTableColumn> dbTableColumns) throws DAOException {
    	List<String> primaryKeys = new ArrayList<>();
    	StringBuilder sb = new StringBuilder(1000);
    	sb.append("CREATE TABLE " + tableName + " (");
    	for (DatabaseTableColumn column : dbTableColumns) {
    		String colName = column.getName();
    		if (colName == null || colName.isEmpty()) {
    			continue;
    		}
    		sb.append(colName + " ");
    		String colType = column.getType();
    		switch (colType) {
    		case "Number":
    			sb.append("INT ");
    			break;
    		case "String":
    			sb.append("VARCHAR(255) ");
    			break;
    		default:
    			sb.append("VARCHAR(255) ");
    			break;
    		}
    		if (!column.isAllowNull()) {
    			sb.append("NOT NULL ");
    		}
    		if (column.isAutoGenerate()) {
    			sb.append("GENERATED ALWAYS AS IDENTITY ");
    		}
    		if (column.isPrimaryKey()) {
    			primaryKeys.add(column.getName());
    		}
    		sb.append(",");
    	}
    	if (primaryKeys.size() > 0) {
    		sb.append("PRIMARY KEY (");
    		for (String key : primaryKeys) {
    			sb.append(key + ",");
    		}
        	// remove last comma
        	sb.deleteCharAt(sb.length() - 1);
        	sb.append("),");
    	}
    	// remove last comma
    	sb.deleteCharAt(sb.length() - 1);
		sb.append(")");
        try (Connection connection = getConnection();
                Statement statement = connection.createStatement()) {
        	System.out.println("execute sql:" + sb.toString());
        	statement.execute(sb.toString());
        } catch (SQLException e) {
            throw new DAOException(e.getMessage());
        }
    }
    
    public void deleteTable(String tableName) throws DAOException {
        try (Connection connection = getConnection();
                Statement statement = connection.createStatement()) {
            statement.execute("DROP TABLE " + tableName);
        } catch (SQLException e) {
            throw new DAOException(e.getMessage());
        }
    }   
}
