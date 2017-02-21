package org.opensingular.server.commons.persistence.dto.healthsystem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class TableInfoDTO implements Serializable {
	
	private String tableName;
	private String schema;
	private List<String> userPrivs;
	private List<ColumnInfoDTO> columnsInfo;
	private boolean found=false;
	
	public TableInfoDTO() {
		userPrivs = new ArrayList<>();
		columnsInfo = new ArrayList<>();
	}
	
	public TableInfoDTO(String tableName) {
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

	public List<ColumnInfoDTO> getColumnsInfo() {
		return columnsInfo;
	}

	public void setColumnsInfo(List<ColumnInfoDTO> columnsInfo) {
		this.columnsInfo = columnsInfo;
	}

	public boolean isFound() {
		return found;
	}

	public void setFound(boolean found) {
		this.found = found;
	}

}
