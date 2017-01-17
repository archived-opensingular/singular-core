package org.opensingular.server.module.wicket.view.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class TableInfo implements Serializable {
	
	private String nomeTabela;
	private String schema;
	private List<String> privilegios;
	private List<ColumnType> colunasInfo;
	
	public TableInfo() {
		privilegios = new ArrayList<>();
		colunasInfo = new ArrayList<>();
	}
	
	public TableInfo(String nomeTabela) {
		this.nomeTabela = nomeTabela;
	}
	
	public String getNomeTabela() {
		return nomeTabela;
	}
	
	public void setNomeTabela(String nomeTabela) {
		this.nomeTabela = nomeTabela;
	}
	
	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public List<String> getPrivilegios() {
		return privilegios;
	}
	
	public void setPrivilegios(List<String> privilegio) {
		this.privilegios = privilegio;
	}

	public List<ColumnType> getColunasInfo() {
		return colunasInfo;
	}

	public void setColunasInfo(List<ColumnType> colunasInfo) {
		this.colunasInfo = colunasInfo;
	}

}
