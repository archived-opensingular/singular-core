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
import java.util.Collection;
import java.util.List;

import org.hibernate.SessionFactory;

/**
 * Hibernate-based interaction with relational database managers.
 *
 * @author Edmundo Andrade
 */
public class RelationalDatabaseHibernate implements RelationalDatabase {
	private SessionFactory sessionFactory;

	public RelationalDatabaseHibernate(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
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
			RelationalTupleHandler<?> tupleHandler) {
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

	public int execScript(Collection<? extends RelationalSQLCommmand> script) {
		int result = 0;
		for (RelationalSQLCommmand command : script) {
			result += exec(command.getCommand(), command.getParameters());
		}
		return result;
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

	public <T> List<T> query(String sql, List<Object> params, RelationalTupleHandler<T> tupleHandler) {
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

	private PreparedStatement prepareStatement(Connection connection, String sql, List<Object> params)
			throws SQLException {
		PreparedStatement statement = connection.prepareStatement(sql);
		for (int i = 0; i < params.size(); i++) {
			statement.setObject(i + 1, params.get(i));
		}
		return statement;
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
