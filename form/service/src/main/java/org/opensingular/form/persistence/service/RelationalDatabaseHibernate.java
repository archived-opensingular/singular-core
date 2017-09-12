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

package org.opensingular.form.persistence.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.hibernate.SessionFactory;
import org.opensingular.form.persistence.RelationalDatabase;
import org.opensingular.form.persistence.relational.RelationalTupleHandler;

/**
 * Hibernate-based interaction with a relational database manager.
 *
 * @author Edmundo Andrade
 */
@Transactional
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
			return prepareStatement(connection, sql, params, null, null).executeUpdate();
		});
	}

	public int execReturningGenerated(String sql, List<Object> params, List<String> generatedColumns,
			RelationalTupleHandler<?> tupleHandler) {
		if (generatedColumns.isEmpty()) {
			return exec(sql, params);
		}
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
		return query(sql, params, null, null);
	}

	public <T> List<T> query(String sql, List<Object> params, RelationalTupleHandler<T> tupleHandler) {
		return query(sql, params, null, null, tupleHandler);
	}

	public List<Object[]> query(String sql, List<Object> params, Long limitOffset, Long limitRows) {
		return query(sql, params, limitOffset, limitRows, rs -> {
			Object[] tuple = new Object[rs.getMetaData().getColumnCount()];
			for (int i = 0; i < tuple.length; i++) {
				tuple[i] = rs.getObject(i + 1);
			}
			return tuple;
		});
	}

	public <T> List<T> query(String sql, List<Object> params, Long limitOffset, Long limitRows,
			RelationalTupleHandler<T> tupleHandler) {
		return sessionFactory.getCurrentSession().doReturningWork(connection -> {
			List<T> result = new ArrayList<>();
			try (ResultSet rs = prepareStatement(connection, sql, params, limitOffset, limitRows).executeQuery()) {
				long rowMin = Optional.ofNullable(limitOffset).orElse(0L);
				long rowMax = rowMin - 1 + Optional.ofNullable(limitRows).orElse(Long.MAX_VALUE - rowMin);
				long row = 0;
				while (rs.next() && row <= rowMax) {
					if (row >= rowMin) {
						result.add(tupleHandler.tuple(rs));
					}
					row++;
				}
			}
			return result;
		});
	}

	private PreparedStatement prepareStatement(Connection connection, String sql, List<Object> params, Long limitOffset,
			Long limitRows) throws SQLException {
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
