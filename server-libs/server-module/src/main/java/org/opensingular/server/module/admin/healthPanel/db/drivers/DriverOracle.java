package org.opensingular.server.module.admin.healthPanel.db.drivers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.hibernate.SQLQuery;
import org.hibernate.transform.ResultTransformer;
import org.opensingular.lib.support.persistence.SimpleDAO;
import org.opensingular.server.module.admin.healthPanel.db.objects.ColumnInfo;
import org.opensingular.server.module.admin.healthPanel.db.objects.SequenceInfo;
import org.opensingular.server.module.admin.healthPanel.db.objects.TableInfo;
import org.springframework.stereotype.Repository;

@Repository
public class DriverOracle extends SimpleDAO implements IValidatorDatabase{

	@Transactional
	@Override
	public List<TableInfo> getAllInfoTable(List<String> tabelas) {
		List<TableInfo> privilegios = new ArrayList<>();
		
		tabelas.forEach(tableName-> {
			TableInfo tabelaInfo = new TableInfo();
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
	public TableInfo checkColumnPermissions(TableInfo tableInfo) {
		
		List<ColumnInfo> colunas = getColumnsInfoFromTable(tableInfo.getTableName());
		
		List<ColumnInfo> colunasAux = new ArrayList<>();
		if(colunas != null && !colunas.isEmpty()){
			tableInfo.getColumnsInfo().forEach(column->{
				for (ColumnInfo col: colunas) {
					if(col.getColumnName().equals(column.getColumnName())) {
						colunasAux.add(col);
						col.setFoundHibernate(true);
						break;
					}
				}
			});
		}
		
		/*  Verifica se o elemento foi encontrado na lista de resultado
		 *  Se não for encontrado, então coloca ele na lista indicando que nao foi encontrado.
		 */
		tableInfo.getColumnsInfo().forEach(column->{
			ColumnInfo colunaEncontrada = null;
			for (ColumnInfo col: colunasAux) {
				if(col.getColumnName().equals(column.getColumnName())){
					colunaEncontrada = col;
					break;
				}
			}
			
			if(colunaEncontrada == null){
				colunasAux.add(column);
			}
		});
		tableInfo.setColumnsInfo(colunasAux);
		
		return tableInfo;
	}
	
	private List<ColumnInfo> getColumnsInfoFromTable(String table) {
		String query = "SELECT OWNER, COLUMN_NAME, DATA_TYPE, CHAR_LENGTH, DATA_PRECISION, TABLE_NAME, DATA_LENGTH "
				+ " FROM SYS.ALL_TAB_COLS "
				+ " WHERE TABLE_NAME = :nome_tabela";
		
		SQLQuery querySQL = getSession().createSQLQuery(query);
		querySQL.setParameter("nome_tabela", table);
		
		querySQL.setResultTransformer(new ResultTransformer() {
			
			@Override
			public Object transformTuple(Object[] obj, String[] arg1) {
				ColumnInfo column = new ColumnInfo();
				
				column.setSchema((String) obj[0]);
				column.setColumnName((String) obj[1]);
				column.setDataType((String) obj[2]);
				column.setCharLength((BigDecimal) obj[3]);
				column.setDataPrecision((BigDecimal) obj[4]);
				column.setTableName((String) obj[5]);
				column.setDataLength((BigDecimal) obj[6]);
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
	public List<TableInfo> getTablesPermission(List<TableInfo> tabelas) {
		tabelas.forEach(table-> setFoundAndUserPrivsFromTable(table));
		return tabelas;
	}

	private void setFoundAndUserPrivsFromTable(TableInfo table) {
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
	public List<SequenceInfo> checkSequences(List<String> sequencesName) {
		
		List<SequenceInfo> sequences = new ArrayList<>();
		
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
					SequenceInfo info = new SequenceInfo();
					info.setSequenceName((String) arg0[0]);
					info.setCurrentValue((BigDecimal) arg0[1]);
					info.setMinValue((BigDecimal) arg0[2]);
					info.setMaxValue((BigDecimal) arg0[3]);
					info.setIncrement((BigDecimal) arg0[4]);
					info.setFound(true);
					
					return info;
				}
				@Override
				public List transformList(List list) {
					return list;
				}
			});
			SequenceInfo info = (SequenceInfo) querySQL.uniqueResult();
			if(info == null){
				info = new SequenceInfo();
				info.setSequenceName(sequenceName);
				info.setFound(false);
			}
			sequences.add(info);
		});
		return sequences;
	}

	@Override
	@Transactional
	public List<TableInfo> checkAllInfoTable(List<TableInfo> tables) {
		
		tables.forEach(table->{
			setFoundAndUserPrivsFromTable(table);
			checkColumnPermissions(table);
			
			if(table.getSchema() == null
					&& table.getColumnsInfo() != null && !table.getColumnsInfo().isEmpty()){
				table.setSchema(table.getColumnsInfo().get(0).getSchema());
			}
		});
		
		return tables;
	}
}
