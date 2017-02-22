/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.opensingular.server.commons.admin.healthsystem.validation.database;

import java.util.List;

import org.opensingular.server.commons.persistence.dto.healthsystem.SequenceInfoDTO;
import org.opensingular.server.commons.persistence.dto.healthsystem.TableInfoDTO;

public interface IValidatorDatabase {
	/**
	 * Metodo para pegar as permissoes encontradas no banco baseado no nome das tabelas
	 * obtidas pelo Hibernate
	 * 
	 * @param tables Lista de tabelas, todas com o nome informado.
	 * @return tables retorna a mesma lista recebida, com as informações de permissão atualizados
	 */
	public List<TableInfoDTO> getTablesPermission(List<TableInfoDTO> tables);
	
	/**
	 * 	Metodo para verificar se os valores das colunas encontrados no hibernate são 
	 * 	os mesmos existentes no banco que está sendo utilizado.
	 *  
	 *  Quem utilizar, deve garantir que o nome da tabela e a lista de columnInfo
	 *  estão devidamente preenchidos, esse metodo só modificará a lista de columnInfo.
	 * 
	 * @param tableInfoDTO - Esse tableInfoDTO deve ter o nome e columnsInfo preenchidos,
	 * 	pois irá pesquisar no banco baseado no nome informado, 
	 *  e fará a comparação utilizando os columnInfo existentes. 
	 *
	 */
	public void checkColumnPermissions(TableInfoDTO tableInfoDTO);
	
	/**
	 * Metodo para verificar se encontra as sequences especificadas no banco
	 * 
	 * @param sequencesName Uma lista com o nome de todas as sequences encontradas no Hibernate
	 * @return Uma Lista de SequenceInfoDTO com cada sequenceName recebido dizendo se encontrou
	 * 	ou não no banco e caso verdadeiro as informações obtidas.
	 */
	public List<SequenceInfoDTO> checkSequences(List<String> sequencesName);
	
	/**
	 * Pega todas as informações que conseguir sobre as tabelas
	 * 
	 * @param tabelas
	 * @return List de TableInfoDTO com todas as informações obtidas.
	 */
	public List<TableInfoDTO> getAllInfoTable(List<String> tables);
	
	/**
	 * Metodo para comparar uma lista de TableInfoDTO PREENCHIDA com os valores existentes no banco
	 * 
	 * @param tables Uma lista de colunas previamente preenchidas para verificar com o banco
	 */
	public void checkAllInfoTable(List<TableInfoDTO> tables);
}
