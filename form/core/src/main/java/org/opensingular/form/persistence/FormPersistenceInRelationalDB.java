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
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensingular.form.SIComposite;
import org.opensingular.form.SIList;
import org.opensingular.form.SInstance;
import org.opensingular.form.SType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.SingularFormException;
import org.opensingular.form.document.RefType;
import org.opensingular.form.document.SDocumentFactory;
import org.opensingular.form.persistence.relational.RelationalColumn;
import org.opensingular.form.persistence.relational.RelationalData;
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
	private FormKeyManager<FormKeyRelational> formKeyManager;

	public FormPersistenceInRelationalDB(RelationalDatabase db, SDocumentFactory documentFactory, Class<TYPE> type) {
		this.db = db;
		this.documentFactory = documentFactory;
		this.type = type;
	}

	@Nonnull
	public FormKey keyFromObject(@Nonnull Object objectValueToBeConverted) {
		return getFormKeyManager().keyFromObject(objectValueToBeConverted);
	}

	@Nonnull
	public FormKey insert(@Nonnull INSTANCE instance, Integer inclusionActor) {
		List<RelationalData> toList = new ArrayList<>();
		RelationalSQL.persistenceStrategy(instance.getType()).save(instance, toList);
		List<SIComposite> targets = new ArrayList<>();
		toList.forEach(data -> {
			if (!targets.contains(data.getTupleKeyRef())) {
				targets.add((SIComposite) data.getTupleKeyRef());
			}
		});
		for (SIComposite target : targets) {
			for (RelationalSQLCommmand command : RelationalSQL.insert(target).toSQLScript()) {
				executeInsertCommand(command);
			}
		}
		return FormKey.fromInstance(instance);
	}

	public void delete(@Nonnull FormKey key) {
		INSTANCE mainInstance = load(key);
		for (SInstance field : mainInstance.getAllChildren()) {
			if (field.getType().isList()) {
				SIList<SIComposite> listInstance = mainInstance.getFieldList(field.getType().getNameSimple(),
						SIComposite.class);
				for (SIComposite item : listInstance.getChildren()) {
					execScript(RelationalSQL.delete(item.getType(), FormKey.fromInstance(item)).toSQLScript());
				}
			}
		}
		execScript(RelationalSQL.delete(createType(), key).toSQLScript());
	}

	@SuppressWarnings("unchecked")
	@Nonnull
	private TYPE createType() {
		return (TYPE) RefType.of(type).get();
	}

	public void update(@Nonnull INSTANCE instance, Integer inclusionActor) {
		for (SInstance field : instance.getAllChildren()) {
			if (RelationalSQL.isListWithTableBound(field.getType())) {
				SIList<SIComposite> listInstance = instance.getFieldList(field.getType().getNameSimple(),
						SIComposite.class);
				for (SIComposite item : listInstance.getChildren()) {
					// TODO Synchronize inserts/updates
					// item.insertOrUpdate(item, inclusionActor);
				}
				// TODO Synchronize deletions
				// item.delete((FormKey.fromInstance(item));
			}
		}
		if (execScript(RelationalSQL.update(instance).toSQLScript()) == 0) {
			throw new SingularFormNotFoundException(FormKey.fromInstance(instance));
		}
	}

	@Nonnull
	public FormKey insertOrUpdate(@Nonnull INSTANCE instance, Integer inclusionActor) {
		if (isPersistent(instance)) {
			update(instance, inclusionActor);
		} else {
			insert(instance, inclusionActor);
		}
		return FormKey.fromInstance(instance);
	}

	public boolean isPersistent(@Nonnull INSTANCE instance) {
		return FormKey.containsKey(instance);
	}

	@Nonnull
	public FormKey newVersion(@Nonnull INSTANCE instance, Integer inclusionActor, boolean keepAnnotations) {
		throw new SingularFormException("Method not implemented.");
	}

	@Nonnull
	public INSTANCE load(@Nonnull FormKey key) {
		return loadOpt(key).orElseThrow(() -> new SingularFormNotFoundException(key));
	}

	@Nonnull
	public Optional<INSTANCE> loadOpt(@Nonnull FormKey key) {
		return Optional.ofNullable(loadInternal(key));
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
		long result = 0;
		RelationalSQL query = RelationalSQL.selectCount(createType());
		for (RelationalSQLCommmand command : query.toSQLScript()) {
			result += (long) db.query(command.getSQL(), command.getParameters()).get(0)[0];
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public INSTANCE createInstance() {
		return (INSTANCE) documentFactory.createInstance(RefType.of(type));
	}

	@Nonnull
	public FormKeyManager<FormKeyRelational> getFormKeyManager() {
		if (formKeyManager == null) {
			formKeyManager = new FormKeyManager<>(FormKeyRelational.class, e -> addInfo(e));
		}
		return formKeyManager;
	}

	@Nonnull
	protected SingularFormPersistenceException addInfo(@Nonnull SingularFormPersistenceException exception) {
		return exception.add("persistence", toString());
	}

	@Nullable
	protected INSTANCE loadInternal(@Nonnull FormKey key) {
		INSTANCE mainInstance = null;
		TYPE mainType = createType();
		RelationalSQL query = RelationalSQL.select(mainType.getContainedTypes()).where(mainType, key);
		for (RelationalSQLCommmand command : query.toSQLScript()) {
			for (INSTANCE instance : executeSelectCommand(command)) {
				mainInstance = instance;
				break;
			}
		}
		for (SType<?> field : mainType.getContainedTypes()) {
			if (field.isList()) {
				SIList<SIComposite> listInstance = mainInstance.getFieldList(field.getNameSimple(), SIComposite.class);
				for (SType<?> detail : field.getLocalTypes()) {
					STypeComposite<?> detailType = (STypeComposite<?>) detail.getSuperType();
					query = RelationalSQL.select(detailType.getContainedTypes()).where(mainType, key);
					for (RelationalSQLCommmand command : query.toSQLScript()) {
						executeSelectCommandIntoSIList(command, listInstance);
					}
				}
			}
		}
		return mainInstance;
	}

	@Nonnull
	protected List<INSTANCE> loadAllInternal(Long first, Long max) {
		List<INSTANCE> result = new ArrayList<>();
		RelationalSQL query = RelationalSQL.select(createType().getContainedTypes()).limit(first, max);
		for (RelationalSQLCommmand command : query.toSQLScript()) {
			result.addAll(executeSelectCommand(command));
		}
		return result;
	}

	protected List<INSTANCE> executeSelectCommand(RelationalSQLCommmand command) {
		return db.query(command.getSQL(), command.getParameters(), command.getLimitOffset(), command.getLimitRows(),
				rs -> {
					INSTANCE instance = createInstance();
					command.setInstance(instance);
					FormKey.setOnInstance(instance, tupleKey(rs, RelationalSQL.tablePK(instance.getType())));
					RelationalSQL.persistenceStrategy(instance.getType()).load(instance, tuple(rs, command));
					return instance;
				});
	}

	protected List<SIComposite> executeSelectCommandIntoSIList(RelationalSQLCommmand command,
			SIList<SIComposite> listInstance) {
		return db.query(command.getSQL(), command.getParameters(), command.getLimitOffset(), command.getLimitRows(),
				rs -> {
					SIComposite instance = listInstance.addNew();
					command.setInstance(instance);
					List<String> pk = RelationalSQL.tablePK(RelationalSQL.tableContext(instance.getType()));
					FormKey.setOnInstance(instance, tupleKey(rs, pk));
					RelationalSQL.persistenceStrategy(instance.getType()).load(instance, tuple(rs, command));
					return instance;
				});
	}

	protected int executeInsertCommand(RelationalSQLCommmand command) {
		List<String> pk = RelationalSQL.tablePK(RelationalSQL.tableContext(command.getInstance().getType()));
		HashMap<String, Object> key = new LinkedHashMap<>();
		pk.forEach(columnName -> key.put(columnName, parameterValue(columnName, command)));
		List<String> generatedColumns = serverSideGeneratedPKColumns(pk, command);
		int result = db.execReturningGenerated(command.getSQL(), command.getParameters(), generatedColumns, rs -> {
			for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
				key.put(generatedColumns.get(i), rs.getObject(i + 1));
			}
			return null;
		});
		FormKey.setOnInstance(command.getInstance(), new FormKeyRelational(key));
		return result;
	}

	protected FormKey tupleKey(ResultSet rs, List<String> pk) throws SQLException {
		HashMap<String, Object> key = new LinkedHashMap<>();
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

	protected int execScript(Collection<? extends RelationalSQLCommmand> script) {
		int result = 0;
		for (RelationalSQLCommmand command : script) {
			result += db.exec(command.getSQL(), command.getParameters());
		}
		return result;
	}

	protected List<String> serverSideGeneratedPKColumns(List<String> pk, RelationalSQLCommmand command) {
		List<String> result = new ArrayList<>();
		pk.forEach(columnName -> {
			if (parameterValue(columnName, command) == null) {
				result.add(columnName);
			}
		});
		return result;
	}

	private Object parameterValue(String columnName, RelationalSQLCommmand command) {
		Object result = null;
		int paramIndex = 0;
		for (RelationalColumn column : command.getColumns()) {
			if (column.getName().equals(columnName)) {
				result = command.getParameters().get(paramIndex);
				break;
			}
			paramIndex++;
		}
		return result;
	}
}
