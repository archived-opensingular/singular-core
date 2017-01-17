package org.opensingular.server.module.wicket.view.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.transform.ResultTransformer;
import org.opensingular.lib.support.persistence.SimpleDAO;
import org.springframework.stereotype.Component;

public class DriverOracle extends SimpleDAO implements IValidatorDatabase{

	@Override
	public List<TableInfo> getAllInfoTable(List<String> tabelas) {
		List<TableInfo> privilegios = new ArrayList<>();
		
		tabelas.forEach(tableName-> {
			TableInfo tabelaInfo = new TableInfo();
			tabelaInfo.setNomeTabela(tableName);
			
			tabelaInfo.setPrivilegios(verificaPermissaoTabelaEspecifica(tableName));
			privilegios.add(tabelaInfo);
			
			if(!tabelaInfo.getPrivilegios().isEmpty()){
				tabelaInfo.setColunasInfo(verificaTipoDadoColuna(tableName));
				if(tabelaInfo.getColunasInfo() != null && !tabelaInfo.getColunasInfo().isEmpty())
					tabelaInfo.setSchema(tabelaInfo.getColunasInfo().get(0).getSchema());
			}
		});
		
		return privilegios;
	}
	/**
	 * Esse metodo pesquisa as informações das colunas quando elas são encontradas,
	 * retornando por fim um TableInfo, esse TableInfo terá as permissoes de acesso da tabela(SELECT, DROP, UPDATE, ALTER)
	 * nulos, pois eles não são o foco desse metodo.
	 * Para ter todos os elementos de um TableInfo preenchidos, use getAllInfoTable.
	 */
	@Override
	public TableInfo checkColumnPermissions(String tabela, List<ColumnType> columnsName) {
		TableInfo info = new TableInfo();
		info.setNomeTabela(tabela);
		
		List<ColumnType> colunas = verificaTipoDadoColuna(tabela);
		
		List<ColumnType> colunasAux = new ArrayList<>();
		if(colunas != null && !colunas.isEmpty()){
			columnsName.forEach(column->{
				for (ColumnType col: colunas) {
					if(col.getColumnName().equals(column.getColumnName()) &&
							col.getDataLength().compareTo(column.getDataLength()) == 0){
						col.setFound(true);
						colunasAux.add(col);
						break;
					}
				}
			});
			info.setColunasInfo(colunasAux);			
		}
		
		/*
		 *  Verifica se o elemento foi encontrado na lista de resultado
		 *  
		 *  Se não for encontrado, então coloca ele na lista indicando que nao foi encontrado.
		 */
		columnsName.forEach(column->{
			ColumnType colunaEncontrada = null;
			for (ColumnType col: colunasAux) {
				if(col.getColumnName().equals(column.getColumnName())){
					colunaEncontrada = col;
					break;
				}
			}
			
			if(colunaEncontrada == null){
				column.setFound(false); // garantir que quem passou não modificou o valor
				colunasAux.add(column);
			}
		});			
		
		return info;
	}

	private List<ColumnType> verificaTipoDadoColuna(String tabela) {
		String query = "SELECT OWNER, COLUMN_NAME, DATA_TYPE, CHAR_LENGTH, DATA_PRECISION, TABLE_NAME, DATA_LENGTH "
				+ " FROM SYS.ALL_TAB_COLUMNS "
				+ " WHERE TABLE_NAME = :nome_tabela";
		
		SQLQuery querySQL = getSession().createSQLQuery(query);
		querySQL.setParameter("nome_tabela", tabela);
		
		querySQL.setResultTransformer(new ResultTransformer() {
			
			@Override
			public Object transformTuple(Object[] obj, String[] arg1) {
				ColumnType column = new ColumnType();
				
				column.setSchema((String) obj[0]);
				column.setColumnName((String) obj[1]);
				column.setDataType((String) obj[2]);
				column.setCharLength((BigDecimal) obj[3]);
				column.setDataPrecision((BigDecimal) obj[4]);
				column.setTableName((String) obj[5]);
				column.setDataLength((BigDecimal) obj[6]);
				column.setFound(true);
				
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
	private List<String> verificaPermissaoTabelaEspecifica(String tabela) {
		String query = " SELECT PRIVILEGE"
				+ " FROM SYS.ALL_TAB_PRIVS_RECD"
				+ " WHERE TABLE_NAME = :nome_tabela";
		
		SQLQuery querySQL = getSession().createSQLQuery(query);
		querySQL.setParameter("nome_tabela", tabela);
		
		return querySQL.list();
	}
	
	/**
	 * Esse metodo pesquisa as informações de acesso a tabela,
	 * retornando por fim um TableInfo, esse TableInfo terá as informações de coluna nulos, pois eles não são o foco desse metodo.
	 * Para ter todos os elementos de um TableInfo preenchidos, use getAllInfoTable.
	 */
	@Override
	public List<TableInfo> checkTablePermissions(List<String> tabelas) {
		List<TableInfo> privilegios = new ArrayList<>();
		
		tabelas.forEach(tableName-> {
			TableInfo tabelaInfo = new TableInfo();
			tabelaInfo.setNomeTabela(tableName);
			
			tabelaInfo.setPrivilegios(verificaPermissaoTabelaEspecifica(tableName));
			privilegios.add(tabelaInfo);
		});
		return privilegios;
	}

	@Override
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
}
