package org.opensingular.lib.support.persistence.entity;

import org.hibernate.EmptyInterceptor;
import org.opensingular.lib.support.persistence.util.SqlUtil;

@SuppressWarnings("serial")
public class SingularEntityInterceptor extends EmptyInterceptor {

    @Override
    public String onPrepareStatement(String sql) {
        return SqlUtil.replaceSingularSchemaName(sql);
    }

}
