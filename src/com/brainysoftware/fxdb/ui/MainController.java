/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brainysoftware.fxdb.ui;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;

import com.brainysoftware.fxdb.dao.DAOException;
import com.brainysoftware.fxdb.dao.DAOFactory;
import com.brainysoftware.fxdb.dao.DatabaseDAO;
import com.brainysoftware.fxdb.model.SQLResult;
import com.brainysoftware.fxdb.service.DatabaseService;
import com.brainysoftware.fxdb.ui.eventmgt.EventManager;
import com.brainysoftware.fxdb.ui.eventmgt.TableCreatedEvent;
import com.brainysoftware.fxdb.ui.eventmgt.TableCreatedListener;
import com.brainysoftware.fxdb.ui.eventmgt.TableDeleteAttemptedEvent;
import com.brainysoftware.fxdb.ui.eventmgt.TableDeleteAttemptedListener;
import com.brainysoftware.fxdb.util.GUIUtil;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToolBar;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 *
 * @author Budi Kurniawan (http://brainysoftware.com)
 */
public class MainController implements Initializable {
    
    public static final String TOOLBAR_GROUP_TABLE = "tablesToolBarGroup";
    public static final String TOOLBAR_GROUP_SQL = "sqlToolBarGroup";
    private boolean tableListLoaded = false;

    @FXML private MenuBar menuBar;
    @FXML private ToolBar toolBar;
    @FXML private Label label;
    @FXML private VBox centerPane;
    @FXML private TextArea sqlStatementTextArea;
    @FXML private StackPane sqlResultStackPane;
    @FXML private ListView<String> tableListView;
    @FXML private StackPane tableStackPane;
    @FXML private StackPane reportStackPane;
    @FXML private Tab sqlTab;
    
    @FXML
    private void handleButtonAction(ActionEvent event) {
        System.out.println("You clicked me!");
        label.setText("Hello World!");
    }
    
    @FXML
    private void handleTabSelected(Event event) {
        Tab tab = (Tab) event.getTarget();
        if (tab.isSelected()) {
            switch (tab.getId()) {
                case "tablesTab":
                    GUIUtil.reorderToolBarGroupAndShow(toolBar, TOOLBAR_GROUP_TABLE);
                    if (!tableListLoaded) {
                        loadTableNames();
                    }
                    break;
                case "sqlTab":
                    GUIUtil.reorderToolBarGroupAndShow(toolBar, TOOLBAR_GROUP_SQL);
                    break;
            }
        }
    }
    
    private void loadTableNames() {
        DatabaseDAO databaseDAO = DAOFactory.getDatabaseDAO();
        List<String> tableNames = null;
        try {
            tableNames = databaseDAO.getTableNames();
            tableListLoaded = true;
        } catch (DAOException e) {
        }
        if (tableNames != null) {
            tableListView.getItems().addAll(tableNames);
        }
    }
    
    private void removeTableNames() {
        tableListView.getItems().clear();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        System.out.println("Initialize MainController...");
        
        EventManager eventManager = EventManager.getInstance();
        
        TableDeleteAttemptedListener tableDeleteAttemptedListener = new TableDeleteAttemptedListener() {
            @Override
            public void tableDeleteAttempted(TableDeleteAttemptedEvent event) {
                MultipleSelectionModel<String> selectionModel = tableListView.getSelectionModel();
                // selectionModel won't be null even when no item is selected
                String selectedItem = selectionModel.getSelectedItem();
                if (selectedItem == null) {
                    GUIUtil.showErrorDialog("Error deleting table", "Please select a table to delete");
                } else {
                    ButtonType result = GUIUtil.showConfirmDialog("Delete Table", "Delete table " + selectedItem + "?");
                    if (result != null && result == ButtonType.OK) {
                        try {
                            new DatabaseService().deleteTable(selectedItem);
                            removeTableNames();
                            tableStackPane.getChildren().clear();
                            loadTableNames();
                        } catch (Exception e) {
                            GUIUtil.showErrorDialog("Error deleting table", e.getMessage());
                        }
                    }
                }
            }
        };        
        eventManager.registerTableDeleteAttemptedListener(tableDeleteAttemptedListener);
        
        TableCreatedListener tableCreatedListener = new TableCreatedListener() {
            @Override
            public void tableCreated(TableCreatedEvent event) {
                tableListView.getItems().add(event.getTableName());
            }
        };
        eventManager.registerTableCreatedListener(tableCreatedListener);
        
        Button executeSQLButton = (Button) GUIUtil.getToolBarNode(
                toolBar, "executeSQLToolBarButton");
        executeSQLButton.setOnAction(n -> executeSQL());
        
        // no idea how to add the listener to fxml
        tableListView.getSelectionModel().selectedItemProperty().addListener(
            new ChangeListener<String>() {
            	@Override
                public void changed(ObservableValue<? extends String> ov, 
                        String oldValue, String newValue) {
            	    if (newValue != null) {
            	        // when the list items are removed, this event is fired with newValue = null
                        loadTableDataToTableStackPane(newValue);
            	    }
                }
            });
    }
    
    private void executeSQL() {
        String sql = sqlStatementTextArea.getText();
        DatabaseDAO databaseDAO = DAOFactory.getDatabaseDAO();
        SQLResult sqlResult = null;
        try {
            sqlResult = databaseDAO.executeSQL(sql);
        } catch (DAOException e) {
            
        }
        if (sqlResult != null) {
            SQLResult.Status status = sqlResult.getStatus();
            switch (status) {
                case FAILED:
                    sqlResultStackPane.getChildren().clear();
                    Label label = new Label(sqlResult.getErrorMessage());
                    sqlResultStackPane.getChildren().add(label);
                    break;
                case SUCCESS_WITHOUT_ROW_SET:
                    sqlResultStackPane.getChildren().clear();
                    Label label2 = new Label(sqlResult.getRowsAffected() + " rows affected.");
                    sqlResultStackPane.getChildren().add(label2);
                    break;
                case SUCCESS_WITH_ROW_SET:
                    sqlResultStackPane.getChildren().clear();
                    TableView<String[]> tableView = GUIUtil.createTableView(sqlResult, null, null);
                    sqlResultStackPane.getChildren().add(tableView);
                    break;
            }
        }
    }

    private void loadTableDataToTableStackPane(String tableName) {
        DatabaseDAO databaseDAO = DAOFactory.getDatabaseDAO();
        SQLResult sqlResult = null;
        try {
            sqlResult = databaseDAO.executeSQL("SELECT * FROM " + tableName);
        } catch (DAOException e) {
            
        }
        if (sqlResult != null) {
            SQLResult.Status status = sqlResult.getStatus();
            ObservableList<Node> tableStackPaneChildren = tableStackPane.getChildren();
            switch (status) {
                case FAILED:
                	tableStackPaneChildren.clear();
                    Label label = new Label(sqlResult.getErrorMessage());
                    tableStackPaneChildren.add(label);
                    break;
                case SUCCESS_WITH_ROW_SET:
                	tableStackPaneChildren.clear();
                    EventHandler<CellEditEvent<String[], String>> editCommitHandler = 
                        new EventHandler<CellEditEvent<String[], String>>() {
                            @Override
                            public void handle(CellEditEvent<String[], String> event) {
                                TablePosition<String[], String> tablePosition = event.getTablePosition();
                                int rowNumber = tablePosition.getRow();
                                int columnNumber = tablePosition.getColumn();
                                String newValue = event.getNewValue();
                                DatabaseDAO databaseDAO = DAOFactory.getDatabaseDAO();
                                String[] row = event.getTableView().getItems()
                                        .get(rowNumber);
                                try {
                                    databaseDAO.updateTableCell(tableName, rowNumber, 
                                            columnNumber, newValue);
                                    row[columnNumber] = newValue;
                                } catch (DAOException e) {
                                    GUIUtil.showErrorDialog("Error", e.getMessage());
                                    row[columnNumber] = event.getOldValue();
                                }
                                ObservableList<String[]> items = event.getTableView().getItems();
                                items.set(rowNumber, row);
                            }
                        };
                    TableView<String[]> tableView = GUIUtil.createTableView(sqlResult,
                            editCommitHandler, null);
                    tableView.setEditable(true);
                    
                    //tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
                    tableView.getSelectionModel().setCellSelectionEnabled(true);
                    
                    int recordCount = sqlResult.getRowSet().length;
                    Label recordCountLabel = new Label(recordCount + " record" + 
                                    (recordCount > 1 ? "s" : ""));
                    VBox vbox = new VBox(tableView, recordCountLabel);
                    tableStackPaneChildren.add(vbox);
                    break;
            }
        }
    }
    
    @FXML
    private void handleKeyInput(final InputEvent event) {
        if (event instanceof KeyEvent) {
            final KeyEvent keyEvent = (KeyEvent) event;
            if (sqlTab.isSelected() && keyEvent.isControlDown() && keyEvent.getCode() == KeyCode.E) {
                System.out.println("execute 2");
            }
        }
    }
}
