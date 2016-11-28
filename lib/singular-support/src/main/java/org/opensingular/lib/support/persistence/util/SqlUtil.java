package org.opensingular.lib.support.persistence.util;

import org.opensingular.lib.commons.base.SingularProperties;

import static org.opensingular.lib.commons.base.SingularProperties.CUSTOM_SCHEMA_NAME;

public class SqlUtil {

    public static String replaceSchemaName(String sql) {
        if (SingularProperties.get().containsKey(CUSTOM_SCHEMA_NAME)) {
            String customSchema = SingularProperties.get().getProperty(CUSTOM_SCHEMA_NAME);
            return sql.replaceAll(Constants.SCHEMA, customSchema);
        } else {
            return sql;
        }
    }

}
