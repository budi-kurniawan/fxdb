package com.brainysoftware.fxdb.ui;

import java.io.IOException;
import java.util.List;
import java.util.ResourceBundle;

import com.brainysoftware.fxdb.service.DatabaseService;
import com.brainysoftware.fxdb.service.SQLScriptService;
import com.brainysoftware.fxdb.ui.eventmgt.EventManager;
import com.brainysoftware.fxdb.ui.eventmgt.TableDeleteAttemptedEvent;
import com.brainysoftware.fxdb.util.GUIUtil;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToolBar;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * 
 * @author Budi Kurniawan (http://brainysoftware.com)
 * 
 */
public class ToolBarController implements Initializable {

    @FXML private ToolBar toolBar;
    @FXML private ComboBox savedSQLToolBarComboBox;
    
    @Override
    public void initialize(java.net.URL arg0, ResourceBundle arg1) {
        System.out.println("Initialize ToolBarController...");
        List<String> savedSQLFiles = new SQLScriptService().getSavedSQLFileNames();
        savedSQLToolBarComboBox.getItems().addAll(savedSQLFiles);
    }

    @FXML
    private void handleNewTableAction(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("tableStructureDialog.fxml"));
        Dialog<String> dialog = new Dialog<>();
        try {
			DialogPane dialogPane = (DialogPane) loader.load();
			dialog.setDialogPane(dialogPane);
		} catch (IOException e) {
			e.printStackTrace();
		}
        // call showAndWait() instead of show(), bec. the former is blocking and show() non-blocking
        dialog.showAndWait();
    }
    @FXML
    private void handleEditTableAction(ActionEvent event) {
        GUIUtil.showErrorDialog("Edit Table", "Not implemented yet");
    }
    
    // method 1, causing interdependency between controllers
//    @FXML
//    private void handleDeleteTableAction(ActionEvent event) {
//        ListView<String> tableListView = 
//                (ListView<String>) toolBar.getScene().lookup("#tableListView");
//        MultipleSelectionModel<String> selectionModel = tableListView.getSelectionModel();
//        // selectionModel won't be null even when no item is selected
//        String selectedItem = selectionModel.getSelectedItem();
//        if (selectedItem == null) {
//            GUIUtil.showErrorDialog("Error deleting table", "Please select a table to delete");
//        } else {
//            ButtonType result = 
//                    GUIUtil.showConfirmDialog("Delete Table", 
//                            "Delete table " + selectedItem + "?");
//            if (result != null && result == ButtonType.OK) {
//                try {
//                    new DatabaseService().deleteTable(selectedItem);
//                } catch (Exception e) {
//                    GUIUtil.showErrorDialog("Error deleting table", e.getMessage());
//                }
//            }
//        }
//    }
    
    // method 2, use an event manager
    @FXML private void handleDeleteTableAction(ActionEvent event) {
        EventManager.getInstance().fireTableDeleteAttemptedEvent(
                new TableDeleteAttemptedEvent(event.getSource(), null));
    }
    
    @FXML private void handleSaveSQLAction(ActionEvent event) {
        String fileName = GUIUtil.showInputDialog("SQL File Name", "Please enter a name:");
        if (fileName != null) {
            String sql = getSQLStatementTextArea().getText();
            new SQLScriptService().saveSQL(fileName, sql);
            //savedSQLToolBarComboBox.getSelectionModel().clearSelection();
            savedSQLToolBarComboBox.getItems().add(fileName);
            savedSQLToolBarComboBox.getSelectionModel().select(fileName);
        }
    }

    @FXML private void handleDeleteSQLAction(ActionEvent event) {
        String selected = (String) savedSQLToolBarComboBox
                .getSelectionModel().getSelectedItem();
        ButtonType result = GUIUtil.showConfirmDialog("Delete SQL?", "Are you sure you want to delete SQL statement \"" 
                + selected + "\"?");
        if (result != null && result == ButtonType.OK) {
            new SQLScriptService().deleteSQLScript(selected);
            savedSQLToolBarComboBox.getSelectionModel().clearSelection();
            savedSQLToolBarComboBox.getItems().remove(selected);
            getSQLStatementTextArea().clear();
        }
    }
    
    @FXML
    public void handleSavedSQLSelectedAction(ActionEvent event) {
        String selected = (String) savedSQLToolBarComboBox
                .getSelectionModel().getSelectedItem();
        if (selected != null && !selected.trim().isEmpty()) {
            // this is also triggered when the user delete a saved sql
            // so don't call getSQL() if selected is null
            String sql = new SQLScriptService().getSQL(selected);
            getSQLStatementTextArea().setText(sql);
        }
    }
    
    private TextArea getSQLStatementTextArea() {
        return (TextArea) GUIUtil.getRootNode(toolBar).lookup("#sqlStatementTextArea");
    }
    
    
    @FXML
    private void handleSQLButtonKeyPressed(final InputEvent event) {
        System.out.println("handle key input for sql tab");
        if (event instanceof KeyEvent) {
            final KeyEvent keyEvent = (KeyEvent) event;
            if (keyEvent.isControlDown() && keyEvent.getCode() == KeyCode.E) {
                System.out.println("execute");
            }
        }
    }


}
