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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;

import org.hibernate.SessionFactory;
import org.jetbrains.annotations.NotNull;
import org.opensingular.form.SIComposite;
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
		RelationalSQLCommmand[] insertScript = RelationalSQL.insert(instance).toSQLScript();
		for (RelationalSQLCommmand command : insertScript) {
			executeInsertCommand(command);
		}
		return FormKey.from(instance);
	}

	public void delete(@Nonnull FormKey key) {
		RelationalSQLCommmand[] deleteScript = RelationalSQL.delete(createType(), key).toSQLScript();
		for (RelationalSQLCommmand command : deleteScript) {
			dbExec(command.getCommand(), command.getParameters());
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
		RelationalSQL query = RelationalSQL.select(createType().getContainedTypes()).where(createType(), key);
		for (RelationalSQLCommmand command : query.toSQLScript()) {
			for (INSTANCE instance : executeSelectCommand(command)) {
				firstInstance = instance;
				break;
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
		return dbExec(sql, Collections.emptyList());
	}

	public int dbExec(String sql, List<Object> params) {
		return sessionFactory.getCurrentSession().doReturningWork(connection -> {
			if (params.isEmpty())
				return connection.createStatement().executeUpdate(sql);
			return prepareStatement(connection, sql, params).executeUpdate();
		});
	}

	public int dbExecReturningGenerated(String sql, List<Object> params, List<String> generatedColumns,
			ResultSetTupleHandler<?> tupleHandler) {
		return sessionFactory.getCurrentSession().doReturningWork(connection -> {
			String newSQL = sql;
			for (Object param : params) {
				newSQL = newSQL.replaceFirst("\\?", toSqlConstant(param));
			}
			System.out.println(sql);
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

	public List<Object[]> dbQuery(String sql, List<Object> params) {
		return dbQuery(sql, params, rs -> {
			Object[] tuple = new Object[rs.getMetaData().getColumnCount()];
			for (int i = 0; i < tuple.length; i++) {
				tuple[i] = rs.getObject(i + 1);
			}
			return tuple;
		});
	}

	public <T> List<T> dbQuery(String sql, List<Object> params, ResultSetTupleHandler<T> tupleHandler) {
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
		System.out.println(sql);
		PreparedStatement statement = connection.prepareStatement(sql);
		for (int i = 0; i < params.size(); i++) {
			statement.setObject(i + 1, params.get(i));
		}
		return statement;
	}

	@Nonnull
	protected List<INSTANCE> loadAllInternal(Long first, Long max) {
		List<INSTANCE> result = new ArrayList<>();
		RelationalSQL query = RelationalSQL.select(createType().getContainedTypes());
		for (RelationalSQLCommmand command : query.toSQLScript()) {
			result.addAll(executeSelectCommand(command));
		}
		return result;
	}

	protected List<INSTANCE> executeSelectCommand(RelationalSQLCommmand command) {
		return dbQuery(command.getCommand(), command.getParameters(), rs -> {
			INSTANCE instance = createInstance();
			List<String> pk = RelationalSQL.tablePK(instance.getType());
			FormKey.set(instance, tupleKey(rs, pk));
			RelationalSQL.persistenceStrategy(instance.getType()).load(instance, tuple(rs, command, pk));
			return instance;
		});
	}

	protected int executeInsertCommand(RelationalSQLCommmand command) {
		List<String> generatedColumns = RelationalSQL.tablePK(command.getInstance().getType());
		return dbExecReturningGenerated(command.getCommand(), command.getParameters(), generatedColumns, rs -> {
			HashMap<String, Object> key = new HashMap<>();
			for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
				key.put(generatedColumns.get(i), rs.getObject(i + 1));
			}
			FormKey.set(command.getInstance(), new FormKeyRelational(key));
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

	protected List<RelationalData> tuple(ResultSet rs, RelationalSQLCommmand command, List<String> pk)
			throws SQLException {
		List<RelationalData> tuple = new ArrayList<>();
		int index = 1;
		for (RelationalColumn column : command.getSelectedColumns()) {
			List<Object> tupleKey = new ArrayList<>();
			for (String keyColumn : pk) {
				tupleKey.add(rs.getObject(keyColumn));
			}
			tuple.add(new RelationalData(column.getTable(), tupleKey, column.getName(), rs.getObject(index)));
			index++;
		}
		return tuple;
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
