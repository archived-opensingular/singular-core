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
import java.util.regex.Pattern;

/**
 * Relational metadata for identifying a foreign key in a Relational DBMS.
 *
 * @author Edmundo Andrade
 */
public class RelationalFK {
	private static final String SERIALIZATION_SEPARATOR = "|";
	private List<RelationalColumn> keyColumns;
	private String foreignTable;
	private List<RelationalColumn> foreignPK;

	public static RelationalFK fromStringPersistence(String value) {
		String parts[] = value.split(Pattern.quote(SERIALIZATION_SEPARATOR));
		return new RelationalFK(parseColumns(parts[0]), parts[1], parseColumns(parts[2]));
	}

	private static List<RelationalColumn> parseColumns(String value) {
		List<RelationalColumn> columns = new ArrayList<>();
		String parts[] = value.split(",");
		for (String part : parts)
			columns.add(RelationalColumn.fromStringPersistence(part));
		return columns;
	}

	public RelationalFK(String keyColumns, String foreignTable, String foreignPK) {
		this(parseColumns(keyColumns), foreignTable, parseColumns(foreignPK));
	}

	public RelationalFK(List<RelationalColumn> keyColumns, String foreignTable, List<RelationalColumn> foreignPK) {
		this.keyColumns = keyColumns;
		this.foreignTable = foreignTable;
		this.foreignPK = foreignPK;
	}

	public List<RelationalColumn> getKeyColumns() {
		return keyColumns;
	}

	public String getForeignTable() {
		return foreignTable;
	}

	public List<RelationalColumn> getForeignPK() {
		return foreignPK;
	}

	public String toStringPersistence() {
		StringJoiner sj = new StringJoiner(SERIALIZATION_SEPARATOR);
		sj.add(toStringPersistence(getKeyColumns()));
		sj.add(getForeignTable());
		sj.add(toStringPersistence(getForeignPK()));
		return sj.toString();
	}

	private String toStringPersistence(List<RelationalColumn> columns) {
		StringJoiner sj = new StringJoiner(",");
		columns.forEach(column -> sj.add(column.toStringPersistence()));
		return sj.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof RelationalFK)
			return ((RelationalFK) obj).getKeyColumns().equals(getKeyColumns())
					&& ((RelationalFK) obj).getForeignTable().equals(getForeignTable())
					&& ((RelationalFK) obj).getForeignPK().equals(getForeignPK());
		return super.equals(obj);
	}
}
