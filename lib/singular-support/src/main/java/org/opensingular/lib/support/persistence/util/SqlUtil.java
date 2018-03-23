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
import java.util.Optional;
import javax.annotation.Nonnull;

import org.apache.commons.collections.CollectionUtils;
import org.opensingular.lib.commons.base.SingularProperties;

import static org.opensingular.lib.commons.base.SingularProperties.CUSTOM_SCHEMA_NAME;
import static org.opensingular.lib.commons.base.SingularProperties.SINGULAR_DEV_MODE;
import static org.opensingular.lib.commons.base.SingularProperties.USE_EMBEDDED_DATABASE;

/**
 * Utility class for sql processing.
 */
public class SqlUtil {

    private static final List<String> CRUD_OPERATIONS = Arrays.asList("SELECT", "UPDATE", "DELETE", "INSERT");


    private SqlUtil() {
    }

    /**
     * Replaces the default Singular database schema name with
     * the name configured through {@value SingularProperties#CUSTOM_SCHEMA_NAME} singular property
     *
     * @param sql - an sql query
     * @return return the {@param sql} with the schema name replaced
     */
    @Nonnull
    public static String replaceInSQL(@Nonnull String sql, @Nonnull String current, @Nonnull String replacement) {
        return sql.replaceAll(current, replacement);

    }


    /**
     * Test if the given {@param schemaName} is the current singular database schema
     *
     * @param schemaName
     * @return true if {@param schemaName} is the current singular schema, false otherwise
     */
    public static boolean isSingularSchema(String schemaName) {
        return replaceSingularSchemaName(Constants.SCHEMA).equals(schemaName);
    }

    /**
     * Replaces default singular schema name using the configured replacement
     */
    @Nonnull
    public static String replaceSingularSchemaName(@Nonnull String sql) {
        Optional<String> customSchema = SingularProperties.getOpt(CUSTOM_SCHEMA_NAME);
        if (customSchema.isPresent()) {
            return SqlUtil.replaceInSQL(sql, Constants.SCHEMA, customSchema.get());
        }
        return sql;
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

    /**
     * Verifies if should use embedded database (usually while running a test or in development mode).
     */
    public static boolean useEmbeddedDatabase() {
        //In the future, this code should be move to Embedded Database helper class
        if (SingularProperties.getOpt(USE_EMBEDDED_DATABASE).isPresent()) {
            return SingularProperties.get().isTrue(USE_EMBEDDED_DATABASE);
        } else if (SingularProperties.get().isTrue(SINGULAR_DEV_MODE)) {
            return false;
        }
        return true;
    }
}
