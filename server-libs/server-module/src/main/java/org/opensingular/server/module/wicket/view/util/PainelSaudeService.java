package org.opensingular.server.module.wicket.view.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.hibernate.metadata.ClassMetadata;
import org.hibernate.persister.entity.AbstractEntityPersister;

public class PainelSaudeService {

//	@Inject
//    private PainelSaudeDAO saudeDao;
	
	private IValidatorDatabase validator;
	
	public Map<String, ClassMetadata> getAllDbMetaData(){
//		Map<String, ClassMetadata> map = saudeDao.getAllDbMetaData();
		
//		List<TableInfo> tabelas = new ArrayList<>();
//		
//		map.forEach((k,v)->{
//			System.out.println(k +" "+ v);
//			
//			getTableInfo(v);
//			
//			System.out.println("\n\n");
//		});
		
		//TODO
		return null;
	}

	private TableInfo getTableInfo(ClassMetadata v) {
		TableInfo tableInfo = new TableInfo();
		
		AbstractEntityPersister persister = (AbstractEntityPersister) v;
		
		String[] name = persister.getTableName().split(".");
		tableInfo.setSchema(name[0]);
		tableInfo.setNomeTabela(name[1]);
		
		List<String> colunas = new ArrayList<>();
		
		String[] propertyNames = v.getPropertyNames();
		
		Arrays.asList(propertyNames).forEach(propertyName->
			colunas.add(persister.getPropertyColumnNames(propertyName)[0]));

		Arrays.asList(persister.getIdentifierColumnNames()).forEach(chave->{
			if(!colunas.contains(chave)){
				colunas.add(chave);
			}
		});
		
		
		List<ColumnType> colunasTypes = new ArrayList<>();
		colunas.forEach(col->colunasTypes.add(new ColumnType(col)));
		tableInfo.setColunasInfo(colunasTypes);
		
//		for (int i = 0; i < propertyNames.length; i++) {
//			System.out.println("Nome property: "+propertyNames[i]);
//			System.out.println("TESTE: "+persister.getPropertyColumnNames(propertyNames[i])[0]);
//		}
		
		return tableInfo;
	}
	
	// TODO verificar qual o driver utilizado atualmente;
	private void verificaDialetoUtilizado(){
		if(this.validator == null){
			
		}
	}
}
