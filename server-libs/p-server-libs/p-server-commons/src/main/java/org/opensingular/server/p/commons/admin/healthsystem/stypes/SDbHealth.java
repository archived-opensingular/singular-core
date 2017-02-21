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

package org.opensingular.server.p.commons.admin.healthsystem.stypes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.opensingular.form.SIComposite;
import org.opensingular.form.SIList;
import org.opensingular.form.SInfoType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.STypeList;
import org.opensingular.form.TypeBuilder;
import org.opensingular.form.type.core.SIBoolean;
import org.opensingular.form.type.core.SIString;
import org.opensingular.form.type.core.STypeBoolean;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.validation.ValidationErrorLevel;
import org.opensingular.form.view.SViewListByMasterDetail;
import org.opensingular.form.view.SViewListByTable;
import org.opensingular.lib.support.persistence.util.SqlUtil;

@SInfoType(spackage = SSystemHealthPackage.class, newable = true, name = SDbHealth.TYPE_NAME)
public class SDbHealth extends STypeComposite<SIComposite> {
	public static final String TYPE_NAME = "dbhealth";
	public static final String TYPE_FULL_NAME = SSystemHealthPackage.PACKAGE_NAME+"."+TYPE_NAME;
	
	@Override
	protected void onLoadType(TypeBuilder tb) {
		
        STypeList<STypeComposite<SIComposite>, SIComposite> tabelas = this.addFieldListOfComposite("tablesList", "tabela");
        tabelas.setView(()->new SViewListByMasterDetail().fullSize().disableNew().disableDelete());
        
        STypeComposite<SIComposite> tabela = tabelas.getElementsType();
        
        STypeString schemaField = tabela.addFieldString("schema");
		schemaField
	        .asAtr()
	        	.label("Schema")
	        	.maxLength(20)
	        	.enabled(true)
	        .asAtrBootstrap()
	        	.colPreference(2);

        tabela.addFieldString("tableName")
        	.asAtr()
        		.label("Nome")
        		.maxLength(50)
        		.enabled(true)
        	.asAtrBootstrap()
        		.colPreference(2);
        
        STypeBoolean foundTableField = tabela.addFieldBoolean("found");
		foundTableField
        	.asAtr()
	    		.label("Encontrado no Banco")
	    		.enabled(true)
	    	.asAtrBootstrap()
	    		.colPreference(2);
		
        STypeList<STypeString, SIString> privs = tabela.addFieldListOf("userPrivs", STypeString.class);
        privs
        	.asAtr()
        		.label("Permissões")
        		.enabled(true)
        	.asAtrBootstrap()
        		.colPreference(2);
        privs.setView(()->new SViewListByTable().disableNew().disableDelete());
        
		tabela.addInstanceValidator(validatable -> {
			Optional<SIBoolean> foundTableInstance = validatable.getInstance().findNearest(foundTableField);
			if (!foundTableInstance.isPresent() || !foundTableInstance.get().getValue()) {
				validatable.error("Table not found!");
			}

			Optional<SIString> foundSchemaField = validatable.getInstance().findNearest(schemaField);
			Optional<SIList<SIString>> listObj = validatable.getInstance().findNearest(privs);
			List<Object> listPrivs = listObj.get().getValue();
			List<String> vals = new ArrayList<>();
			listPrivs.forEach(obj -> vals.add((String) obj));

			if(foundSchemaField.get() == null){
				validatable.error(ValidationErrorLevel.ERROR, "Schema not found!");
			}else{
				if (SqlUtil.isSingularSchema(foundSchemaField.get().getValue())
						&& (!vals.contains("SELECT") || !vals.contains("UPDATE")
							|| !vals.contains("DELETE") || !vals.contains("INSERT"))) {
					validatable.error("Singular table without complete CRUD!");
				}
			}
		});
        
        STypeList<STypeComposite<SIComposite>, SIComposite> colunas = tabela.addFieldListOfComposite("columnsInfo", "colunas");
        
        colunas.setView(()->new SViewListByTable().disableNew().disableDelete());
        colunas.asAtr().label("Colunas");
        
        STypeComposite<SIComposite> coluna = colunas.getElementsType();
        
        coluna.addFieldString("columnName")
        	.asAtr()
        		.label("Nome")
        		.maxLength(50)
        		.enabled(true)
        	.asAtrBootstrap()
        		.colPreference(2);
        
        STypeString dataType = coluna.addFieldString("dataType");
        dataType
        	.selectionOf("CHAR", "CLOB", "DATE", "DATETIME", "NUMBER", "VARCHAR", "VARCHAR2")
      		.asAtr()
	      		.label("Tipo de Dados")
	      		.maxLength(10)
	      		.enabled(true)
	      	.asAtrBootstrap()
	          	.colPreference(2);
      
        coluna.addFieldInteger("dataLength")
	  		.asAtr()
	  			.label("Tamanho(Bytes)")
	  			.enabled(true)
	  		.asAtrBootstrap()
	          	.colPreference(1);
      
        coluna.addFieldInteger("charLength")
	    	.asAtr()
	    		.label("Tamanho(Caracteres)")
	    		.enabled(true)
	    	.asAtrBootstrap()
	        	.colPreference(1);
      
        coluna.addFieldInteger("dataPrecision")
	  		.asAtr()
	  			.label("Precisao(valores numericos)")
	  		.asAtrBootstrap()
	          	.colPreference(1);
        
        STypeBoolean nullableField = coluna.addFieldBoolean("nullable");
		nullableField
	    	.asAtr()
	    		.label("Aceita null")
//	    		.enabled(false)
	    	.asAtrBootstrap()
	        	.colPreference(1);
        
        STypeBoolean foundHibernateField = coluna.addFieldBoolean("foundHibernate");
		foundHibernateField
	        .asAtr()
				.label("Encontrado no Hibernate")
				.enabled(true)
			.asAtrBootstrap()
	      		.colPreference(2);
        
        STypeBoolean foundDatabaseField = coluna.addFieldBoolean("foundDataBase");
		foundDatabaseField
	        .asAtr()
				.label("Encontrado no Banco")
				.enabled(true)
			.asAtrBootstrap()
	      		.colPreference(2);

		coluna.addInstanceValidator(validatable->{
			Optional<SIBoolean> databaseFieldInstance = validatable.getInstance().findNearest(foundDatabaseField);
			Optional<SIBoolean> hibernateFieldInstance = validatable.getInstance().findNearest(foundHibernateField);

			// Encontrado no hibernate e nao no banco
			if(hibernateFieldInstance.get().getValue() && !databaseFieldInstance.get().getValue()){
				validatable.error("Inconsistency between database and Hibernate!");
			}else{
				// Encontrado no banco e nao no hibernate
				Optional<SIBoolean> nullableFieldInstance = validatable.getInstance().findNearest(nullableField);
				Optional<SIList<SIString>> listObj = validatable.getInstance().findNearest(privs);
				List<Object> listPrivs = listObj.get().getValue();
				List<String> vals = new ArrayList<>();
				listPrivs.forEach(obj -> vals.add((String) obj));

				if(!vals.contains("SELECT") || !vals.contains("DELETE")){
					if(!nullableFieldInstance.get().getValue() && (!vals.contains("INSERT") || !vals.contains("UPDATE"))){
						validatable.error("Column NOT NULL without SELECT or UPDATE permissions");
					}
				}
			}
		});
	}
}
