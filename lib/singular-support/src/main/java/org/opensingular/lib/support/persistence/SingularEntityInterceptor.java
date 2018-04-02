/*
 *
 *  * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.opensingular.lib.support.persistence;

import org.hibernate.EmptyInterceptor;
import org.opensingular.lib.commons.base.SingularProperties;
import org.opensingular.lib.support.persistence.util.Constants;
import org.opensingular.lib.support.persistence.util.SqlUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.opensingular.lib.commons.base.SingularProperties.*;

@SuppressWarnings("serial")
public class SingularEntityInterceptor extends EmptyInterceptor {

    private List<DatabaseSchemaReplacement> schemaReplacements = new ArrayList<>();

    public SingularEntityInterceptor(List<DatabaseSchemaReplacement> replacements) {
        Optional<String> schemaName = SingularProperties.getOpt(CUSTOM_SCHEMA_NAME);
        schemaName.ifPresent(s -> schemaReplacements.add(new DatabaseSchemaReplacement(Constants.SCHEMA, s)));
        schemaReplacements.addAll(replacements);
    }

    public SingularEntityInterceptor() {
        this(Collections.EMPTY_LIST);
    }

    @Override
    public String onPrepareStatement(String sql) {
        String result = sql;
        for (DatabaseSchemaReplacement schemaReplacement : schemaReplacements) {
            result = SqlUtil.replaceSchemaName(result, schemaReplacement.getOriginalSchema(), schemaReplacement.getSchemaReplacement());
        }
        return result;
    }

}
