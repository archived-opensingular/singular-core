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

/**
 * Builder for SQL queries on Relational DBMS.
 *
 * @author Edmundo Andrade
 */
public class RelationalSQLQuery implements RelationalSQL {
	private Collection<SType<?>> targetFields;
	private List<String> targetTables;
	private List<RelationalColumn> keyColumns;
	private List<RelationalColumn> targetColumns;
	private Map<String, String> mapColumnToField;
	private List<RelationalColumn> orderingColumns = new ArrayList<RelationalColumn>();
	private Map<String, RelationalFK> joinMap;
	private STypeComposite<?> keyFormType;
	private Map<String, Object> keyFormColumnMap;

	@SafeVarargs
	public RelationalSQLQuery(Collection<SType<?>>... fieldCollections) {
		this.targetFields = new ArrayList<>();
		for (Collection<SType<?>> fieldCollection : fieldCollections)
			this.targetFields.addAll(fieldCollection);
		this.targetTables = new ArrayList<String>();
		this.keyColumns = new ArrayList<RelationalColumn>();
		this.targetColumns = new ArrayList<RelationalColumn>();
		this.mapColumnToField = new HashMap<>();
		List<RelationalFK> relationships = new ArrayList<RelationalFK>();
		for (SType<?> field : targetFields) {
			RelationalSQL.collectKeyColumns(field, keyColumns, targetTables);
			RelationalSQL.collectTargetColumn(field, targetColumns, targetTables, Collections.emptyList(),
					mapColumnToField);
			RelationalSQL.collectRelationships(field, relationships);
		}
		joinMap = createJoinMap(relationships);
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
		return this;
	}

	public Collection<SType<?>> getTargetFields() {
		return targetFields;
	}

	public RelationalSQLCommmand[] toSQLScript() {
		List<Object> params = new ArrayList<>();
		String wherePart = "";
		if (keyFormType != null)
			wherePart += " where " + RelationalSQL.where(RelationalSQL.table(keyFormType), keyColumns, keyFormColumnMap,
					targetTables, params);
		String orderPart = "";
		if (!orderingColumns.isEmpty())
			orderPart = " order by " + concatenateOrderingColumns(", ");
		List<RelationalColumn> selected = selectedColumns();
		return new RelationalSQLCommmand[] { new RelationalSQLCommmand(
				"select " + concatenateColumnNames(selected, ", ") + " from " + joinTables() + wherePart + orderPart,
				params, null, selected) };
	}

	private List<RelationalColumn> selectedColumns() {
		List<RelationalColumn> result = new ArrayList<>(targetColumns);
		keyColumns.forEach(column -> {
			if (!targetColumns.contains(column))
				result.add(column);
		});
		return result;
	}

	private String concatenateColumnNames(List<RelationalColumn> columns, String separator) {
		StringJoiner sj = new StringJoiner(separator);
		columns.forEach(column -> sj.add(tableAlias(column.getTable()) + "." + column.getName()));
		return sj.toString();
	}

	private String joinTables() {
		StringJoiner sj = new StringJoiner(" left join ");
		List<String> joinedTables = new ArrayList<>();
		for (String table : targetTables) {
			sj.add(onClause(table, joinedTables));
			joinedTables.add(table);
		}
		return sj.toString();
	}

	private String onClause(String table, List<String> joinedTables) {
		String tableAlias = tableAlias(table);
		String result = table + " " + tableAlias;
		if (joinedTables.isEmpty())
			return result;
		RelationalFK relationship = locateRelationship(table, joinedTables);
		if (relationship == null)
			throw new SingularFormException(
					"Relational mapping should provide foreign key for relevant relationships with table '" + table
							+ "'.");
		String foreignTable = RelationalSQL.table(relationship.getForeignType());
		List<String> foreignColumns = RelationalSQL.tablePK(relationship.getForeignType());
		if (relationship.getKeyColumns().size() != foreignColumns.size())
			throw new SingularFormException(
					"Relational mapping should provide same-size foreign key for the relationship between table '"
							+ table + "' and '" + foreignTable + "'.");
		StringJoiner sj = new StringJoiner(" and ");
		for (int i = 0; i < foreignColumns.size(); i++)
			sj.add(tableAlias + "." + relationship.getKeyColumns().get(i).getName() + " = " + tableAlias(foreignTable)
					+ "." + foreignColumns.get(i));
		return result + " on " + sj.toString();
	}

	private RelationalFK locateRelationship(String table, List<String> joinedTables) {
		RelationalFK result = null;
		for (String joinedTable : joinedTables) {
			result = joinMap.get(table + ">" + joinedTable);
			if (result != null)
				break;
		}
		return result;
	}

	private String concatenateOrderingColumns(String separator) {
		StringJoiner sj = new StringJoiner(separator);
		orderingColumns.forEach(column -> sj.add(tableAlias(column.getTable()) + "." + column.getName()));
		return sj.toString();
	}

	private Map<String, RelationalFK> createJoinMap(List<RelationalFK> relationships) {
		Map<String, RelationalFK> result = new HashMap<>();
		for (RelationalFK relationship : relationships)
			result.put(relationship.getTable() + ">" + RelationalSQL.table(relationship.getForeignType()),
					relationship);
		return result;
	}

	private String tableAlias(String table) {
		return RelationalSQL.tableAlias(table, targetTables);
	}
}
