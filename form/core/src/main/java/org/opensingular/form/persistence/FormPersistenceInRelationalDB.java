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

package org.opensingular.form.persistence;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.NotNull;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SInstance;
import org.opensingular.form.SType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.document.RefType;
import org.opensingular.form.document.SDocumentFactory;
import org.opensingular.form.persistence.relational.FormKeyRelational;
import org.opensingular.form.persistence.relational.RelationalColumn;
import org.opensingular.form.persistence.relational.RelationalData;
import org.opensingular.form.persistence.relational.RelationalDatabase;
import org.opensingular.form.persistence.relational.RelationalFK;
import org.opensingular.form.persistence.relational.RelationalSQL;
import org.opensingular.form.persistence.relational.RelationalSQLCommmand;

/**
 * Form persistence based on relational database managers.
 *
 * @author Edmundo Andrade
 */
public class FormPersistenceInRelationalDB<TYPE extends STypeComposite<INSTANCE>, INSTANCE extends SIComposite>
		implements FormRespository<TYPE, INSTANCE> {
	protected RelationalDatabase db;
	private final SDocumentFactory documentFactory;
	private final Class<TYPE> type;

	public FormPersistenceInRelationalDB(RelationalDatabase db, SDocumentFactory documentFactory, Class<TYPE> type) {
		this.db = db;
		this.documentFactory = documentFactory;
		this.type = type;
	}

	// TODO Conversar com Daniel
	@Nonnull
	public FormKey keyFromObject(@Nonnull Object objectValueToBeConverted) {
		return null;
	}

	@Nonnull
	public FormKey insert(@Nonnull INSTANCE instance, Integer inclusionActor) {
		List<RelationalData> toList = new ArrayList<>();
		RelationalSQL.persistenceStrategy(instance.getType()).save(instance, toList);
		List<SIComposite> targets = new ArrayList<>();
		toList.forEach(data -> {
			if (!targets.contains(data.getTupleKeyRef()))
				targets.add((SIComposite) data.getTupleKeyRef());
		});
		for (SIComposite target : targets) {
			for (RelationalSQLCommmand command : RelationalSQL.insert(target).toSQLScript()) {
				checkForOneToManyRelationship(command);
				executeInsertCommand(command);
			}
		}
		return FormKey.from(instance);
	}

	public void delete(@Nonnull FormKey key) {
		INSTANCE instance = load(key);
		for (SInstance field : instance.getAllChildren()) {
			if (field.getType().isList()) {
				for (SInstance item : field.getChildren()) {
					System.out.println(item.getType().getName()+ " | "+item.getValue("item") + " | " + FormKey.from(item).toStringPersistence());
					db.execScript(
							RelationalSQL.delete((STypeComposite) item.getType(), FormKey.from(item)).toSQLScript());
				}
			}
		}
		db.execScript(RelationalSQL.delete(createType(), key).toSQLScript());
	}

	@SuppressWarnings("unchecked")
	@NotNull
	private TYPE createType() {
		return (TYPE) RefType.of(type).get();
	}

	// TODO
	public void update(@Nonnull INSTANCE instance, Integer inclusionActor) {
	}

	// TODO
	@Nonnull
	public FormKey insertOrUpdate(@Nonnull INSTANCE instance, Integer inclusionActor) {
		return null;
	}

	public boolean isPersistent(@Nonnull INSTANCE instance) {
		return false;
	}

	@Nonnull
	public FormKey newVersion(@Nonnull INSTANCE instance, Integer inclusionActor, boolean keepAnnotations) {
		return null;
	}

	@Nonnull
	public INSTANCE load(@Nonnull FormKey key) {
		INSTANCE mainInstance = null;
		TYPE mainType = createType();
		RelationalSQL query = RelationalSQL.select(mainType.getContainedTypes()).where(mainType, key);
		for (RelationalSQLCommmand command : query.toSQLScript()) {
			for (INSTANCE instance : executeSelectCommand(command, mainType)) {
				mainInstance = instance;
				break;
			}
		}
		for (SType<?> list : mainType.getContainedTypes()) {
			if (list.isList()) {
				for (SType<?> detail : list.getLocalTypes()) {
					STypeComposite<?> detailType = (STypeComposite<?>) detail.getSuperType();
					query = RelationalSQL.select(detailType.getContainedTypes()).where(mainType, key);
					for (RelationalSQLCommmand command : query.toSQLScript()) {
						for (INSTANCE instance : executeSelectCommand(command, (TYPE) detailType)) {
							mainInstance.getFieldList(list.getNameSimple(), SIComposite.class).addElement(instance);
						}
					}
				}
			}
		}
		if (mainInstance == null) {
			throw new SingularFormNotFoundException(key);
		}
		return mainInstance;
	}

	@Nonnull
	public Optional<INSTANCE> loadOpt(@Nonnull FormKey key) {
		return null;
	}

	@Nonnull
	public List<INSTANCE> loadAll(long first, long max) {
		return loadAllInternal(first, max);
	}

	@Nonnull
	public List<INSTANCE> loadAll() {
		return loadAllInternal(null, null);
	}

	public long countAll() {
		return 0;
	}

	@SuppressWarnings("unchecked")
	public INSTANCE createInstance() {
		return (INSTANCE) documentFactory.createInstance(RefType.of(type));
	}

	@Nonnull
	protected List<INSTANCE> loadAllInternal(Long first, Long max) {
		List<INSTANCE> result = new ArrayList<>();
		TYPE currentType = createType();
		RelationalSQL query = RelationalSQL.select(currentType.getContainedTypes());
		for (RelationalSQLCommmand command : query.toSQLScript()) {
			result.addAll(executeSelectCommand(command, currentType));
		}
		return result;
	}

	protected List<INSTANCE> executeSelectCommand(RelationalSQLCommmand command, TYPE currentType) {
		return db.query(command.getSQL(), command.getParameters(), rs -> {
			INSTANCE instance = (INSTANCE) documentFactory.createInstance(RefType.of(() -> currentType));
			command.setInstance(instance);
			FormKey.setOnInstance(instance, tupleKey(rs, RelationalSQL.tablePK(instance.getType())));
			RelationalSQL.persistenceStrategy(instance.getType()).load(instance, tuple(rs, command));
			return instance;
		});
	}

	protected int executeInsertCommand(RelationalSQLCommmand command) {
		List<String> generatedColumns = RelationalSQL.tablePK(command.getInstance().getType());
		return db.execReturningGenerated(command.getSQL(), command.getParameters(), generatedColumns, rs -> {
			HashMap<String, Object> key = new HashMap<>();
			for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
				key.put(generatedColumns.get(i), rs.getObject(i + 1));
			}
			FormKey.setOnInstance(command.getInstance(), new FormKeyRelational(key));
			return key;
		});
	}

	protected FormKey tupleKey(ResultSet rs, List<String> pk) throws SQLException {
		HashMap<String, Object> key = new HashMap<>();
		for (String keyColumn : pk) {
			key.put(keyColumn, rs.getObject(keyColumn));
		}
		return new FormKeyRelational(key);
	}

	protected List<RelationalData> tuple(ResultSet rs, RelationalSQLCommmand command) throws SQLException {
		List<RelationalData> tuple = new ArrayList<>();
		int index = 1;
		for (RelationalColumn column : command.getColumns()) {
			tuple.add(new RelationalData(column.getTable(), command.getInstance(), column.getName(),
					rs.getObject(index)));
			index++;
		}
		return tuple;
	}

	private void checkForOneToManyRelationship(RelationalSQLCommmand command) {
		SIComposite instance = command.getInstance();
		if (instance.getParent() == null) {
			return;
		}
		SInstance container = instance.getParent().getParent();
		FormKeyRelational containerKey = (FormKeyRelational) FormKey.from(container);
		List<String> containerPK = RelationalSQL.tablePK(container.getType());
		for (RelationalFK fk : RelationalSQL.tableFKs(instance.getType())) {
			if (fk.getForeignType().equals(container.getType())) {
				for (int i = 0; i < fk.getKeyColumns().size(); i++) {
					RelationalColumn keyColumn = fk.getKeyColumns().get(i);
					command.getParameters().set(command.getColumns().indexOf(keyColumn),
							containerKey.getColumnValue(containerPK.get(i)));
				}
			}
		}
	}
}
