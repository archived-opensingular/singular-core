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

package org.opensingular.form.persistence.relational;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;

import org.opensingular.form.SIComposite;
import org.opensingular.form.SInstance;
import org.opensingular.form.SType;
import org.opensingular.form.persistence.FormKey;
import org.opensingular.form.persistence.FormKeyRelational;

/**
 * Builder for SQL updates and other data synchronizations on Relational DBMS.
 *
 * @author Edmundo Andrade
 */
public class RelationalSQLUpdate extends RelationalSQL {
    private SIComposite instance;
    private List<RelationalColumn> keyColumns;
    private List<RelationalColumn> targetColumns;
    private Map<String, SType<?>> mapColumnToField;
    private Map<String, Object> mapColumnToValue;

    public RelationalSQLUpdate(SIComposite instance, SIComposite previousInstance) {
        this.instance = instance;
        this.keyColumns = new ArrayList<>();
        this.targetColumns = new ArrayList<>();
        this.mapColumnToField = new HashMap<>();
        getFields(instance).stream().filter(field -> foreignColumn(field) == null).forEach(field -> {
            if (previousInstance == null
                    || !Objects.equals(fieldValue(instance, field), fieldValue(previousInstance, field))) {
                collectKeyColumns(field, keyColumns);
                collectTargetColumn(field, targetColumns, keyColumns, mapColumnToField);
            }
        });
        mapColumnToValue = ((FormKeyRelational) FormKey.from((SInstance) instance)).getValue();
    }

    public SIComposite getInstance() {
        return instance;
    }

    @Override
    public List<RelationalSQLCommmand> toSQLScript() {
        List<RelationalSQLCommmand> lines = new ArrayList<>();
        for (SType<?> tableContext : targetTables) {
            String tableName = table(tableContext);
            List<Object> params = new ArrayList<>();
            lines.add(new RelationalSQLCommmand(
                    "update " + tableName + " " + tableAlias(tableName, targetColumns) + " set "
                            + set(tableName, targetColumns, params) + " where "
                            + where(tableName, keyColumns, mapColumnToValue, targetColumns, params),
                    params, instance, null));
        }
        return lines;
    }

    private String set(String table, List<RelationalColumn> setColumns, List<Object> params) {
        StringJoiner sj = new StringJoiner(", ");
        setColumns.forEach(column -> {
            if (column.getTable().equals(table)) {
                sj.add(tableAlias(table, targetColumns) + "." + column.getName() + " = ?");
                params.add(columnValue(column));
            }
        });
        return sj.toString();
    }

    private Object columnValue(RelationalColumn column) {
        SType<?> field = mapColumnToField.get(column.toStringPersistence());
        if (field == null) {
            return null;
        }
        return fieldValue(instance, field);
    }
}
