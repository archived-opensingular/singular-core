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
import java.util.List;
import java.util.Map;
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
public class RelationalSQLQuery implements RelationalSQL {
	private RelationalSQLAggregator aggregator;
	private List<SType<?>> targetTables = new ArrayList<>();
	private Collection<SType<?>> targetFields = new ArrayList<>();;
	private List<RelationalColumn> keyColumns;
	private List<RelationalColumn> targetColumns;
	private Map<String, String> mapColumnToField;
	private List<RelationalColumn> orderingColumns = new ArrayList<>();
	private STypeComposite<?> keyFormType;
	private Map<String, Object> keyFormColumnMap;
	private List<RelationalColumn> keyFormColumns;
	private Long limitOffset;
	private Long limitRows;

	@SafeVarargs
	public RelationalSQLQuery(RelationalSQLAggregator aggregator, Collection<SType<?>>... fieldCollections) {
		this.aggregator = aggregator;
		for (Collection<SType<?>> fieldCollection : fieldCollections)
			this.targetFields.addAll(fieldCollection);
		this.keyColumns = new ArrayList<>();
		this.targetColumns = new ArrayList<>();
		this.mapColumnToField = new HashMap<>();
		for (SType<?> field : targetFields) {
			RelationalSQL.collectKeyColumns(field, keyColumns, targetTables);
			RelationalSQL.collectTargetColumn(field, targetColumns, targetTables, Collections.emptyList(),
					mapColumnToField);
		}
	}

	public RelationalSQLQuery orderBy(SType<?>... fields) {
		orderingColumns.clear();
		for (SType<?> field : fields)
			RelationalSQL.collectTargetColumn(field, orderingColumns, targetTables, Collections.emptyList(),
					mapColumnToField);
		return this;
	}

	public RelationalSQLQuery where(STypeComposite<?> type, FormKey formKey) {
		keyFormType = type;
		keyFormColumnMap = ((FormKeyRelational) formKey).getValue();
		keyFormColumns = new ArrayList<>();
		RelationalSQL.collectKeyColumns(keyFormType, keyFormColumns, targetTables);
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

	public List<RelationalSQLCommmand> toSQLScript() {
		Map<String, RelationalFK> joinMap = createJoinMap();
		reorderTargetTables(joinMap);
		List<Object> params = new ArrayList<>();
		String wherePart = "";
		if (keyFormType != null) {
			wherePart += " where " + RelationalSQL.where(RelationalSQL.table(keyFormType), keyFormColumns,
					keyFormColumnMap, targetTables, params);
		}
		String orderPart = "";
		if (!orderingColumns.isEmpty()) {
			orderPart = " order by " + concatenateOrderingColumns(", ");
		}
		List<RelationalColumn> selected = selectedColumns();
		String sql = "select " + selectPart(concatenateColumnNames(selected, ", ")) + " from " + joinTables(joinMap)
				+ wherePart + orderPart;
		return Arrays.asList(new RelationalSQLCommmand(sql, params, null, selected, limitOffset, limitRows));
	}

	private String selectPart(String columnsSequence) {
		String result;
		if (aggregator.equals(COUNT))
			result = "count(*)";
		else if (aggregator.equals(DISTINCT))
			result = "distinct " + columnsSequence;
		else
			result = columnsSequence;
		return result;
	}

	private List<RelationalColumn> selectedColumns() {
		List<RelationalColumn> result = new ArrayList<>(targetColumns);
		keyColumns.forEach(column -> {
			if (aggregator.equals(NONE) && !targetColumns.contains(column)) {
				result.add(column);
			}
		});
		return result;
	}

	private String concatenateColumnNames(List<RelationalColumn> columns, String separator) {
		StringJoiner sj = new StringJoiner(separator);
		columns.forEach(column -> sj.add(tableAlias(column.getTable()) + "." + column.getName()));
		return sj.toString();
	}

	private String joinTables(Map<String, RelationalFK> joinMap) {
		StringJoiner sj = new StringJoiner(" left join ");
		List<String> joinedTables = new ArrayList<>();
		for (SType<?> tableContext : targetTables) {
			sj.add(onClause(tableContext, joinedTables, joinMap));
			joinedTables.add(RelationalSQL.table(tableContext));
		}
		return sj.toString();
	}

	private String onClause(SType<?> tableContext, List<String> joinedTables, Map<String, RelationalFK> joinMap) {
		String table = RelationalSQL.table(tableContext);
		String tableAlias = tableAlias(table);
		String result = table + " " + tableAlias;
		if (joinedTables.isEmpty())
			return result;
		RelationalFK relationship = locateRelationship(table, joinedTables, joinMap);
		if (relationship == null)
			throw new SingularFormException(
					"Relational mapping should provide foreign key for relevant relationships with table '" + table
							+ "'.");
		String leftTable = relationship.getTable();
		List<RelationalColumn> leftColumns = relationship.getKeyColumns();
		List<String> rightColumns = RelationalSQL.tablePK(tableContext);
		if (leftColumns.size() != rightColumns.size())
			throw new SingularFormException(
					"Relational mapping should provide compatible-size foreign key for the relationship between table '"
							+ leftTable + "' and '" + table + "'.");
		StringJoiner sj = new StringJoiner(" and ");
		for (int i = 0; i < leftColumns.size(); i++)
			sj.add(tableAlias(leftTable) + "." + leftColumns.get(i).getName() + " = " + tableAlias + "."
					+ rightColumns.get(i));
		return result + " on " + sj.toString();
	}

	private RelationalFK locateRelationship(String table, List<String> joinedTables,
			Map<String, RelationalFK> joinMap) {
		RelationalFK result = null;
		for (String joinedTable : joinedTables) {
			result = joinMap.get(joinedTable + ">" + table);
			if (result != null) {
				break;
			}
		}
		return result;
	}

	private String concatenateOrderingColumns(String separator) {
		StringJoiner sj = new StringJoiner(separator);
		orderingColumns.forEach(column -> sj.add(tableAlias(column.getTable()) + "." + column.getName()));
		return sj.toString();
	}

	private Map<String, RelationalFK> createJoinMap() {
		Map<String, RelationalFK> result = new HashMap<>();
		for (SType<?> tableContext : targetTables) {
			for (RelationalFK relationship : RelationalSQL.tableFKs(tableContext)) {
				result.put(relationship.getTable() + ">" + RelationalSQL.table(relationship.getForeignType()),
						relationship);
			}
		}
		return result;
	}

	private void reorderTargetTables(Map<String, RelationalFK> joinMap) {
		for (int i = 0; i < targetTables.size() - 1; i++) {
			String tableLeft = RelationalSQL.table(targetTables.get(i));
			for (int j = i + 1; j < targetTables.size(); j++) {
				String tableRight = RelationalSQL.table(targetTables.get(j));
				if (joinMap.containsKey(tableRight + ">" + tableLeft)) {
					SType<?> newRight = targetTables.get(i);
					SType<?> newLeft = targetTables.get(j);
					targetTables.set(i, newLeft);
					targetTables.set(j, newRight);
					i--;
					break;
				}
			}
		}
	}

	private String tableAlias(String table) {
		return RelationalSQL.tableAlias(table, targetTables);
	}
}
