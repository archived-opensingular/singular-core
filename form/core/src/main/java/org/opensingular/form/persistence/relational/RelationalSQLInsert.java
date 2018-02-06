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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;

import org.opensingular.form.SIComposite;
import org.opensingular.form.SInstance;
import org.opensingular.form.SType;
import org.opensingular.form.persistence.FormKey;
import org.opensingular.form.persistence.FormKeyRelational;

/**
 * Builder for SQL insertions on Relational DBMS.
 *
 * @author Edmundo Andrade
 */
public class RelationalSQLInsert extends RelationalSQL {
    private SIComposite instance;
    private List<RelationalColumn> keyColumns;
    private List<RelationalColumn> targetColumns;
    private Map<String, SType<?>> mapColumnToField;

    public RelationalSQLInsert(SIComposite instance) {
        this.instance = instance;
        this.keyColumns = new ArrayList<>();
        this.targetColumns = new ArrayList<>();
        this.mapColumnToField = new HashMap<>();
        SType<?> tableContext = tableContext(instance.getType());
        for (SType<?> field : getFields(instance.getType())) {
            if (tableContext(field) == tableContext && foreignColumn(field) == null
                    && fieldValue(instance, field) != null) {
                collectKeyColumns(field, keyColumns);
                collectTargetColumn(field, targetColumns, keyColumns, mapColumnToField);
            }
        }
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
            Map<String, Object> containerKeyColumns = new HashMap<>();
            List<RelationalColumn> inserted = insertedColumns(tableName, containerKeyColumns);
            SIComposite tableInstance = instance;
            if (tableContext != tableContext(instance.getType())) {
                Optional<SInstance> found = instance.getFields().stream()
                        .filter(field -> tableContext == tableContext(field.getType())).findFirst();
                if (found.isPresent()) {
                    tableInstance = (SIComposite) found.get();
                }
            }
            lines.add(new RelationalSQLCommmand(
                    "insert into " + tableName + " (" + concatenateColumnNames(inserted, ", ") + ") values ("
                            + concatenateColumnValues(inserted, ", ", containerKeyColumns, params) + ")",
                    params, tableInstance, inserted));
        }
        return lines;
    }

    private List<RelationalColumn> insertedColumns(String table, Map<String, Object> containerKeyColumns) {
        Set<RelationalColumn> result = new LinkedHashSet<>();
        keyColumns.forEach(column -> {
            if (column.getTable().equals(table) && resolveColumnValue(column, containerKeyColumns) != null) {
                result.add(column);
            }
        });
        targetColumns.forEach(column -> {
            if (column.getTable().equals(table) && resolveColumnValue(column, containerKeyColumns) != null) {
                result.add(column);
            }
        });
        getContainerInstances(instance).stream().filter(FormKey::containsKey).forEach(container -> {
            FormKeyRelational containerKey = (FormKeyRelational) FormKey.fromInstance(container);
            List<String> containerPK = tablePK(container.getType());
            for (RelationalFK fk : tableFKs(instance.getType())) {
                if (fk.getForeignType() == container.getType()
                        || fk.getForeignType() == container.getType().getSuperType()) {
                    collectColumnIfNecessary(containerKeyColumns, result, containerKey, containerPK, fk);
                }
            }
        });
        return new ArrayList<>(result);
    }

    private void collectColumnIfNecessary(Map<String, Object> containerKeyColumns, Set<RelationalColumn> inserted,
            FormKeyRelational containerKey, List<String> containerPK, RelationalFK fk) {
        for (int i = 0; i < fk.getKeyColumns().size(); i++) {
            RelationalColumn keyColumn = fk.getKeyColumns().get(i);
            inserted.add(keyColumn);
            containerKeyColumns.put(keyColumn.getName(), containerKey.getColumnValue(containerPK.get(i)));
        }
    }

    private String concatenateColumnNames(List<RelationalColumn> columns, String separator) {
        StringJoiner sj = new StringJoiner(separator);
        columns.forEach(column -> sj.add(column.getName()));
        return sj.toString();
    }

    private String concatenateColumnValues(List<RelationalColumn> columns, String separator,
            Map<String, Object> containerKeyColumns, List<Object> params) {
        StringJoiner sj = new StringJoiner(separator);
        columns.forEach(column -> {
            sj.add("?");
            params.add(resolveColumnValue(column, containerKeyColumns));
        });
        return sj.toString();
    }

    private Object resolveColumnValue(RelationalColumn column, Map<String, Object> containerKeyColumns) {
        SType<?> field = mapColumnToField.get(column.toStringPersistence());
        if (field == null) {
            return containerKeyColumns.get(column.getName());
        }
        return fieldValue(instance, field);
    }
}
