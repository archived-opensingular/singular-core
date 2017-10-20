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

import static org.opensingular.form.persistence.relational.RelationalColumnConverter.ASPECT_RELATIONAL_CONV;
import static org.opensingular.form.persistence.relational.RelationalMapper.ASPECT_RELATIONAL_MAP;
import static org.opensingular.form.persistence.relational.RelationalSQLAggregator.COUNT;
import static org.opensingular.form.persistence.relational.RelationalSQLAggregator.DISTINCT;
import static org.opensingular.form.persistence.relational.RelationalSQLAggregator.NONE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;

import org.opensingular.form.SIComposite;
import org.opensingular.form.SInstance;
import org.opensingular.form.SType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.SingularFormException;
import org.opensingular.form.persistence.FormKey;
import org.opensingular.form.persistence.relational.strategy.PersistenceStrategy;

/**
 * Abstract class for relational SQL builders.
 *
 * @author Edmundo Andrade
 */
public abstract class RelationalSQL {
	@SafeVarargs
	public static RelationalSQLQuery select(Collection<SType<?>>... fieldCollections) {
		return new RelationalSQLQuery(NONE, fieldCollections);
	}

	public static RelationalSQLQuery selectCount(SType<?> type) {
		return new RelationalSQLQuery(COUNT, Arrays.asList(type));
	}

	@SafeVarargs
	public static RelationalSQLQuery selectDistinct(Collection<SType<?>>... fieldCollections) {
		return new RelationalSQLQuery(DISTINCT, fieldCollections);
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
		return tableOpt(field).orElseThrow(() -> new SingularFormException(
				"Relational mapping should provide table name for the type '" + field.getName() + "'."));
	}

	public static Optional<String> tableOpt(SType<?> field) {
		return Optional.ofNullable(aspectRelationalMap(field).table(field));
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

	public static RelationalForeignColumn foreignColumn(SType<?> type) {
		return aspectRelationalMap(type).foreignColumn(type);
	}

	public static PersistenceStrategy persistenceStrategy(SType<?> field) {
		return aspectRelationalMap(field).persistenceStrategy(field);
	}

	static BasicRelationalMapper singletonBasicRelationalMapper = new BasicRelationalMapper();

	public static RelationalMapper aspectRelationalMap(SType<?> type) {
		Optional<RelationalMapper> mapper = type.getAspect(ASPECT_RELATIONAL_MAP);
		if (mapper.isPresent()) {
			return mapper.get();
		}
		return singletonBasicRelationalMapper;
	}

	public static Optional<RelationalColumnConverter> aspectRelationalColumnConverter(SType<?> type) {
		return type.getAspect(ASPECT_RELATIONAL_CONV);
	}

	public static SType<?> tableRef(SType<?> field) {
		SType<?> tableContext = tableContext(field);
		String columnName = column(field);
		for (RelationalFK fk : tableFKs(tableContext)) {
			if (fk.getKeyColumns().size() == 1 && fk.getKeyColumns().get(0).getName().equalsIgnoreCase(columnName)) {
				return fk.getForeignType();
			}
		}
		return null;
	}

	public static SInstance tupleKeyRef(SInstance instance) {
		SType<?> ancestorType = tableContext(instance.getType());
		for (SInstance parent = instance.getParent(); parent != null; parent = parent.getParent()) {
			if (parent.getType().isTypeOf(ancestorType)) {
				return parent;
			}
		}
		return null;
	}

	public static Object fieldValue(SInstance instance) {
		Optional<RelationalColumnConverter> converter = aspectRelationalColumnConverter(instance.getType());
		if (converter.isPresent()) {
			return converter.get().toRelationalColumn(instance);
		}
		return instance.getValue();
	}

	public static void setFieldValue(SInstance instance, List<RelationalData> fromList) {
		SType<?> field = instance.getType();
		SType<?> tableContext;
		String fieldName;
		RelationalForeignColumn foreignColumn = foreignColumn(field);
		if (foreignColumn == null) {
			tableContext = tableContext(field);
			fieldName = column(field);
		} else {
			tableContext = tableContext(foreignColumn.getForeignKey().getForeignType());
			fieldName = foreignColumn.getForeignColumn();
		}
		String tableName = table(tableContext);
		SInstance tupleKeyRef = tupleKeyRef(instance);
		Object value = getFieldValue(tableName, tupleKeyRef, fieldName, fromList);
		Optional<RelationalColumnConverter> converter = aspectRelationalColumnConverter(instance.getType());
		if (converter.isPresent()) {
			converter.get().fromRelationalColumn(value, instance);
		} else if (value == null) {
			instance.clearInstance();
		} else {
			instance.setValue(value);
		}
	}

	static Object getFieldValue(String tableName, SInstance tupleKeyRef, String fieldName,
			List<RelationalData> fromList) {
		for (RelationalData data : fromList) {
			if (data.getTableName().equals(tableName) && data.getTupleKeyRef().equals(tupleKeyRef)
					&& data.getFieldName().equals(fieldName)) {
				return data.getFieldValue();
			}
		}
		return null;
	}

	protected List<SType<?>> targetTables = new ArrayList<>();

	public abstract List<RelationalSQLCommmand> toSQLScript();

	protected List<SType<?>> getFields(SIComposite instance) {
		List<SType<?>> list = new ArrayList<>();
		instance.getAllChildren().forEach(child -> addFieldToList(child.getType(), list));
		return list;
	}

	protected List<SType<?>> getFields(STypeComposite<?> type) {
		List<SType<?>> list = new ArrayList<>();
		addFieldsToList(type.getFields(), list);
		return list;
	}

	protected void addFieldsToList(Collection<SType<?>> fields, List<SType<?>> list) {
		fields.forEach(field -> addFieldToList(field, list));
	}

	protected void addFieldToList(SType<?> field, List<SType<?>> list) {
		if (column(field) != null || foreignColumn(field) != null) {
			list.add(field);
		} else if (field.isComposite()) {
			addFieldsToList(getFields((STypeComposite<?>) field), list);
		}
	}

	protected void collectKeyColumns(SType<?> type, List<RelationalColumn> keyColumns) {
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

	protected void collectTargetColumn(SType<?> field, List<RelationalColumn> targetColumns,
			List<RelationalColumn> keyColumns, Map<String, SType<?>> mapColumnToField) {
		if (field.isList()) {
			return;
		}
		String columnName;
		SType<?> tableContext;
		RelationalForeignColumn foreignColumn = foreignColumn(field);
		if (foreignColumn == null) {
			tableContext = tableContext(field);
			columnName = column(field);
		} else {
			tableContext = tableContext(foreignColumn.getForeignKey().getForeignType());
			columnName = foreignColumn.getForeignColumn();
		}
		String tableName = table(tableContext);
		if (!targetTables.contains(tableContext)) {
			targetTables.add(tableContext);
		}
		RelationalColumn column = new RelationalColumn(tableName, columnName);
		mapColumnToField.put(column.toStringPersistence(), field);
		if (!targetColumns.contains(column) && !keyColumns.contains(column)) {
			targetColumns.add(column);
		}
	}

	protected String where(String table, List<RelationalColumn> filterColumns, Map<String, Object> mapColumnToValue,
			List<Object> params) {
		StringJoiner sj = new StringJoiner(" and ");
		filterColumns.forEach(column -> {
			if (column.getTable().equals(table)) {
				sj.add(tableAlias(table) + "." + column.getName() + " = ?");
				params.add(columnValue(column, mapColumnToValue));
			}
		});
		return sj.toString();
	}

	protected Object columnValue(RelationalColumn column, Map<String, Object> mapColumnToValue) {
		return mapColumnToValue.get(column.getName());
	}

	protected String tableAlias(String table) {
		int index = 1;
		for (SType<?> tableContext : targetTables) {
			if (table.equals(table(tableContext))) {
				return "T" + index;
			}
			index++;
		}
		return null;
	}

	protected Object fieldValue(SIComposite instance, SType<?> field) {
		String fieldPath = field.getName().replace(instance.getType().getName() + ".", "");
		return fieldValue(instance.getField(fieldPath));
	}
}
