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
import java.util.Optional;
import java.util.StringJoiner;

import org.opensingular.form.ICompositeType;
import org.opensingular.form.SInstance;
import org.opensingular.form.SType;

/**
 * Interface for relational SQL builders.
 *
 * @author Edmundo Andrade
 */
public interface RelationalSQL {
	String[] toSQLScript();

	@SafeVarargs
	public static RelationalSQLQuery select(Collection<SType<?>>... fieldCollections) {
		return new RelationalSQLQuery(fieldCollections);
	}

	public static RelationalSQLInsert insert(SInstance instance) {
		return new RelationalSQLInsert(instance);
	}

	public static RelationalSQLUpdate update(SInstance instance) {
		return new RelationalSQLUpdate(instance);
	}

	public static RelationalSQLDelete delete(SInstance instance) {
		return new RelationalSQLDelete(instance);
	}

	public static String table(SType<?> field) {
		return aspectRelationalMap(field).table(field);
	}

	public static List<String> tablePK(SType<?> type) {
		return aspectRelationalMap(type).tablePK(type);
	}

	public static List<RelationalFK> tableFKs(SType<?> type) {
		return aspectRelationalMap(type).tableFKs(type);
	}

	public static String column(SType<?> field) {
		return aspectRelationalMap(field).column(field);
	}

	public static RelationalMapper aspectRelationalMap(SType<?> field) {
		Optional<RelationalMapper> mapper = field.getAspect(ASPECT_RELATIONAL_MAP);
		if (mapper.isPresent())
			return mapper.get();
		return new BasicRelationalMapper();
	}

	public static void collectKeyColumns(SType<?> type, List<RelationalColumn> keyColumns, List<String> targetTables) {
		String tableName = table(type);
		if (!targetTables.contains(tableName))
			targetTables.add(tableName);
		for (String columnName : tablePK(type)) {
			RelationalColumn column = new RelationalColumn(tableName, columnName);
			if (!keyColumns.contains(column))
				keyColumns.add(column);
		}
	}

	public static void collectTargetColumn(SType<?> field, List<RelationalColumn> targetColumns,
			List<String> targetTables, List<RelationalColumn> keyColumns) {
		if (field instanceof ICompositeType)
			return;
		String tableName = table(field);
		if (!targetTables.contains(tableName))
			targetTables.add(tableName);
		String columnName = column(field);
		RelationalColumn column = new RelationalColumn(tableName, columnName);
		if (!targetColumns.contains(column) && !keyColumns.contains(column))
			targetColumns.add(column);
	}

	public static String where(String table, List<RelationalColumn> filterColumns) {
		StringJoiner sj = new StringJoiner(" and ");
		filterColumns.forEach(column -> {
			if (column.getTable().equals(table))
				sj.add(column.getName() + " = ?");
		});
		return sj.toString();
	}

	public static void collectRelationships(SType<?> field, List<RelationalFK> relationships) {
		if (field instanceof ICompositeType)
			((ICompositeType) field).getContainedTypes()
					.forEach(item -> relationships.addAll(RelationalSQL.tableFKs(item.getSuperType())));
	}
}
