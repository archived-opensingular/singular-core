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

import java.util.ArrayList;
import java.util.List;

import org.opensingular.form.SType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.STypeList;
import org.opensingular.form.persistence.relational.strategy.PersistenceStrategy;
import org.opensingular.form.type.ref.STypeRef;

/**
 * Basic implementation of a Relational Mapper.
 *
 * @author Edmundo Andrade
 */
public class BasicRelationalMapper implements RelationalMapper {
	public SType<?> tableContext(SType<?> type) {
		SType<?> result = type;
		while (table(result) == null && hasParentType(result)) {
			result = getParentType(result);
		}
		return result;
	}

	public String table(SType<?> type) {
		String result = type.asSQL().getTable();
		if (result == null && !hasParentType(type)) {
			result = type.getNameSimple();
		}
		return result;
	}

	public List<String> tablePK(SType<?> type) {
		String pk = type.asSQL().getTablePK();
		if (pk == null) {
			return null;
		}
		List<String> result = new ArrayList<>();
		for (String key : pk.split(",")) {
			result.add(key.trim());
		}
		return result;
	}

	public List<RelationalFK> tableFKs(SType<?> field) {
		return field.asSQL().getTableFKs();
	}

	public String tableRefColumn(SType<?> field) {
		return field.asSQL().getTableRefColumn();
	}

	public String column(SType<?> field) {
		String result = field.asSQL().getColumn();
		if (result == null) {
			result = field.getNameSimple();
		}
		return result;
	}

	public PersistenceStrategy persistenceStrategy(SType<?> field) {
		PersistenceStrategy result = PersistenceStrategy.COLUMN;
		if (field instanceof STypeComposite && !(field instanceof STypeRef)) {
			result = PersistenceStrategy.TABLE;
		} else if (field instanceof STypeList) {
			result = PersistenceStrategy.ONE_TO_MANY;
		}
		return result;
	}

	protected boolean hasParentType(SType<?> type) {
		return type instanceof STypeRef || !type.isComposite() && !type.getParentScope().equals(type.getPackage());
	}

	protected SType<?> getParentType(SType<?> type) {
		return type.getDictionary().getType(type.getParentScope().getName());
	}
}
