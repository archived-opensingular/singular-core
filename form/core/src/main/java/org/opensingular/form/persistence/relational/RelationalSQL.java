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
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;

import org.opensingular.form.ICompositeType;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.persistence.FormKey;
import org.opensingular.form.persistence.relational.strategy.PersistenceStrategy;

/**
 * Interface for relational SQL builders.
 *
 * @author Edmundo Andrade
 */
public interface RelationalSQL {
	List<RelationalSQLCommmand> toSQLScript();

	@SafeVarargs
	public static RelationalSQLQuery select(Collection<SType<?>>... fieldCollections) {
		return new RelationalSQLQuery(fieldCollections);
	}

	public static RelationalSQLInsert insert(SIComposite instance) {
		return new RelationalSQLInsert(instance);
	}

	public static RelationalSQLUpdate update(SIComposite instance) {
		return new RelationalSQLUpdate(instance);
	}

	public static RelationalSQLDelete delete(STypeComposite<?> type, FormKey formKey) {
		return new RelationalSQLDelete(type, formKey);
	}

	public static SType<?> tableContext(SType<?> type) {
		return aspectRelationalMap(type).tableContext(type);
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

	public static PersistenceStrategy persistenceStrategy(SType<?> field) {
		return aspectRelationalMap(field).persistenceStrategy(field);
	}

	public static RelationalMapper aspectRelationalMap(SType<?> type) {
		Optional<RelationalMapper> mapper = type.getAspect(ASPECT_RELATIONAL_MAP);
		if (mapper.isPresent()) {
			return mapper.get();
		}
		return new BasicRelationalMapper();
	}

	public static void collectKeyColumns(SType<?> type, List<RelationalColumn> keyColumns,
			List<SType<?>> targetTables) {
		SType<?> tableContext = tableContext(type);
		String tableName = table(tableContext);
		if (!targetTables.contains(tableContext)) {
			targetTables.add(tableContext);
		}
		List<String> pk = tablePK(tableContext);
		if (pk != null) {
			for (String columnName : pk) {
				RelationalColumn column = new RelationalColumn(tableName, columnName);
				if (!keyColumns.contains(column)) {
					keyColumns.add(column);
				}
			}
		}
	}

	public static void collectTargetColumn(SType<?> field, List<RelationalColumn> targetColumns,
			List<SType<?>> targetTables, List<RelationalColumn> keyColumns, Map<String, String> mapColumnToField) {
		if (field instanceof ICompositeType) {
			return;
		}
		SType<?> tableContext = tableContext(field);
		String tableName = table(tableContext);
		if (!targetTables.contains(tableContext)) {
			targetTables.add(tableContext);
		}
		String columnName = column(field);
		mapColumnToField.put(columnName, field.getNameSimple());
		RelationalColumn column = new RelationalColumn(tableName, columnName);
		if (!targetColumns.contains(column) && !keyColumns.contains(column)) {
			targetColumns.add(column);
		}
	}

	public static String where(String table, List<RelationalColumn> filterColumns, Map<String, Object> mapColumnToValue,
			List<SType<?>> targetTables, List<Object> params) {
		StringJoiner sj = new StringJoiner(" and ");
		filterColumns.forEach(column -> {
			if (column.getTable().equals(table)) {
				sj.add(tableAlias(table, targetTables) + "." + column.getName() + " = ?");
				params.add(columnValue(column, mapColumnToValue));
			}
		});
		return sj.toString();
	}

	public static String tableAlias(String table, List<SType<?>> targetTables) {
		int index = 1;
		for (SType<?> tableContext : targetTables) {
			if (table.equals(table(tableContext))) {
				return "T" + index;
			}
			index++;
		}
		return null;
	}

	public static Object columnValue(RelationalColumn column, Map<String, Object> mapColumnToValue) {
		return mapColumnToValue.get(column.getName());
	}
}
