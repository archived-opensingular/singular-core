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

import java.util.Optional;
import java.util.StringJoiner;
import java.util.regex.Pattern;

/**
 * Relational metadata for specifying column details in a Relational DBMS.
 *
 * @author Edmundo Andrade
 */
public class ColumnDefinition {
	private static final String SERIALIZATION_SEPARATOR = "|";
	private String table;
	private String column;
	private Optional<Integer> sqlType;

	public static ColumnDefinition fromStringPersistence(String value) {
		String parts[] = value.split(Pattern.quote(SERIALIZATION_SEPARATOR));
		Integer sqlType = parts[2].isEmpty() ? null : Integer.parseInt(parts[2]);
		return new ColumnDefinition(parts[0], parts[1], sqlType);
	}

	public ColumnDefinition(String table, String column, Integer sqlType) {
		this.table = table;
		this.column = column;
		this.sqlType = Optional.ofNullable(sqlType);
	}

	public String getTable() {
		return table;
	}

	public String getColumn() {
		return column;
	}

	public Optional<Integer> getSqlType() {
		return sqlType;
	}

	public String toStringPersistence() {
		StringJoiner sj = new StringJoiner(SERIALIZATION_SEPARATOR);
		sj.add(getTable());
		sj.add(getColumn());
		sj.add(getSqlType().isPresent() ? Integer.toString(getSqlType().get()) : "");
		return sj.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ColumnDefinition)
			return ((ColumnDefinition) obj).getTable().equals(getTable())
					&& ((ColumnDefinition) obj).getColumn().equals(getColumn())
					&& ((ColumnDefinition) obj).getSqlType().equals(getSqlType());
		return super.equals(obj);
	}
}
