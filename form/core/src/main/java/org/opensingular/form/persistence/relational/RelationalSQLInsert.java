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
import java.util.Optional;
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
    private Optional<SInstance> containerInstance;
    private List<RelationalColumn> keyColumns;
    private List<RelationalColumn> targetColumns;
    private Map<String, SType<?>> mapColumnToField;

    public RelationalSQLInsert(SIComposite instance) {
        this.instance = instance;
        if (instance.getParent() == null) {
            this.containerInstance = Optional.empty();
        } else {
            this.containerInstance = Optional.ofNullable(instance.getParent().getParent());
        }
        this.keyColumns = new ArrayList<>();
        this.targetColumns = new ArrayList<>();
        this.mapColumnToField = new HashMap<>();
        for (SType<?> field : getFields(instance.getType())) {
            if (RelationalSQL.foreignColumn(field) == null && fieldValue(instance, field) != null) {
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
            String tableName = RelationalSQL.table(tableContext);
            List<Object> params = new ArrayList<>();
            Map<String, Object> containerKeyColumns = new HashMap<>();
            List<RelationalColumn> inserted = insertedColumns(tableName, containerKeyColumns);
            lines.add(new RelationalSQLCommmand(
                    "insert into " + tableName + " (" + concatenateColumnNames(inserted, ", ") + ") values ("
                            + concatenateColumnValues(inserted, ", ", containerKeyColumns, params) + ")",
                    params, instance, inserted));
        }
        return lines;
    }

    private List<RelationalColumn> insertedColumns(String table, Map<String, Object> containerKeyColumns) {
        List<RelationalColumn> result = new ArrayList<>();
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
        if (containerInstance.isPresent() && FormKey.containsKey(containerInstance.get())) {
            SInstance container = containerInstance.get();
            FormKeyRelational containerKey = (FormKeyRelational) FormKey.fromInstance(container);
            List<String> containerPK = RelationalSQL.tablePK(container.getType());
            for (RelationalFK fk : RelationalSQL.tableFKs(instance.getType())) {
                if (fk.getForeignType().equals(container.getType())) {
                    collectColumnIfNecessary(containerKeyColumns, result, containerKey, containerPK, fk);
                }
            }
        }
        return result;
    }

    private void collectColumnIfNecessary(Map<String, Object> containerKeyColumns, List<RelationalColumn> result,
            FormKeyRelational containerKey, List<String> containerPK, RelationalFK fk) {
        for (int i = 0; i < fk.getKeyColumns().size(); i++) {
            RelationalColumn keyColumn = fk.getKeyColumns().get(i);
            if (!result.contains(keyColumn)) {
                containerKeyColumns.put(keyColumn.getName(), containerKey.getColumnValue(containerPK.get(i)));
                result.add(keyColumn);
            }
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
