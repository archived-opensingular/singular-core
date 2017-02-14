package org.opensingular.server.module.admin.healthPanel.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.hibernate.metadata.ClassMetadata;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.opensingular.lib.support.persistence.util.SqlUtil;
import org.opensingular.server.module.admin.healthPanel.dao.PainelSaudeDAO;
import org.opensingular.server.module.admin.healthPanel.db.drivers.DriverOracle;
import org.opensingular.server.module.admin.healthPanel.db.drivers.IValidatorDatabase;
import org.opensingular.server.module.admin.healthPanel.db.objects.ColumnInfo;
import org.opensingular.server.module.admin.healthPanel.db.objects.HealthInfo;
import org.opensingular.server.module.admin.healthPanel.db.objects.TableInfo;
import org.springframework.stereotype.Service;

@Service
public class HealthPanelDbService {

	@Inject
	private DriverOracle driverOracle;
	
	@Inject
	private PainelSaudeDAO saudeDao;
	
	private IValidatorDatabase validator;
	
	public HealthInfo getAllDbMetaData(){
		verificaDialetoUtilizado();
		Map<String, ClassMetadata> map = saudeDao.getAllDbMetaData();
		
		List<TableInfo> tabelas = new ArrayList<>();
		
		map.forEach((k,v)->tabelas.add(getTableInfo(v)));
		
		validator.checkAllInfoTable(tabelas);
		
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
	
	// TODO implementar outros drivers
	private void verificaDialetoUtilizado(){
		if(this.validator == null){
			String hibernateDialect = saudeDao.getHibernateDialect();
			if(hibernateDialect.equals("org.hibernate.dialect.Oracle9gDialect")
				|| hibernateDialect.equals("org.hibernate.dialect.Oracle10gDialect")
				|| hibernateDialect.equals("org.hibernate.dialect.Oracle11gDialect")
				|| hibernateDialect.equals("org.hibernate.dialect.Oracle12gDialect")){
				
				validator = driverOracle;
			}else if(hibernateDialect.equals("")){
				
			}else if(hibernateDialect.equals("")){
				
			}
		}
	}
}
