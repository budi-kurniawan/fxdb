package com.brainysoftware.fxdb.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * 
 * @author Budi Kurniawan (http://brainysoftware.com)
 * 
 */
public class DatabaseTableColumn {
	public StringProperty name = new SimpleStringProperty();
	public StringProperty type = new SimpleStringProperty("String");
	public IntegerProperty capacity = new SimpleIntegerProperty();
	public BooleanProperty allowNull = new SimpleBooleanProperty();
	public BooleanProperty autoGenerate = new SimpleBooleanProperty();
	public BooleanProperty primaryKey = new SimpleBooleanProperty();
	
	public DatabaseTableColumn() {
	}
	
	public DatabaseTableColumn(String name, String type, int capacity,
			boolean allowNull, boolean primaryKey, boolean autoGenerate) {
		this.name.set(name);
		this.type.set(type);
		this.capacity.set(capacity);
		this.allowNull.set(allowNull);
		this.primaryKey.set(primaryKey);
		this.autoGenerate.set(autoGenerate);
	}
	
	@Override
	public String toString() {
		return "name: " + name + ", type: " + type + ", allowNull: " + allowNull
				+ ", primaryKey: " + primaryKey + ", autoGenerate: " + autoGenerate
				+ ", capacity: " + capacity;
	}

	public StringProperty nameProperty() {
		return this.name;
	}
	

	public String getName() {
		return this.nameProperty().get();
	}
	

	public void setName(final String name) {
		this.nameProperty().set(name);
	}
	

	public StringProperty typeProperty() {
		return this.type;
	}
	

	public String getType() {
		return this.typeProperty().get();
	}
	

	public void setType(final String type) {
		this.typeProperty().set(type);
	}
	

	public IntegerProperty capacityProperty() {
		return this.capacity;
	}
	

	public int getCapacity() {
		return this.capacityProperty().get();
	}
	

	public void setCapacity(final int capacity) {
		this.capacityProperty().set(capacity);
	}
	

	public BooleanProperty allowNullProperty() {
		return this.allowNull;
	}
	

	public boolean isAllowNull() {
		return this.allowNullProperty().get();
	}
	

	public void setAllowNull(final boolean allowNull) {
		this.allowNullProperty().set(allowNull);
	}
	

	public BooleanProperty autoGenerateProperty() {
		return this.autoGenerate;
	}
	

	public boolean isAutoGenerate() {
		return this.autoGenerateProperty().get();
	}
	

	public void setAutoGenerate(final boolean autoGenerate) {
		this.autoGenerateProperty().set(autoGenerate);
	}
	

	public BooleanProperty primaryKeyProperty() {
		return this.primaryKey;
	}
	

	public boolean isPrimaryKey() {
		return this.primaryKeyProperty().get();
	}
	

	public void setPrimaryKey(final boolean primaryKey) {
		this.primaryKeyProperty().set(primaryKey);
	}
	
}
