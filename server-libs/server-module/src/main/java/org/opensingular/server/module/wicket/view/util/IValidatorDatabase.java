package org.opensingular.server.module.wicket.view.util;

import java.util.List;

public interface IValidatorDatabase {
	/**
	 * Pega somente as permissoes da tabela
	 * 
	 */
	public List<TableInfo> checkTablePermissions(List<String> tables);
	
	public TableInfo checkColumnPermissions(String table, List<ColumnType> columnsName);
	
	public List<SequenceInfo> checkSequences(List<String> sequencesName);
	
	/**
	 * Pega todas as informações que conseguir sobre as tabelas
	 * 
	 * @param tabelas
	 * @return List de TableInfo com todas as informações obtidas.
	 */
	public List<TableInfo> getAllInfoTable(List<String> tables);
}
