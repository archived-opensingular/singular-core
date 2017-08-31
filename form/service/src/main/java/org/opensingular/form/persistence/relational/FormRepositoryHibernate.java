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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;

import org.hibernate.SessionFactory;
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

	// TODO
	@Nonnull
	public FormKey insert(@Nonnull INSTANCE instance, Integer inclusionActor) {
		RelationalSQL insert = RelationalSQL.insert(instance);
		sessionFactory.openSession().doWork(new Work() {
			public void execute(Connection connection) throws SQLException {
				for (RelationalSQLCommmand sql : insert.toSQLScript()) {
					System.out.println(sql.getCommand());
					Statement statement = connection.createStatement();
					statement.executeUpdate(sql.getCommand());
					ResultSet rs = statement.getGeneratedKeys();
					if (rs.next())
						System.out.println("> inserted: #" + rs.getInt(1));
					rs.close();
				}
			}
		});
		return null;
	}

	// TODO
	public void delete(@Nonnull FormKey key) {
		RelationalSQL delete = RelationalSQL.delete(createType(), key);
		for (RelationalSQLCommmand sql : delete.toSQLScript())
			System.out.println(sql.getCommand());
	}

	@NotNull
	@SuppressWarnings("unchecked")
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
		RelationalSQL query = RelationalSQL.select(createType().getContainedTypes());
		for (RelationalSQLCommmand sql : query.toSQLScript())
			System.out.println(sql.getCommand());
		INSTANCE instance = createInstance();
		if (instance == null)
			throw new SingularFormNotFoundException(key);
		return instance;
	}

	@Nonnull
	public Optional<INSTANCE> loadOpt(@Nonnull FormKey key) {
		return null;
	}

	@Nonnull
	public List<INSTANCE> loadAll(long first, long max) {
		return null;
	}

	// TODO
	@Nonnull
	public List<INSTANCE> loadAll() {
		return null;
	}

	public long countAll() {
		return 0;
	}

	@SuppressWarnings("unchecked")
	public INSTANCE createInstance() {
		return (INSTANCE) documentFactory.createInstance(RefType.of(type));
	}
}
