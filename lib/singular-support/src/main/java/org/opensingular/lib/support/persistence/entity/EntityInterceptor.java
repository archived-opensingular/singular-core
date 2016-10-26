package org.opensingular.lib.support.persistence.entity;
 
import org.hibernate.EmptyInterceptor;
import org.opensingular.lib.commons.base.SingularProperties;
import org.opensingular.lib.support.persistence.util.Constants;

import static org.opensingular.lib.commons.base.SingularProperties.CUSTOM_SCHEMA_NAME;

@SuppressWarnings("serial")
public class EntityInterceptor extends EmptyInterceptor {
    
    @Override
    public String onPrepareStatement(String sql) {
        if (SingularProperties.get().containsKey(CUSTOM_SCHEMA_NAME)) {
            String customSchema = SingularProperties.get().getProperty(CUSTOM_SCHEMA_NAME);
            return sql.replaceAll(Constants.SCHEMA, customSchema);
        } else {
            return sql;
        }
    }
 
}
