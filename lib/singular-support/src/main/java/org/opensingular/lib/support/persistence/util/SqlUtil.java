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
package org.opensingular.lib.support.persistence.util;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.opensingular.lib.commons.base.SingularProperties;

import static org.opensingular.lib.commons.base.SingularProperties.CUSTOM_SCHEMA_NAME;

/**
 * Utility class for sql processing.
 */
public class SqlUtil {

    private static final List<String> CRUD_OPERATIONS = Arrays.asList("SELECT", "UPDATE", "DELETE", "INSERT");

    private SqlUtil() {}

    /**
     * Replaces the default Singular database schema name with
     * the name configured through {@value SingularProperties#CUSTOM_SCHEMA_NAME} singular property
     * @param sql - an sql query
     * @return
     *  return the {@param sql} with the schema name replaced
     *
     */
    public static String replaceSchemaName(String sql) {
        if (SingularProperties.get().containsKey(CUSTOM_SCHEMA_NAME)) {
            String customSchema = SingularProperties.get().getProperty(CUSTOM_SCHEMA_NAME);
            return sql.replaceAll(Constants.SCHEMA, customSchema);
        } else {
            return sql;
        }
    }

    /**
     * Test if the given {@param schemaName} is the current singular database schema
     * @param schemaName
     * @return
     *  true if {@param schemaName} is the current singular schema, false otherwise
     */
    public static boolean isSingularSchema(String schemaName){
        return replaceSchemaName(Constants.SCHEMA).equals(schemaName);
    }

    public static boolean hasCompleteCrud(List<String> vals) {
        if (CollectionUtils.isEmpty(vals)) {
            return false;
        }

        for (String crudOperation : CRUD_OPERATIONS) {
            if (!vals.contains(crudOperation)) {
                return false;
            }
        }

        return true;
    }
}
