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
import org.hibernate.jdbc.ReturningWork;
import org.hibernate.jdbc.Work;
import org.jetbrains.annotations.NotNull;
import org.opensingular.form.SIComposite;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.document.RefType;
import org.opensingular.form.document.SDocumentFactory;
import org.opensingular.form.persistence.FormKey;
import org.opensingular.form.persistence.FormRespository;
import org.opensingular.form.persistence.SingularFormNotFoundException;

/**
 * JPA-based persistent repository.
 *
 * @author Edmundo Andrade
 */
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
		sessionFactory.openSession().doWork(new Work() {
			public void execute(Connection connection) throws SQLException {
				RelationalSQL insert = RelationalSQL.insert(instance);
				for (RelationalSQLCommmand command : insert.toSQLScript()) {
					executeInsertCommand(command, connection);
				}
			}
		});
		return FormKey.from(instance);
	}

	public void delete(@Nonnull FormKey key) {
		sessionFactory.openSession().doWork(new Work() {
			public void execute(Connection connection) throws SQLException {
				RelationalSQL delete = RelationalSQL.delete(createType(), key);
				for (RelationalSQLCommmand command : delete.toSQLScript()) {
					executeDeleteCommand(command, connection);
				}
			}
		});
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
		INSTANCE instance = sessionFactory.openSession().doReturningWork(new ReturningWork<INSTANCE>() {
			public INSTANCE execute(Connection connection) throws SQLException {
				INSTANCE firstInstance = null;
				RelationalSQL query = RelationalSQL.select(createType().getContainedTypes()).where(createType(), key);
				for (RelationalSQLCommmand command : query.toSQLScript()) {
					for (INSTANCE instance : executeSelectCommand(command, connection)) {
						firstInstance = instance;
						break;
					}
				}
				return firstInstance;
			}
		});
		if (instance == null) {
			throw new SingularFormNotFoundException(key);
		}
		return instance;
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

	protected void executeInsertCommand(RelationalSQLCommmand command, Connection connection) throws SQLException {
		List<String> generatedColumns = RelationalSQL.tablePK(command.getInstance().getType());
		String sql = command.getCommand();
		for (Object parameterValue : command.getParameters())
			sql = sql.replaceFirst("\\?", toSqlConstant(parameterValue));
		System.out.println(sql);
		Statement statement = connection.createStatement();
		statement.executeUpdate(sql, generatedColumns.toArray(new String[generatedColumns.size()]));
		try (ResultSet rs = statement.getGeneratedKeys()) {
			if (rs.next()) {
				HashMap<String, Object> key = new HashMap<>();
				int keyIndex = 1;
				for (String keyColumn : generatedColumns) {
					int keyValue = rs.getInt(keyIndex);
					key.put(keyColumn, keyValue);
					System.out.print(" [" + keyColumn + ": " + keyValue + "]");
					keyIndex++;
				}
				System.out.println();
				FormKey.set(command.getInstance(), new FormKeyRelational(key));
			}
		}
	}

	protected void executeDeleteCommand(RelationalSQLCommmand command, Connection connection) throws SQLException {
		String sql = command.getCommand();
		System.out.println(sql);
		PreparedStatement statement = connection.prepareStatement(sql);
		int paramIndex = 1;
		for (Object parameterValue : command.getParameters()) {
			statement.setObject(paramIndex, parameterValue);
			System.out.print(" [" + parameterValue + "]");
			paramIndex++;
		}
		System.out.println();
		statement.executeUpdate();
	}

	@Nonnull
	protected List<INSTANCE> loadAllInternal(Long first, Long max) {
		List<INSTANCE> result = new ArrayList<>();
		sessionFactory.openSession().doWork(new Work() {
			public void execute(Connection connection) throws SQLException {
				RelationalSQL query = RelationalSQL.select(createType().getContainedTypes());
				for (RelationalSQLCommmand command : query.toSQLScript()) {
					result.addAll(executeSelectCommand(command, connection));
				}
			}
		});
		return result;
	}

	protected List<INSTANCE> executeSelectCommand(RelationalSQLCommmand command, Connection connection)
			throws SQLException {
		List<INSTANCE> result = new ArrayList<>();
		String sql = command.getCommand();
		System.out.println(sql);
		PreparedStatement statement = connection.prepareStatement(sql);
		int paramIndex = 1;
		for (Object parameterValue : command.getParameters()) {
			statement.setObject(paramIndex, parameterValue);
			System.out.print(" [" + parameterValue + "]");
			paramIndex++;
		}
		System.out.println();
		try (ResultSet rs = statement.executeQuery()) {
			while (rs.next()) {
				INSTANCE instance = createInstance();
				List<String> pk = RelationalSQL.tablePK(instance.getType());
				FormKey.set(instance, tupleKey(rs, pk));
				RelationalSQL.persistenceStrategy(instance.getType()).load(instance, tuple(rs, command, pk));
				result.add(instance);
			}
		}
		return result;
	}

	protected FormKey tupleKey(ResultSet rs, List<String> pk) throws SQLException {
		HashMap<String, Object> key = new HashMap<>();
		for (String keyColumn : pk) {
			key.put(keyColumn, rs.getInt(keyColumn));
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

	protected String toSqlConstant(Object parameterValue) {
		if (parameterValue == null) {
			return "NULL";
		} else if (parameterValue instanceof String) {
			return "'" + ((String) parameterValue).replace("'", "''") + "'";
		}
		return parameterValue.toString();
	}
}
