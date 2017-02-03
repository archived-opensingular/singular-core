package org.opensingular.server.module.admin;

import java.util.List;

import org.opensingular.server.module.admin.bd.objects.TableInfo;

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
