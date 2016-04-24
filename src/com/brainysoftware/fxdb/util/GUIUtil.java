package com.brainysoftware.fxdb.util;

import java.io.File;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Optional;

import com.brainysoftware.fxdb.model.DatabaseTableColumn;
import com.brainysoftware.fxdb.model.SQLResult;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.TableViewFocusModel;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToolBar;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.util.Callback;

/**
 * 
 * @author Budi Kurniawan (http://brainysoftware.com)
 * 
 */
public class GUIUtil {

    public static void showMessageDialog(String header, String content) {
        Alert errorAlert = new Alert(Alert.AlertType.INFORMATION);
        errorAlert.setTitle("Message");
        errorAlert.setHeaderText(header);
        errorAlert.setContentText(content);
        errorAlert.showAndWait();
    }
    
    public static void showErrorDialog(String header, String content) {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setTitle("Error");
        errorAlert.setHeaderText(header);
        errorAlert.setContentText(content);
        errorAlert.showAndWait();
    }
    
    public static ButtonType showConfirmDialog(String header, String content) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm");
        confirmAlert.setHeaderText(header);
        confirmAlert.setContentText(content);
        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent()) {
            return result.get();
        }
        return null;
    }
    
    public static String showInputDialog(String header, String content) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Input Dialog");
        dialog.setHeaderText(header);
        dialog.setContentText(content);

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            return result.get();
        } else {
            return null;
        }
    }

    public static MenuItem getMenuItem(MenuBar menuBar, String id) {
        if (menuBar == null) {
            return null;
        }
        ObservableList<Menu> menus = menuBar.getMenus();
        for (Menu menu : menus) {
            ObservableList<MenuItem> items = menu.getItems();
            for (MenuItem item : items) {
                if (id.equals(item.getId())) {
                    return item;
                }
            }
        }
        return null;
    }
    
    public static Group getToolBarGroup(ToolBar toolBar, String id) {
        ObservableList<Node> items = toolBar.getItems();
        for (Node node : items) {
            if (id.equals(node.getId())) {
                return (Group) node;
            }
        }
        return null;
    } 
    
    public static Node getToolBarNode(ToolBar toolBar, String id) {
        ObservableList<Node> items = toolBar.getItems();
        for (Node node : items) {
            if (node instanceof Group) {
                Group group = (Group) node;
                ObservableList<Node> children = group.getChildren();
                for (Node child : children) {
                    if (child instanceof HBox) {
                        HBox hBox = (HBox) child;
                        ObservableList<Node> buttons = hBox.getChildren();
                        for (Node child2 : buttons) {
                            if (id.equals(child2.getId())) {
                                return child2;
                            }
                        }
                    }
                }
            }
        }
        return null;
    } 

    public static void reorderToolBarGroupAndShow(ToolBar toolBar, String id) {
        ObservableList<Node> items = toolBar.getItems();
        int size = items.size();
        int position = 0;
        for (int i = 0; i < size; i++) {
            Group group = (Group) items.get(i);
            if (id.equals(group.getId())) {
                position = i;
            }
            group.setVisible(false);
        }
        if (position != 0) {
            // Swap. Remove the nth node first so
            // as not to change the index and size
            Group group2 = (Group) items.remove(position); 
            Group group1 = (Group) items.remove(0);
            items.add(0, group2);
            items.add(position, group1);
        }
        items.get(0).setVisible(true);
    }
    
    public static <T> TableColumn<T, String> createTableColumn(T type, String label,
            String propertyName, int minWidth) {
        TableColumn<T, String> column = new TableColumn<>(label);
        column.setMinWidth(minWidth);
        column.setCellValueFactory(
                new PropertyValueFactory<T, String>(propertyName));
        return column;
    }

    public static TableView<String[]> createTableView(SQLResult sqlResult,
            EventHandler<TableColumn.CellEditEvent<String[], String>> editCommitHandler,
            EventHandler<TableColumn.CellEditEvent<String[], String>> editCancelHandler) {
        String[][] rowSet = sqlResult.getRowSet();
        String[] columnNames = sqlResult.getColumnNames();
        int columnCount = columnNames.length;
        TableColumn[] tableColumns = new TableColumn[columnCount];
        
        for (int i = 0; i < columnCount; i++) {
            tableColumns[i] = new TableColumn(columnNames[i]);
            // for editing
            tableColumns[i].setCellFactory(TextFieldTableCell.forTableColumn());
            tableColumns[i].setOnEditCommit(editCommitHandler);
            tableColumns[i].setOnEditCancel(editCancelHandler);
        }
        for (int i = 0; i < columnCount; i++) {
//            tableColumns[i] = new TableColumn(columnNames[i]);
            final int c = i;
            tableColumns[i].setCellValueFactory(
                    new Callback<CellDataFeatures<String[], String>, 
                            ObservableValue<String>>() {
                @Override
                public ObservableValue<String> call(CellDataFeatures<String[], String> p) {
                    return new SimpleStringProperty((p.getValue()[c]));
                }
            });
        }
        
        TableView<String[]> tableView = new TableView<>();
        tableView.getColumns().addAll(tableColumns);
        ObservableList<String[]> observableArrayList = FXCollections.observableArrayList(rowSet);
        tableView.setItems(observableArrayList);
		// enter edit mode with a single-click
		tableView.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				TableViewFocusModel<String[]> focusModel = tableView.getFocusModel();
				TablePosition<String[], String> focusedCell = focusModel.getFocusedCell();
				tableView.edit(focusedCell.getRow(), focusedCell.getTableColumn());
			}
		});

        return tableView;
    }
    
    public static Node getRootNode(Node control) {
        Node node = control;
        while (node.getParent() != null) {
            node = node.getParent();
        }
        return node;
    }
    
    private static NumberFormat numberFormat = NumberFormat.getCurrencyInstance();
    public static String formatMoney(BigDecimal value) {
        String result = null;
        try  {
            result = numberFormat.format(value  );
        } catch (Exception e    ) {
            System.out.println("error:  " + e.getMessage() + ", value:"
             + value);
        }
        return result;
    }
    
    public static List<File> chooseFiles(String title, File initDir) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.setInitialDirectory(initDir);
        return fileChooser.showOpenMultipleDialog(null);
    }
    
    public static CheckBoxTableCell<DatabaseTableColumn, Boolean> createCheckBoxTableCell(
    		/*ObservableList<DatabaseTableColumn> data*/) {
        CheckBoxTableCell<DatabaseTableColumn, Boolean> checkBoxTableCell 
        		= new CheckBoxTableCell<>();
        return checkBoxTableCell;
    }
}