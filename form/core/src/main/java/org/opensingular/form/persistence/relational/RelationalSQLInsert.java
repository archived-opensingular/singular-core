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
import java.util.List;
import java.util.StringJoiner;

import org.opensingular.form.ICompositeInstance;
import org.opensingular.form.SInstance;
import org.opensingular.form.SType;

/**
 * Builder for SQL insertions on Relational DBMS.
 *
 * @author Edmundo Andrade
 */
public class RelationalSQLInsert implements RelationalSQL {
	private SInstance instance;
	private List<String> insertTables;
	private List<RelationalColumn> insertColumns;

	public RelationalSQLInsert(SInstance instance) {
		this.instance = instance;
		this.insertTables = new ArrayList<String>();
		this.insertColumns = new ArrayList<RelationalColumn>();
		if (instance instanceof ICompositeInstance) {
			int nextIndex = insertColumns.size();
			for (SInstance child : ((ICompositeInstance) instance).getChildren())
				insertColumns.add(selectField(child.getType()));
			for (SInstance child : ((ICompositeInstance) instance).getChildren())
				for (String name : RelationalSQL.keyColumns(child.getType())) {
					String table = RelationalSQL.tableName(child.getType());
					RelationalColumn column = new RelationalColumn(table, name);
					if (!insertColumns.contains(column)) {
						insertColumns.add(nextIndex, column);
						nextIndex++;
					}
				}
		}
	}

	public SInstance getInstance() {
		return instance;
	}

	public String[] toSQLScript() {
		List<String> lines = new ArrayList<>();
		for (String table : insertTables)
			lines.add("insert into " + table + " (" + concatenateColumnNames(table, ", ") + ") values ("
					+ concatenateColumnvalues(table, ", ") + ")");
		return lines.toArray(new String[lines.size()]);
	}

	private RelationalColumn selectField(SType<?> field) {
		String table = RelationalSQL.tableName(field);
		String column = RelationalSQL.columnName(field);
		if (!insertTables.contains(table))
			insertTables.add(table);
		return new RelationalColumn(table, column);
	}

	private String concatenateColumnNames(String table, String separator) {
		StringJoiner sj = new StringJoiner(separator);
		insertColumns.forEach((column) -> {
			if (column.getTable().equals(table))
				sj.add(column.getName());
		});
		return sj.toString();
	}

	private String concatenateColumnvalues(String table, String separator) {
		StringJoiner sj = new StringJoiner(separator);
		insertColumns.forEach((column) -> {
			if (column.getTable().equals(table))
				sj.add("?");
		});
		return sj.toString();
	}
}
