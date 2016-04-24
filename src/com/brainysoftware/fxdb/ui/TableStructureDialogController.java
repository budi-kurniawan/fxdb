/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brainysoftware.fxdb.ui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.brainysoftware.fxdb.model.DatabaseTableColumn;
import com.brainysoftware.fxdb.service.DatabaseService;
import com.brainysoftware.fxdb.ui.eventhandler.TableStructureCellEditCommitEventHandler;
import com.brainysoftware.fxdb.ui.eventmgt.EventManager;
import com.brainysoftware.fxdb.ui.eventmgt.TableCreatedEvent;
import com.brainysoftware.fxdb.util.GUIUtil;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.TableViewFocusModel;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

/**
 * 
 * @author Budi Kurniawan (http://brainysoftware.com)
 * 
 */
public class TableStructureDialogController implements Initializable {
	@FXML
	private DialogPane tableStructureDialogPane;
	@FXML
	private TableView<DatabaseTableColumn> databaseTableColumnsTableView;

	@FXML
	private void handleButtonAction(ActionEvent event) {
		System.out.println("You clicked me!");
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		System.out.println("Initialize TableStructureController...");
		// a Dialog have its own stage and scene, so it won't inherit its parent
		// css
		// Need to load own css file. See
		// http://stackoverflow.com/questions/28417140/styling-default-javafx-dialogs
		tableStructureDialogPane.getStylesheets().add("css/dialog.css");

		// add ButtonTypes
		ObservableList<ButtonType> buttonTypes = tableStructureDialogPane.getButtonTypes();
		// create a Cancel button whose ButtonData is CANCEL_CLOSE so the dialog
		// can be closed using the X button.
		// https://docs.oracle.com/javase/8/javafx/api/javafx/scene/control/Dialog.html
		ButtonType cancelButtonType = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
		ButtonType createButtonType = ButtonType.OK;
		buttonTypes.add(cancelButtonType);
		buttonTypes.add(createButtonType);
		
		databaseTableColumnsTableView.setEditable(true);
		
		// enter edit mode with a single-click
		databaseTableColumnsTableView.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				TableViewFocusModel<DatabaseTableColumn> focusModel = databaseTableColumnsTableView.getFocusModel();
				TablePosition<DatabaseTableColumn, String> focusedCell = focusModel.getFocusedCell();
				databaseTableColumnsTableView.edit(focusedCell.getRow(), focusedCell.getTableColumn());
			}
		});

		// An EventHandler that persists the new value to the model and
		// adds a row if a cell of the last row is entered a value.
		// this has been moved to TableStructureEditEventHandler
		EventHandler<CellEditEvent<DatabaseTableColumn, String>> editCommitHandler = new EventHandler<CellEditEvent<DatabaseTableColumn, String>>() {
			@Override
			public void handle(CellEditEvent<DatabaseTableColumn, String> event) {
				int rowCount = event.getTableView().getItems().size();
				TablePosition<DatabaseTableColumn, String> tablePosition = event.getTablePosition();
				int rowNumber = tablePosition.getRow();
				int columnNumber = tablePosition.getColumn();
				String newValue = event.getNewValue();
				ObservableList<DatabaseTableColumn> items = event.getTableView().getItems();
				DatabaseTableColumn edited = items.get(rowNumber);
				if (columnNumber == 0) {
					edited.setName(newValue);
				} else if (columnNumber == 1) {
					edited.setType(newValue);
				}
				if (rowCount == rowNumber + 1) {
					// insert a new row
					items.add(new DatabaseTableColumn());
				}
			}
		};

		
		TableStructureCellEditCommitEventHandler<CellEditEvent<DatabaseTableColumn, String>> 
				editCommitEventHandler 
				= new TableStructureCellEditCommitEventHandler<>();
		
		List<DatabaseTableColumn> dbTableColumns = new ArrayList<>();
		dbTableColumns.add(new DatabaseTableColumn());
		ObservableList<DatabaseTableColumn> data = FXCollections.observableList(dbTableColumns);
		
		// column "name"
		TableColumn<DatabaseTableColumn, String> dbColumnNameTableColumn = new TableColumn<DatabaseTableColumn, String>(
				"Name");
		dbColumnNameTableColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
		dbColumnNameTableColumn.setMinWidth(160);
		dbColumnNameTableColumn.setCellFactory(TextFieldTableCell.forTableColumn());
		//dbColumnNameTableColumn.setOnEditCommit(editCommitHandler);
		dbColumnNameTableColumn.setOnEditCommit(editCommitEventHandler);
		

		// column "type"
		TableColumn<DatabaseTableColumn, String> dbColumnTypeTableColumn = new TableColumn<DatabaseTableColumn, String>(
				"Type");
		dbColumnTypeTableColumn.setPrefWidth(90);
		dbColumnTypeTableColumn.setCellValueFactory(new PropertyValueFactory<DatabaseTableColumn, String>("type"));
		dbColumnTypeTableColumn.setCellFactory(
				new Callback<TableColumn<DatabaseTableColumn, String>, TableCell<DatabaseTableColumn, String>>() {
					public TableCell<DatabaseTableColumn, String> call(TableColumn<DatabaseTableColumn, String> p) {
						return new ComboBoxTableCell<DatabaseTableColumn, String>(
								FXCollections.observableArrayList("String", "Number", "Boolean"));
				}
				});
		//dbColumnTypeTableColumn.setOnEditCommit(editCommitHandler);
		dbColumnTypeTableColumn.setOnEditCommit(editCommitEventHandler);

		Callback<TableColumn<DatabaseTableColumn, Boolean>, TableCell<DatabaseTableColumn, Boolean>> 
				checkBoxCellFactory = new Callback<TableColumn<DatabaseTableColumn, Boolean>, TableCell<DatabaseTableColumn, Boolean>>() {
			public TableCell<DatabaseTableColumn, Boolean> call(TableColumn<DatabaseTableColumn, Boolean> p) {
				return GUIUtil.createCheckBoxTableCell();
			}
		};

		// enter edit mode on single click:
		// http://stackoverflow.com/questions/29707487/javafx-tableview-edit-with-single-click-and-auto-insert-row

		// TableView:
		// https://wiki.openjdk.java.net/display/OpenJFX/TableView+User+Experience+Documentation
		// Split and multiline headers: http://stackoverflow.com/questions/10952111/javafx-2-0-table-with-multiline-table-header
		
		// column "allowNull"
		TableColumn<DatabaseTableColumn, Boolean> dbColumnAllowNullTableColumn = new TableColumn<>(
				"Allow Null");
		dbColumnAllowNullTableColumn.setCellValueFactory(new PropertyValueFactory<>("allowNull"));
		dbColumnAllowNullTableColumn.setMinWidth(110);
		dbColumnAllowNullTableColumn.setCellFactory(checkBoxCellFactory);
		// setOnEditCommit won't be called if the cell is of type CheckBoxTableCell
		// See https://docs.oracle.com/javase/8/javafx/api/index.html?javafx/scene/control/cell/CheckBoxTableCell.html
		dbColumnAllowNullTableColumn.setCellFactory(CheckBoxTableCell.forTableColumn(new Callback<Integer, ObservableValue<Boolean>>() {
		    @Override
		    public ObservableValue<Boolean> call(Integer index) {
		    	DatabaseTableColumn edited = data.get(index);
		    	if (edited.allowNullProperty().get()) {
		    		// if allows null, then this column cannot be a primary key
		    		edited.setPrimaryKey(false);
		    	}
		        return data.get(index).allowNullProperty();
		    }
		}));


		// column "primaryKey"
		TableColumn<DatabaseTableColumn, Boolean> dbColumnPrimaryKeyTableColumn = new TableColumn<>(
				"Primary Key");
		dbColumnPrimaryKeyTableColumn.setCellValueFactory(new PropertyValueFactory<>("primaryKey"));
		dbColumnPrimaryKeyTableColumn.setMinWidth(120);
		dbColumnPrimaryKeyTableColumn.setCellFactory(checkBoxCellFactory);
		dbColumnPrimaryKeyTableColumn.setCellFactory(CheckBoxTableCell.forTableColumn(new Callback<Integer, ObservableValue<Boolean>>() {
		    @Override
		    public ObservableValue<Boolean> call(Integer index) {
		    	DatabaseTableColumn edited = data.get(index);
		    	if (edited.primaryKeyProperty().get()) {
		    		// if primary key, must not allow null
		    		edited.setAllowNull(false);
		    	}
		        return data.get(index).primaryKeyProperty();
		    }
		}));
		
		// column "autoGenerate
		TableColumn<DatabaseTableColumn, Boolean> dbColumnAutoGenerateTableColumn = 
				new TableColumn<>("Auto Generate");
		dbColumnAutoGenerateTableColumn.setCellValueFactory(new PropertyValueFactory<>("autoGenerate"));
		dbColumnAutoGenerateTableColumn.setMinWidth(140);
		dbColumnAutoGenerateTableColumn.setCellFactory(checkBoxCellFactory);
		
		databaseTableColumnsTableView.getColumns().setAll(dbColumnNameTableColumn, dbColumnTypeTableColumn,
				dbColumnAllowNullTableColumn, dbColumnPrimaryKeyTableColumn, dbColumnAutoGenerateTableColumn);
		databaseTableColumnsTableView.setItems(data);
		// if !cellSelectionEnabled, selection will be row-based
		databaseTableColumnsTableView.getSelectionModel().setCellSelectionEnabled(true);

		// handler for when Create button is clicked
		final Button createButton = (Button) tableStructureDialogPane.lookupButton(ButtonType.OK);
		createButton.addEventFilter(ActionEvent.ACTION, event -> {
			String tableName = GUIUtil.showInputDialog("Create Table", "Table Name");
			if (tableName == null) {
				event.consume();
			} else {
				DatabaseService dbService = new DatabaseService();
				try {
					dbService.createTable(tableName, dbTableColumns);
					EventManager.getInstance().fireTableCreatedEvent(
					        new TableCreatedEvent(createButton, tableName));
				} catch (Exception e) {
					GUIUtil.showErrorDialog("Error creating table", e.getMessage());
					event.consume();
				}
			}
		});
	}
}