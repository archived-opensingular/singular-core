package org.opensingular.server.module.admin.bd.drivers;

import java.util.List;

import org.opensingular.server.module.admin.bd.objects.SequenceInfo;
import org.opensingular.server.module.admin.bd.objects.TableInfo;

public interface IValidatorDatabase {
	/**
	 * Metodo para pegar as permissoes encontradas no banco baseado no nome das tabelas
	 * obtidas pelo Hibernate
	 * 
	 * @param tables Lista de tabelas, todas com o nome informado.
	 * @return tables retorna a mesma lista recebida, com as informações de permissão atualizados
	 */
	public List<TableInfo> getTablesPermission(List<TableInfo> tables);
	
	/**
	 * 	Metodo para verificar se os valores das colunas encontrados no hibernate são 
	 * 	os mesmos existentes no banco que está sendo utilizado.
	 *  
	 *  Quem utilizar, deve garantir que o nome da tabela e a lista de columnInfo
	 *  estão devidamente preenchidos, esse metodo só modificará a lista de columnInfo.
	 * 
	 * @param tableInfo - Esse tableInfo deve ter o nome e columnsInfo preenchidos,
	 * 	pois irá pesquisar no banco baseado no nome informado, 
	 *  e fará a comparação utilizando os columnInfo existentes. 
	 * 
	 * @return o mesmo tableInfo com as informações atualizadas
	 */
	public TableInfo checkColumnPermissions(TableInfo tableInfo);
	
	/**
	 * 
	 * @param sequencesName Uma lista com o nome de todas as sequences encontradas no Hibernate
	 * @return Uma Lista de SequenceInfo com cada sequenceName recebido dizendo se encontrou 
	 * 	ou não no banco e caso verdadeiro as informações obtidas.
	 */
	public List<SequenceInfo> checkSequences(List<String> sequencesName);
	
	/**
	 * Pega todas as informações que conseguir sobre as tabelas
	 * 
	 * @param tabelas
	 * @return List de TableInfo com todas as informações obtidas.
	 */
	public List<TableInfo> getAllInfoTable(List<String> tables);
	
	/**
	 * Metodo para comparar uma lista de TableInfo PREENCHIDA com os valores existentes no banco
	 * 
	 * @param tables Uma lista de colunas previamente preenchidas para verificar com o banco
	 * @return a lista inicial atualizada
	 */
	public List<TableInfo> checkAllInfoTable(List<TableInfo> tables);
}
