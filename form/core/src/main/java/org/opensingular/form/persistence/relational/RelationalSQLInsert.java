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

/**
 * Builder for SQL insertions on Relational DBMS.
 *
 * @author Edmundo Andrade
 */
public class RelationalSQLInsert implements RelationalSQL {
	private ICompositeInstance instance;
	private List<String> targetTables;
	private List<RelationalColumn> keyColumns;
	private List<RelationalColumn> targetColumns;

	public RelationalSQLInsert(ICompositeInstance instance) {
		this.instance = instance;
		this.targetTables = new ArrayList<String>();
		this.keyColumns = new ArrayList<RelationalColumn>();
		this.targetColumns = new ArrayList<RelationalColumn>();
		for (SInstance child : instance.getChildren()) {
			RelationalSQL.collectKeyColumns(child.getType(), keyColumns, targetTables);
			RelationalSQL.collectTargetColumn(child.getType(), targetColumns, targetTables, keyColumns);
		}
	}

	public ICompositeInstance getInstance() {
		return instance;
	}

	public String[] toSQLScript() {
		List<String> lines = new ArrayList<>();
		for (String table : targetTables)
			lines.add("insert into " + table + " (" + concatenateColumnNames(table, ", ") + ") values ("
					+ concatenateColumnvalues(table, ", ") + ")");
		return lines.toArray(new String[lines.size()]);
	}

	private String concatenateColumnNames(String table, String separator) {
		StringJoiner sj = new StringJoiner(separator);
		keyColumns.forEach(column -> {
			if (column.getTable().equals(table))
				sj.add(column.getName());
		});
		targetColumns.forEach(column -> {
			if (column.getTable().equals(table))
				sj.add(column.getName());
		});
		return sj.toString();
	}

	private String concatenateColumnvalues(String table, String separator) {
		StringJoiner sj = new StringJoiner(separator);
		keyColumns.forEach(column -> {
			if (column.getTable().equals(table))
				sj.add("?");
		});
		targetColumns.forEach(column -> {
			if (column.getTable().equals(table))
				sj.add("?");
		});
		return sj.toString();
	}
}
