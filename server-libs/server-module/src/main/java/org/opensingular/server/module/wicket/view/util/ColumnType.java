package org.opensingular.server.module.wicket.view.util;

import java.io.Serializable;
import java.math.BigDecimal;

@SuppressWarnings("serial")
public class ColumnType implements Serializable {
	
	private String schema; // No Oracle, o schema geralmente é o OWNER
	private String tableName;
	private String columnName;
	private String dataType;
	
	/**
	 * Mostra a quantidade de char para os tipos: CHAR, VARCHAR2, NCHAR, NVARCHAR
	 * 
	 * Caso o dataType da coluna seja diferente dos mencionados, então o valor será zero.
	 */
	private BigDecimal charLength;
	
	/**
	 * Precisao DECIMAL de valores do tipo NUMBER
	 * Se for FLOAT entao a precisao é em BINARIO
	 * 
	 * NULL para outros tipos de coluna
	 */
	private BigDecimal dataPrecision;
	
	/**
	 * Length of the column (in bytes)
	 */
	private BigDecimal dataLength;
	
	private boolean found =false;
	
	public ColumnType() {
	}
	
	public ColumnType(String columnName) {
		this.columnName = columnName;
	}
	
	public ColumnType(String columnName, BigDecimal dataLength) {
		this.columnName = columnName;
		this.dataLength = dataLength;
	}
	
	public String getSchema() {
		return schema;
	}
	public void setSchema(String schema) {
		this.schema = schema;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getColumnName() {
		return columnName;
	}
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	public BigDecimal getCharLength() {
		return charLength;
	}
	public void setCharLength(BigDecimal charLength) {
		this.charLength = charLength;
	}
	public BigDecimal getDataPrecision() {
		return dataPrecision;
	}
	public void setDataPrecision(BigDecimal dataPrecision) {
		this.dataPrecision = dataPrecision;
	}
	public BigDecimal getDataLength() {
		return dataLength;
	}
	public void setDataLength(BigDecimal dataLength) {
		this.dataLength = dataLength;
	}

	public boolean isFound() {
		return found;
	}

	public void setFound(boolean found) {
		this.found = found;
	}
	
}
