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
import java.util.StringJoiner;

import org.opensingular.form.SIComposite;
import org.opensingular.form.SInstance;

/**
 * Builder for SQL insertions on Relational DBMS.
 *
 * @author Edmundo Andrade
 */
public class RelationalSQLInsert implements RelationalSQL {
	private SIComposite instance;
	private List<String> targetTables;
	private List<RelationalColumn> keyColumns;
	private List<RelationalColumn> targetColumns;
	private Map<String, String> mapColumnToField;

	public RelationalSQLInsert(SIComposite instance) {
		this.instance = instance;
		this.targetTables = new ArrayList<String>();
		this.keyColumns = new ArrayList<RelationalColumn>();
		this.targetColumns = new ArrayList<RelationalColumn>();
		this.mapColumnToField = new HashMap<>();
		for (SInstance child : instance.getChildren()) {
			RelationalSQL.collectKeyColumns(child.getType(), keyColumns, targetTables);
			RelationalSQL.collectTargetColumn(child.getType(), targetColumns, targetTables, keyColumns,
					mapColumnToField);
		}
	}

	public SIComposite getInstance() {
		return instance;
	}

	public RelationalSQLCommmand[] toSQLScript() {
		List<RelationalSQLCommmand> lines = new ArrayList<>();
		for (String table : targetTables) {
			List<Object> params = new ArrayList<>();
			lines.add(new RelationalSQLCommmand("insert into " + table + " (" + concatenateColumnNames(table, ", ")
					+ ") values (" + concatenateColumnValues(table, ", ", params) + ")", params, instance));
		}
		return lines.toArray(new RelationalSQLCommmand[lines.size()]);
	}

	private String concatenateColumnNames(String table, String separator) {
		StringJoiner sj = new StringJoiner(separator);
		keyColumns.forEach(column -> {
			if (column.getTable().equals(table) && columnValue(column) != null)
				sj.add(column.getName());
		});
		targetColumns.forEach(column -> {
			if (column.getTable().equals(table) && columnValue(column) != null)
				sj.add(column.getName());
		});
		return sj.toString();
	}

	private String concatenateColumnValues(String table, String separator, List<Object> params) {
		StringJoiner sj = new StringJoiner(separator);
		keyColumns.forEach(column -> {
			if (column.getTable().equals(table) && columnValue(column) != null) {
				sj.add("?");
				params.add(columnValue(column));
			}
		});
		targetColumns.forEach(column -> {
			if (column.getTable().equals(table) && columnValue(column) != null) {
				sj.add("?");
				params.add(columnValue(column));
			}
		});
		return sj.toString();
	}

	private Object columnValue(RelationalColumn column) {
		String fieldName = mapColumnToField.get(column.getName());
		if (fieldName == null)
			return null;
		return instance.getValue(fieldName);
	}
}
