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

import java.util.Collection;
import java.util.List;

import org.opensingular.form.SInstance;
import org.opensingular.form.SType;

/**
 * Interface for relational SQL builders.
 *
 * @author Edmundo Andrade
 */
public interface RelationalSQL {
	String[] toSQLScript();

	public static RelationalSQLQuery select(Collection<SType<?>> fields) {
		return new RelationalSQLQuery(fields);
	}

	public static RelationalSQLInsert insert(SInstance instance) {
		return new RelationalSQLInsert(instance);
	}

	public static RelationalSQLDelete delete(SInstance instance) {
		return new RelationalSQLDelete(instance);
	}

	public static String tableName(SType<?> field) {
		String table = field.getAspect(ASPECT_RELATIONAL_MAP).get().table(field);
		if (table == null)
			field.getName();
		return table;
	}

	public static String columnName(SType<?> field) {
		String column = field.getAspect(ASPECT_RELATIONAL_MAP).get().column(field);
		if (column == null)
			field.getNameSimple();
		return column;
	}

	public static List<String> keyColumns(SType<?> type) {
		return type.getAspect(ASPECT_RELATIONAL_MAP).get().keyColumns(type);
	}
}
