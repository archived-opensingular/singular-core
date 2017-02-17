package org.opensingular.server.commons.admin.healthsystem.db.objects;

import java.util.List;

public class HealthInfo {
	private List<TableInfo> tablesList;
	
	public HealthInfo(List<TableInfo> tablesList) {
		this.tablesList = tablesList;
	}

	public List<TableInfo> getTablesList() {
		return tablesList;
	}

	public void setTablesList(List<TableInfo> tablesList) {
		this.tablesList = tablesList;
	}
}
