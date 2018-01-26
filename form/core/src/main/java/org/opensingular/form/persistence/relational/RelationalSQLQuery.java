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

import static org.opensingular.form.persistence.relational.RelationalSQLAggregator.COUNT;
import static org.opensingular.form.persistence.relational.RelationalSQLAggregator.DISTINCT;
import static org.opensingular.form.persistence.relational.RelationalSQLAggregator.NONE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;

import org.opensingular.form.SType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.SingularFormException;
import org.opensingular.form.persistence.FormKey;
import org.opensingular.form.persistence.FormKeyRelational;

/**
 * Builder for SQL queries on Relational DBMS.
 *
 * @author Edmundo Andrade
 */
public class RelationalSQLQuery extends RelationalSQL {
    private RelationalSQLAggregator aggregator;
    private Collection<SType<?>> targetFields = new ArrayList<>();
    private List<RelationalColumn> keyColumns;
    private List<RelationalColumn> targetColumns;
    private Map<String, SType<?>> mapColumnToField;
    private List<RelationalColumn> orderingColumns = new ArrayList<>();
    private String keyFormTable;
    private Map<String, Object> keyFormColumnMap;
    private List<RelationalColumn> keyFormColumns = Collections.emptyList();
    private Long limitOffset;
    private Long limitRows;

    @SafeVarargs
    public RelationalSQLQuery(RelationalSQLAggregator aggregator, Collection<SType<?>>... fieldCollections) {
        this.aggregator = aggregator;
        for (Collection<SType<?>> fieldCollection : fieldCollections) {
            this.targetFields.addAll(fieldCollection);
        }
        this.keyColumns = new ArrayList<>();
        this.targetColumns = new ArrayList<>();
        this.mapColumnToField = new HashMap<>();
        List<SType<?>> list = new ArrayList<>();
        addFieldsToList(targetFields, list);
        for (SType<?> field : list) {
            collectKeyColumns(field, keyColumns);
            collectTargetColumn(field, targetColumns, Collections.emptyList(), mapColumnToField);
        }
    }

    public RelationalSQLQuery orderBy(SType<?>... fields) {
        orderingColumns.clear();
        for (SType<?> field : fields)
            collectTargetColumn(field, orderingColumns, Collections.emptyList(), mapColumnToField);
        return this;
    }

    public RelationalSQLQuery where(STypeComposite<?> type, FormKey formKey) {
        keyFormTable = RelationalSQL.table(type);
        keyFormColumnMap = ((FormKeyRelational) formKey).getValue();
        keyFormColumns = new ArrayList<>();
        collectKeyColumns(type, keyFormColumns);
        return this;
    }

    public RelationalSQLQuery limit(Long limitOffset, Long limitRows) {
        this.limitOffset = limitOffset;
        this.limitRows = limitRows;
        return this;
    }

    public Collection<SType<?>> getTargetFields() {
        return targetFields;
    }

    @Override
    public List<RelationalSQLCommmand> toSQLScript() {
        List<RelationalColumn> selected = selectedColumns();
        Set<RelationalColumn> relevant = new LinkedHashSet<>(selected);
        relevant.addAll(keyFormColumns);
        Map<String, RelationalFK> joinMap = createJoinMap();
        reorderTargetTables(joinMap);
        List<Object> params = new ArrayList<>();
        String wherePart = "";
        if (keyFormTable != null) {
            wherePart += " where " + where(keyFormTable, keyFormColumns, keyFormColumnMap, relevant, params);
        }
        String orderPart = "";
        if (!orderingColumns.isEmpty()) {
            orderPart = " order by " + concatenateOrderingColumns(", ", relevant);
        }
        String sql = "select " + selectPart(concatenateColumnNames(selected, ", ", relevant)) + " from "
                + joinTables(relevant, joinMap) + wherePart + orderPart;
        return Arrays.asList(new RelationalSQLCommmand(sql, params, null, selected, limitOffset, limitRows));
    }

    private String selectPart(String columnsSequence) {
        String result;
        if (aggregator == COUNT) {
            result = "count(*)";
        } else if (aggregator == DISTINCT) {
            result = "distinct " + columnsSequence;
        } else {
            result = columnsSequence;
        }
        return result;
    }

    private List<RelationalColumn> selectedColumns() {
        Set<RelationalColumn> result = new LinkedHashSet<>(targetColumns);
        keyColumns.forEach(column -> {
            if (aggregator == NONE) {
                result.add(column);
            }
        });
        return new ArrayList<>(result);
    }

    private String concatenateColumnNames(List<RelationalColumn> columns, String separator,
            Collection<RelationalColumn> relevantColumns) {
        StringJoiner sj = new StringJoiner(separator);
        columns.forEach(column -> sj.add(
                tableAlias(column.getTable(), column.getSourceKeyColumns(), relevantColumns) + "." + column.getName()));
        return sj.toString();
    }

    private String joinTables(Collection<RelationalColumn> relevantColumns, Map<String, RelationalFK> joinMap) {
        StringJoiner sj = new StringJoiner(" left join ");
        List<String> joinedTables = new ArrayList<>();
        for (SType<?> tableContext : targetTables) {
            String table = RelationalSQL.table(tableContext);
            for (List<RelationalColumn> join : distinctJoins(table, relevantColumns)) {
                sj.add(onClause(tableContext, join, relevantColumns, joinedTables, joinMap));
            }
            joinedTables.add(table);
            if (aggregator == COUNT) {
                break;
            }
        }
        return sj.toString();
    }

    private String onClause(SType<?> tableContext, List<RelationalColumn> sourceKeyColumns,
            Collection<RelationalColumn> relevantColumns, List<String> joinedTables,
            Map<String, RelationalFK> joinMap) {
        String table = RelationalSQL.table(tableContext);
        String tableAlias = tableAlias(table, sourceKeyColumns, relevantColumns);
        String result = table + " " + tableAlias;
        if (joinedTables.isEmpty())
            return result;
        RelationalFK relationship = locateRelationship(table, sourceKeyColumns, joinedTables, joinMap);
        if (relationship == null) {
            throw new SingularFormException(
                    "Relational mapping should provide foreign key for relevant relationships with table '" + table
                            + "'.");
        }
        String leftTable = relationship.getTable();
        List<RelationalColumn> leftColumns = relationship.getKeyColumns();
        List<String> rightColumns = RelationalSQL.tablePK(tableContext);
        if (leftColumns.size() != rightColumns.size()) {
            throw new SingularFormException(
                    "Relational mapping should provide compatible-size foreign key for the relationship between table '"
                            + leftTable + "' and '" + table + "'.");
        }
        StringJoiner sj = new StringJoiner(" and ");
        for (int i = 0; i < leftColumns.size(); i++) {
            sj.add(tableAlias(leftTable, targetColumns) + "." + leftColumns.get(i).getName() + " = " + tableAlias + "."
                    + rightColumns.get(i));
        }
        return result + " on " + sj;
    }

    private RelationalFK locateRelationship(String table, List<RelationalColumn> sourceKeyColumns,
            List<String> joinedTables, Map<String, RelationalFK> joinMap) {
        RelationalFK result = null;
        for (String joinedTable : joinedTables) {
            result = joinMap.get(joinedTable + ">" + table + "@" + serialize(sourceKeyColumns));
            if (result == null) {
                Optional<String> key = joinMap.keySet().stream()
                        .filter(item -> item.startsWith(joinedTable + ">" + table + "@")).findFirst();
                if (key.isPresent()) {
                    result = joinMap.get(key.get());
                }
            }
            if (result != null) {
                break;
            }
        }
        return result;
    }

    private String concatenateOrderingColumns(String separator, Set<RelationalColumn> relevantColumns) {
        StringJoiner sj = new StringJoiner(separator);
        orderingColumns.forEach(column -> sj.add(
                tableAlias(column.getTable(), column.getSourceKeyColumns(), relevantColumns) + "." + column.getName()));
        return sj.toString();
    }

    private Map<String, RelationalFK> createJoinMap() {
        Map<String, RelationalFK> result = new HashMap<>();
        for (SType<?> tableContext : targetTables) {
            for (RelationalFK relationship : RelationalSQL.tableFKs(tableContext)) {
                List<RelationalColumn> sourceKeyColumns = relationship.getKeyColumns();
                result.put(relationship.getTable() + ">" + RelationalSQL.table(relationship.getForeignType()) + "@"
                        + serialize(sourceKeyColumns), relationship);
            }
        }
        return result;
    }

    private String serialize(List<RelationalColumn> columns) {
        StringJoiner sj = new StringJoiner(",");
        columns.forEach(column -> sj.add(column.toStringPersistence()));
        return sj.toString().toUpperCase();
    }

    private void reorderTargetTables(Map<String, RelationalFK> joinMap) {
        List<SType<?>> tables = new ArrayList<>(targetTables);
        for (int i = 0; i < tables.size() - 1; i++) {
            String tableLeft = RelationalSQL.table(tables.get(i));
            for (int j = i + 1; j < tables.size(); j++) {
                String tableRight = RelationalSQL.table(tables.get(j));
                if (joinMap.keySet().stream().anyMatch(item -> item.startsWith(tableRight + ">" + tableLeft + "@"))) {
                    SType<?> newRight = tables.get(i);
                    SType<?> newLeft = tables.get(j);
                    tables.set(i, newLeft);
                    tables.set(j, newRight);
                    i--;
                    break;
                }
            }
        }
        targetTables.clear();
        targetTables.addAll(tables);
    }
}
