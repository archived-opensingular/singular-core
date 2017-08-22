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
import java.util.List;
import java.util.StringJoiner;

import org.opensingular.form.SType;

/**
 * Builder for SQL queries on Relational DBMS.
 *
 * @author Edmundo Andrade
 */
public class RelationalSQLQuery implements RelationalSQL {
	private Collection<SType<?>> selectedFields;
	private List<String> selectedTables;
	private List<RelationalColumn> selectedColumns;
	private List<RelationalColumn> orderingColumns = new ArrayList<RelationalColumn>();

	public RelationalSQLQuery(Collection<SType<?>> fields) {
		this.selectedFields = fields;
		this.selectedTables = new ArrayList<String>();
		this.selectedColumns = new ArrayList<RelationalColumn>();
		int nextIndex = selectedColumns.size();
		for (SType<?> field : fields)
			selectedColumns.add(selectField(field));
		for (SType<?> field : fields)
			for (String name : RelationalSQL.keyColumns(field)) {
				String table = RelationalSQL.tableName(field);
				RelationalColumn column = new RelationalColumn(table, name);
				if (!selectedColumns.contains(column)) {
					selectedColumns.add(nextIndex, column);
					nextIndex++;
				}
			}
	}

	public RelationalSQLQuery orderBy(SType<?> field) {
		orderingColumns.add(selectField(field));
		return this;
	}

	public Collection<SType<?>> getSelectedFields() {
		return selectedFields;
	}

	public String[] toSQLScript() {
		String orderPart = "";
		if (!orderingColumns.isEmpty())
			orderPart = " order by " + concatenateOrderingColumns(", ");
		return new String[] {
				"select " + concatenateColumnNames(", ") + " from " + concatenateTableNames(", ") + orderPart };
	}

	private RelationalColumn selectField(SType<?> field) {
		String table = RelationalSQL.tableName(field);
		String column = RelationalSQL.columnName(field);
		if (!selectedTables.contains(table))
			selectedTables.add(table);
		return new RelationalColumn(table, column);
	}

	private String concatenateColumnNames(String separator) {
		StringJoiner sj = new StringJoiner(separator);
		selectedColumns.forEach((column) -> sj.add(tableAlias(column.getTable()) + "." + column.getName()));
		return sj.toString();
	}

	private String concatenateTableNames(String separator) {
		StringJoiner sj = new StringJoiner(separator);
		selectedTables.forEach((table) -> sj.add(table + " " + tableAlias(table)));
		return sj.toString();
	}

	private String concatenateOrderingColumns(String separator) {
		StringJoiner sj = new StringJoiner(separator);
		orderingColumns.forEach((column) -> sj.add(tableAlias(column.getTable()) + "." + column.getName()));
		return sj.toString();
	}

	private String tableAlias(String table) {
		int index = selectedTables.indexOf(table) + 1;
		return "T" + index;
	}
}
