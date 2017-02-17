package org.opensingular.server.module.admin.healthsystem.db.objects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class TableInfo implements Serializable {
	
	private String tableName;
	private String schema;
	private List<String> userPrivs;
	private List<ColumnInfo> columnsInfo;
	private boolean found=false;
	
	public TableInfo() {
		userPrivs = new ArrayList<>();
		columnsInfo = new ArrayList<>();
	}
	
	public TableInfo(String tableName) {
		this.tableName = tableName;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public List<String> getUserPrivs() {
		return userPrivs;
	}

	public void setUserPrivs(List<String> userPrivs) {
		this.userPrivs = userPrivs;
	}

	public List<ColumnInfo> getColumnsInfo() {
		return columnsInfo;
	}

	public void setColumnsInfo(List<ColumnInfo> columnsInfo) {
		this.columnsInfo = columnsInfo;
	}

	public boolean isFound() {
		return found;
	}

	public void setFound(boolean found) {
		this.found = found;
	}

}
