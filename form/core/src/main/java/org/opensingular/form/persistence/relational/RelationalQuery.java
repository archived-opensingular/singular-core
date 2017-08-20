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

import static org.opensingular.form.persistence.relational.RelationalMapper.ASPECT_RELATIONAL_MAP;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringJoiner;

import org.opensingular.form.SType;

/**
 * Builder for Relational DBMS queries.
 *
 * @author Edmundo Andrade
 */
public class RelationalQuery {
	public static RelationalQuery select(Collection<SType<?>> fields) {
		return new RelationalQuery(fields);
	}

	private Collection<SType<?>> selectedFields;
	private List<String> selectedTables;
	private List<RelationalColumn> selectedColumns;
	private List<RelationalColumn> orderingColumns = new ArrayList<RelationalColumn>();

	public RelationalQuery(Collection<SType<?>> fields) {
		this.selectedFields = fields;
		this.selectedTables = new ArrayList<String>();
		this.selectedColumns = new ArrayList<RelationalColumn>();
		for (SType<?> field : fields)
			selectedColumns.add(selectField(field));
	}

	public RelationalQuery orderBy(SType<?> field) {
		orderingColumns.add(selectField(field));
		return this;
	}

	public Collection<SType<?>> getSelectedFields() {
		return selectedFields;
	}

	public String toSQL() {
		String orderPart = "";
		if (!orderingColumns.isEmpty())
			orderPart = " order by " + concatenateOrderingColumns(", ");
		return "select " + concatenateColumnNames(", ") + " from " + concatenateTableNames(", ") + orderPart;
	}

	private RelationalColumn selectField(SType<?> field) {
		String table = tableName(field);
		String column = columnName(field);
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

	private String tableName(SType<?> field) {
		String table = field.getAspect(ASPECT_RELATIONAL_MAP).get().table(field);
		if (table == null)
			field.getName();
		return table;
	}

	private String columnName(SType<?> field) {
		String column = field.getAspect(ASPECT_RELATIONAL_MAP).get().column(field);
		if (column == null)
			field.getNameSimple();
		return column;
	}
}
