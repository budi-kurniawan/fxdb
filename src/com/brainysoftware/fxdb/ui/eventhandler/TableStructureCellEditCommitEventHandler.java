package com.brainysoftware.fxdb.ui.eventhandler;

import com.brainysoftware.fxdb.model.DatabaseTableColumn;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TablePosition;

/**
 * 
 * @author Budi Kurniawan (http://brainysoftware.com)
 * 
 */
public class TableStructureCellEditCommitEventHandler<T extends CellEditEvent<DatabaseTableColumn, String>> 
		implements EventHandler<T> {
	
	@Override
	public void handle(T event) {
		int rowCount = event.getTableView().getItems().size();
		TablePosition<DatabaseTableColumn, ? extends Object> tablePosition = event.getTablePosition();
		int rowNumber = tablePosition.getRow();
		int columnNumber = tablePosition.getColumn();
		String newValue = event.getNewValue();
		ObservableList<DatabaseTableColumn> items = event.getTableView().getItems();
		DatabaseTableColumn edited = items.get(rowNumber);
		//System.out.println(edited);
		switch (columnNumber) {
		case 0: // name
			edited.setName(newValue);
			break;
		case 1: // type
			edited.setType(newValue);
			break;
		}
		if (rowCount == rowNumber + 1 && columnNumber == 0) {
			// insert a new row if a new column name is entered to the last row
			items.add(new DatabaseTableColumn());
		}
	}
}
