package org.opensingular.server.module.admin.healthPanel.dao;

import java.util.Map;

import javax.transaction.Transactional;

import org.hibernate.dialect.Dialect;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.metadata.ClassMetadata;
import org.opensingular.lib.support.persistence.SimpleDAO;
import org.springframework.stereotype.Repository;

@Repository
public class PainelSaudeDAO extends SimpleDAO {

	@Transactional
	public Map<String, ClassMetadata> getAllDbMetaData(){		
		return sessionFactory.getAllClassMetadata();
	}
	
	@Transactional
	public String getHibernateDialect(){
		Dialect dialect = ((SessionFactoryImpl)sessionFactory).getDialect();
		
		return dialect.toString();
	}
}
