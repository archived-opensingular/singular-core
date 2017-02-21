package org.opensingular.server.commons.persistence.dto.healthsystem;

import java.util.List;

public class HealthInfoDTO {
	private List<TableInfoDTO> tablesList;
	
	public HealthInfoDTO(List<TableInfoDTO> tablesList) {
		this.tablesList = tablesList;
	}

	public List<TableInfoDTO> getTablesList() {
		return tablesList;
	}

	public void setTablesList(List<TableInfoDTO> tablesList) {
		this.tablesList = tablesList;
	}
}
