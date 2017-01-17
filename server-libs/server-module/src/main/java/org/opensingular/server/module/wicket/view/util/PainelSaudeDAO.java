package org.opensingular.server.module.wicket.view.util;

import java.util.Map;

import org.hibernate.dialect.Dialect;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.metadata.ClassMetadata;
import org.opensingular.lib.support.persistence.SimpleDAO;

public class PainelSaudeDAO extends SimpleDAO {

	public Map<String, ClassMetadata> getAllDbMetaData(){		
		return sessionFactory.getAllClassMetadata();
	}
	
	public String getHibernateDialect(){
		Dialect dialect = ((SessionFactoryImpl)sessionFactory).getDialect();
		
		String dialeto = dialect.getDefaultProperties().getProperty("dialect");
		return dialeto;
	}
}
