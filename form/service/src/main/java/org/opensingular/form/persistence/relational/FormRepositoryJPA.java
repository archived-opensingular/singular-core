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

import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.persistence.EntityManager;

import org.opensingular.form.ICompositeInstance;
import org.opensingular.form.ICompositeType;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SInstance;
import org.opensingular.form.SType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.persistence.FormKey;
import org.opensingular.form.persistence.FormRespository;
import org.opensingular.form.persistence.SingularFormNotFoundException;

/**
 * JPA-based persistent repository.
 *
 * @author Edmundo Andrade
 */
public class FormRepositoryJPA<TYPE extends SType<INSTANCE>, INSTANCE extends SInstance>
		implements FormRespository<TYPE, INSTANCE> {
	private EntityManager entityManager;
	private ICompositeType type;

	public FormRepositoryJPA(EntityManager entityManager, ICompositeType type) {
		this.entityManager = entityManager;
		this.type = type;
	}

	@Nonnull
	public FormKey keyFromObject(@Nonnull Object objectValueToBeConverted) {
		return null;
	}

	@Nonnull
	public FormKey insert(@Nonnull INSTANCE instance, Integer inclusionActor) {
		RelationalSQL insert = RelationalSQL.insert((ICompositeInstance) instance);
		for (String sql : insert.toSQLScript())
			System.out.println(sql);
		return null;
	}

	public void delete(@Nonnull FormKey key) {
		RelationalSQL delete = RelationalSQL.delete(type, key);
		for (String sql : delete.toSQLScript())
			System.out.println(sql);
	}

	public void update(@Nonnull INSTANCE instance, Integer inclusionActor) {
	}

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

	@SuppressWarnings("unchecked")
	@Nonnull
	public INSTANCE load(@Nonnull FormKey key) {
		RelationalSQL query = RelationalSQL.select(type.getContainedTypes());
		for (String sql : query.toSQLScript())
			System.out.println(sql);
		INSTANCE instance = (INSTANCE) ((STypeComposite<SIComposite>) type).newInstance();
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

	@Nonnull
	public List<INSTANCE> loadAll() {
		return null;
	}

	public long countAll() {
		return 0;
	}

	public INSTANCE createInstance() {
		return null;
	}
}
