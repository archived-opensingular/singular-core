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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;

import org.hibernate.SessionFactory;
import org.jetbrains.annotations.NotNull;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SInstance;
import org.opensingular.form.SType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.document.RefType;
import org.opensingular.form.document.SDocumentFactory;
import org.opensingular.form.persistence.FormKey;
import org.opensingular.form.persistence.FormRespository;
import org.opensingular.form.persistence.SingularFormNotFoundException;
import org.springframework.stereotype.Repository;

/**
 * JPA-based persistent repository.
 *
 * @author Edmundo Andrade
 */
@Repository
public class FormRepositoryHibernate<TYPE extends STypeComposite<INSTANCE>, INSTANCE extends SIComposite>
		implements FormRespository<TYPE, INSTANCE> {
	private final SessionFactory sessionFactory;
	private final SDocumentFactory documentFactory;
	private final Class<TYPE> type;

	public FormRepositoryHibernate(SessionFactory session, SDocumentFactory documentFactory, Class<TYPE> type) {
		this.sessionFactory = session;
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
			RelationalSQLCommmand[] insertScript = RelationalSQL.insert(target).toSQLScript();
			for (RelationalSQLCommmand command : insertScript) {
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
					RelationalSQLCommmand[] deleteScript = RelationalSQL
							.delete((STypeComposite) item.getType(), FormKey.from(item)).toSQLScript();
					for (RelationalSQLCommmand command : deleteScript) {
						exec(command.getCommand(), command.getParameters());
					}
				}
			}
		}
		RelationalSQLCommmand[] deleteScript = RelationalSQL.delete(createType(), key).toSQLScript();
		for (RelationalSQLCommmand command : deleteScript) {
			exec(command.getCommand(), command.getParameters());
		}
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
		INSTANCE firstInstance = null;
		TYPE currentType = createType();
		RelationalSQL query = RelationalSQL.select(currentType.getContainedTypes()).where(currentType, key);
		for (RelationalSQLCommmand command : query.toSQLScript()) {
			for (INSTANCE instance : executeSelectCommand(command, currentType)) {
				firstInstance = instance;
				break;
			}
		}
		for (SType<?> list : currentType.getContainedTypes()) {
			if (list.isList()) {
				for (SType<?> detail : list.getLocalTypes()) {
					STypeComposite<?> detailCurrentType = (STypeComposite<?>) detail.getSuperType();
					query = RelationalSQL.select(currentType.getContainedTypes()).where(currentType, key);
					for (RelationalSQLCommmand command : query.toSQLScript()) {
						for (INSTANCE instance : executeSelectCommand(command, (TYPE) detailCurrentType)) {
						}
					}
				}
			}
		}
		if (firstInstance == null) {
			throw new SingularFormNotFoundException(key);
		}
		return firstInstance;
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

	public int exec(String sql) {
		return sessionFactory.getCurrentSession().doReturningWork(connection -> {
			return connection.createStatement().executeUpdate(sql);
		});
	}

	public int exec(String sql, List<Object> params) {
		return sessionFactory.getCurrentSession().doReturningWork(connection -> {
			return prepareStatement(connection, sql, params).executeUpdate();
		});
	}

	public int execReturningGenerated(String sql, List<Object> params, List<String> generatedColumns,
			ResultSetTupleHandler<?> tupleHandler) {
		return sessionFactory.getCurrentSession().doReturningWork(connection -> {
			String newSQL = sql;
			for (Object param : params) {
				newSQL = newSQL.replaceFirst("\\?", toSqlConstant(param));
			}
			Statement statement = connection.createStatement();
			int result = statement.executeUpdate(newSQL, generatedColumns.toArray(new String[generatedColumns.size()]));
			try (ResultSet rs = statement.getGeneratedKeys()) {
				while (rs.next()) {
					tupleHandler.tuple(rs);
				}
			}
			return result;
		});
	}

	public List<Object[]> query(String sql, List<Object> params) {
		return query(sql, params, rs -> {
			Object[] tuple = new Object[rs.getMetaData().getColumnCount()];
			for (int i = 0; i < tuple.length; i++) {
				tuple[i] = rs.getObject(i + 1);
			}
			return tuple;
		});
	}

	public <T> List<T> query(String sql, List<Object> params, ResultSetTupleHandler<T> tupleHandler) {
		return sessionFactory.getCurrentSession().doReturningWork(connection -> {
			List<T> result = new ArrayList<>();
			try (ResultSet rs = prepareStatement(connection, sql, params).executeQuery()) {
				while (rs.next()) {
					result.add(tupleHandler.tuple(rs));
				}
			}
			return result;
		});
	}

	protected PreparedStatement prepareStatement(Connection connection, String sql, List<Object> params)
			throws SQLException {
		PreparedStatement statement = connection.prepareStatement(sql);
		for (int i = 0; i < params.size(); i++) {
			statement.setObject(i + 1, params.get(i));
		}
		return statement;
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
		return query(command.getCommand(), command.getParameters(), rs -> {
			INSTANCE instance = (INSTANCE) documentFactory.createInstance(RefType.of(() -> currentType));
			command.setInstance(instance);
			FormKey.setOnInstance(instance, tupleKey(rs, RelationalSQL.tablePK(instance.getType())));
			RelationalSQL.persistenceStrategy(instance.getType()).load(instance, tuple(rs, command));
			return instance;
		});
	}

	protected int executeInsertCommand(RelationalSQLCommmand command) {
		List<String> generatedColumns = RelationalSQL.tablePK(command.getInstance().getType());
		return execReturningGenerated(command.getCommand(), command.getParameters(), generatedColumns, rs -> {
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

	private String toSqlConstant(Object parameterValue) {
		if (parameterValue == null) {
			return "NULL";
		} else if (parameterValue instanceof String) {
			return "'" + ((String) parameterValue).replace("'", "''") + "'";
		}
		return parameterValue.toString();
	}
}
