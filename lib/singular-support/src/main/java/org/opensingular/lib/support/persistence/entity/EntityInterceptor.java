package org.opensingular.lib.support.persistence.entity;

import org.hibernate.EmptyInterceptor;
import org.opensingular.lib.support.persistence.util.SqlUtil;

@SuppressWarnings("serial")
public class EntityInterceptor extends EmptyInterceptor {
    
    @Override
    public String onPrepareStatement(String sql) {
        return SqlUtil.replaceSchemaName(sql);
    }
 
}
