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
package org.opensingular.server.module.admin.healthsystem.service;

import org.hibernate.metadata.ClassMetadata;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.opensingular.lib.support.persistence.util.SqlUtil;
import org.opensingular.server.module.admin.healthsystem.dao.HealthSystemDAO;
import org.opensingular.server.module.admin.healthsystem.db.drivers.IValidatorDatabase;
import org.opensingular.server.module.admin.healthsystem.db.drivers.ValidatorFactory;
import org.opensingular.server.module.admin.healthsystem.db.objects.ColumnInfo;
import org.opensingular.server.module.admin.healthsystem.db.objects.HealthInfo;
import org.opensingular.server.module.admin.healthsystem.db.objects.TableInfo;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class HealthPanelDbService {
	@Inject
	private HealthSystemDAO saudeDao;

	public HealthInfo getAllDbMetaData(){
		IValidatorDatabase validator = verificaDialetoUtilizado();
		List<TableInfo> tabelas = new ArrayList<>();

		Map<String, ClassMetadata> map = saudeDao.getAllDbMetaData();
		map.forEach((k,v)->tabelas.add(getTableInfo(v)));

		try{
			validator.checkAllInfoTable(tabelas);
		} catch (Exception e){
			tabelas.clear();
			e.printStackTrace();
		}

		return new HealthInfo(tabelas);
	}

	private TableInfo getTableInfo(ClassMetadata v) {
		TableInfo tableInfo = new TableInfo();
		
		AbstractEntityPersister persister = (AbstractEntityPersister) v;
		
		String[] name = SqlUtil.replaceSchemaName(persister.getTableName()).split("\\.");
		tableInfo.setSchema(name[0]);
		tableInfo.setTableName(name[1]);
		
		List<String> colunas = new ArrayList<>();

		String[] propertyNames = v.getPropertyNames();
		
		Arrays.asList(propertyNames).forEach(propertyName->
			colunas.add(persister.getPropertyColumnNames(propertyName)[0]));

		Arrays.asList(persister.getIdentifierColumnNames()).forEach(chave->{
			if(!colunas.contains(chave)){
				colunas.add(chave);
			}
		});
		
		List<ColumnInfo> colunasTypes = new ArrayList<>();
		colunas.forEach(col->colunasTypes.add(new ColumnInfo(col, true)));
		tableInfo.setColumnsInfo(colunasTypes);
		
		return tableInfo;
	}

	private IValidatorDatabase  verificaDialetoUtilizado(){
		String hibernateDialect = saudeDao.getHibernateDialect();

		try{
			return ValidatorFactory.getDriver(hibernateDialect);
		}catch (Exception e){
			return null;
		}
	}
}
