package org.opensingular.server.commons.admin.healthsystem.validation.database;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.hibernate.SQLQuery;
import org.hibernate.transform.ResultTransformer;
import org.opensingular.lib.support.persistence.SimpleDAO;
import org.opensingular.server.commons.persistence.dto.healthsystem.ColumnInfoDTO;
import org.opensingular.server.commons.persistence.dto.healthsystem.SequenceInfoDTO;
import org.opensingular.server.commons.persistence.dto.healthsystem.TableInfoDTO;
import org.springframework.stereotype.Repository;

@Repository
public class ValidatorOracle extends SimpleDAO implements IValidatorDatabase{

	@Transactional
	@Override
	public List<TableInfoDTO> getAllInfoTable(List<String> tabelas) {
		List<TableInfoDTO> privilegios = new ArrayList<>();
		
		tabelas.forEach(tableName-> {
			TableInfoDTO tabelaInfo = new TableInfoDTO();
			tabelaInfo.setTableName(tableName);
			
			tabelaInfo.setUserPrivs(getPermissionEspecificTable(tableName));
			privilegios.add(tabelaInfo);
			
			if(!tabelaInfo.getUserPrivs().isEmpty()){
				tabelaInfo.setColumnsInfo(getColumnsInfoFromTable(tableName));
				if(tabelaInfo.getColumnsInfo() != null && !tabelaInfo.getColumnsInfo().isEmpty())
					tabelaInfo.setSchema(tabelaInfo.getColumnsInfo().get(0).getSchema());
			}
		});
		
		return privilegios;
	}

	@Transactional
	@Override
	public void checkColumnPermissions(TableInfoDTO tableInfoDTO) {
		
		List<ColumnInfoDTO> colunas = getColumnsInfoFromTable(tableInfoDTO.getTableName());
		
		// Verifica se as colunas encontradas no banco foi encontrada no hibernate
		// caso nao tenha sido, indica isso
		colunas.forEach(coluna->{
			boolean colunaDoBancoEncontradaNoHibernate = false;
			for (ColumnInfoDTO col: tableInfoDTO.getColumnsInfo()) {
				if(col.getColumnName().equals(coluna.getColumnName())) {
					colunaDoBancoEncontradaNoHibernate = true;
					coluna.setFoundHibernate(true);
					break;
				}
			}
			
			if(!colunaDoBancoEncontradaNoHibernate){
				coluna.setFoundHibernate(false); // garantir que o valor é de que não foi encontrado
			}
		});
		
		// Verifica se as colunas encontradas no hibernate foram encontradas no banco
		// caso nao tenha sido, indica isso
		tableInfoDTO.getColumnsInfo().forEach(tableCol->{
			boolean colunaDoHibernateEncontradaNoBanco = false;
			for (ColumnInfoDTO col: colunas) {
				if(col.getColumnName().equals(tableCol.getColumnName())) {
					colunaDoHibernateEncontradaNoBanco = true;
					break;
				}
			}
			
			if(!colunaDoHibernateEncontradaNoBanco){
				tableCol.setFoundHibernate(true);
				tableCol.setFoundDataBase(false);
				colunas.add(tableCol);
			}
		});
		
		tableInfoDTO.setColumnsInfo(colunas);
	}
	
	private List<ColumnInfoDTO> getColumnsInfoFromTable(String table) {
		String query = "SELECT OWNER, COLUMN_NAME, DATA_TYPE, CHAR_LENGTH, DATA_PRECISION, TABLE_NAME, DATA_LENGTH, NULLABLE "
				+ " FROM SYS.ALL_TAB_COLS "
				+ " WHERE TABLE_NAME = :nome_tabela";
		
		SQLQuery querySQL = getSession().createSQLQuery(query);
		querySQL.setParameter("nome_tabela", table);
		
		querySQL.setResultTransformer(new ResultTransformer() {
			
			@Override
			public Object transformTuple(Object[] obj, String[] arg1) {
				ColumnInfoDTO column = new ColumnInfoDTO();

				int i=0;
				column.setSchema((String) obj[i]);
				column.setColumnName((String) obj[++i]);
				column.setDataType((String) obj[++i]);
				column.setCharLength((BigDecimal) obj[++i]);
				column.setDataPrecision((BigDecimal) obj[++i]);
				column.setTableName((String) obj[++i]);
				column.setDataLength((BigDecimal) obj[++i]);
				column.setNullable("Y".equals((String) obj[++i]));
				column.setFoundDataBase(true);
				
				return column;
			}
			@Override
			public List transformList(List list) {
				return list;
			}
		});
		
		return querySQL.list();
	}
	
	/**
	 * Segundo Documentação Oracle:
	 * 
	 * ALL_TAB_PRIVS_RECD describes the following types of grants:
	 * 		Object grants for which the current user is the grantee
	 * 		Object grants for which an enabled role or PUBLIC is the grantee
	 * 
	 * @param tabela
	 * @return lista de String com o nome dos privilegios obtidos.
	 */
	private List<String> getPermissionEspecificTable(String tabela) {
		String query = " SELECT PRIVILEGE"
				+ " FROM SYS.ALL_TAB_PRIVS_RECD"
				+ " WHERE TABLE_NAME = :nome_tabela";
		
		SQLQuery querySQL = getSession().createSQLQuery(query);
		querySQL.setParameter("nome_tabela", tabela);
		
		return querySQL.list();
	}
	
	@Override
	@Transactional
	public List<TableInfoDTO> getTablesPermission(List<TableInfoDTO> tabelas) {
		tabelas.forEach(table-> setFoundAndUserPrivsFromTable(table));
		return tabelas;
	}

	private void setFoundAndUserPrivsFromTable(TableInfoDTO table) {
		List<String> permissions = getPermissionEspecificTable(table.getTableName());
		table.setUserPrivs(permissions);
		if(permissions != null && !permissions.isEmpty()){
			table.setFound(true);
		}else{
			table.setFound(false);
		}
	}

	@Override
	@Transactional
	public List<SequenceInfoDTO> checkSequences(List<String> sequencesName) {
		
		List<SequenceInfoDTO> sequences = new ArrayList<>();
		
		sequencesName.forEach(sequenceName->{
			String query = "SELECT "
					+ " seq.SEQUENCE_NAME, seq.LAST_NUMBER, seq.MIN_VALUE, seq.MAX_VALUE, seq.INCREMENT_BY "
					+ " FROM SYS.ALL_SEQUENCES seq  "
					+ " WHERE seq.SEQUENCE_NAME = :sequence_name";
			
			SQLQuery querySQL = getSession().createSQLQuery(query);
			querySQL.setParameter("sequence_name", sequenceName);
			
			querySQL.setResultTransformer(new ResultTransformer() {
				@Override
				public Object transformTuple(Object[] arg0, String[] arg1) {
					SequenceInfoDTO info = new SequenceInfoDTO();

					int i=0;
					info.setSequenceName((String) arg0[i]);
					info.setCurrentValue((BigDecimal) arg0[++i]);
					info.setMinValue((BigDecimal) arg0[++i]);
					info.setMaxValue((BigDecimal) arg0[++i]);
					info.setIncrement((BigDecimal) arg0[++i]);
					info.setFound(true);
					
					return info;
				}
				@Override
				public List transformList(List list) {
					return list;
				}
			});
			SequenceInfoDTO info = (SequenceInfoDTO) querySQL.uniqueResult();
			if(info == null){
				info = new SequenceInfoDTO();
				info.setSequenceName(sequenceName);
				info.setFound(false);
			}
			sequences.add(info);
		});
		return sequences;
	}

	@Override
	@Transactional
	public void checkAllInfoTable(List<TableInfoDTO> tables) {
		
		tables.forEach(table->{
			setFoundAndUserPrivsFromTable(table);
			checkColumnPermissions(table);
			
			if(table.getSchema() == null
					&& table.getColumnsInfo() != null && !table.getColumnsInfo().isEmpty()){
				table.setSchema(table.getColumnsInfo().get(0).getSchema());
			}
		});
	}
}
