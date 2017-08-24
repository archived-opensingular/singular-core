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
import java.util.List;
import java.util.StringJoiner;

import org.opensingular.form.SType;

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
	private List<RelationalColumn> orderingColumns = new ArrayList<RelationalColumn>();
	private List<RelationalFK> relationships = new ArrayList<RelationalFK>();

	@SafeVarargs
	public RelationalSQLQuery(Collection<SType<?>>... fieldCollections) {
		this.targetFields = new ArrayList<>();
		for (Collection<SType<?>> fieldCollection : fieldCollections)
			this.targetFields.addAll(fieldCollection);
		this.targetTables = new ArrayList<String>();
		this.keyColumns = new ArrayList<RelationalColumn>();
		this.targetColumns = new ArrayList<RelationalColumn>();
		for (SType<?> field : targetFields) {
			RelationalSQL.collectKeyColumns(field, keyColumns, targetTables);
			RelationalSQL.collectTargetColumn(field, targetColumns, targetTables, Collections.emptyList());
			relationships.addAll(RelationalSQL.tableFKs(field));
		}
	}

	public RelationalSQLQuery orderBy(SType<?>... fields) {
		orderingColumns.clear();
		for (SType<?> field : fields)
			RelationalSQL.collectTargetColumn(field, orderingColumns, targetTables, Collections.emptyList());
		return this;
	}

	public Collection<SType<?>> getTargetFields() {
		return targetFields;
	}

	public String[] toSQLScript() {
		String orderPart = "";
		if (!orderingColumns.isEmpty())
			orderPart = " order by " + concatenateOrderingColumns(", ");
		return new String[] { "select " + concatenateColumnNames(", ") + " from " + joinTables() + orderPart };
	}

	private String concatenateColumnNames(String separator) {
		StringJoiner sj = new StringJoiner(separator);
		targetColumns.forEach(column -> sj.add(tableAlias(column.getTable()) + "." + column.getName()));
		keyColumns.forEach(column -> {
			if (!targetColumns.contains(column))
				sj.add(tableAlias(column.getTable()) + "." + column.getName());
		});
		return sj.toString();
	}

	private String joinTables() {
		StringJoiner sj = new StringJoiner(" left join ");
		targetTables.forEach(table -> sj.add(table + " " + tableAlias(table)));
		return sj.toString();
	}

	private String concatenateOrderingColumns(String separator) {
		StringJoiner sj = new StringJoiner(separator);
		orderingColumns.forEach(column -> sj.add(tableAlias(column.getTable()) + "." + column.getName()));
		return sj.toString();
	}

	private String tableAlias(String table) {
		int index = targetTables.indexOf(table) + 1;
		return "T" + index;
	}
}
