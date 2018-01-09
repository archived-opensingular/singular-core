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
import org.opensingular.form.persistence.relational.strategy.PersistenceStrategy;

/**
 * Basic implementation of a Relational Mapper.
 *
 * @author Edmundo Andrade
 */
public class BasicRelationalMapper implements RelationalMapper {
	public SType<?> tableContext(SType<?> type) {
		SType<?> result = type;
		while (table(result) == null && !result.getParentScope().equals(result.getPackage())) {
			result = result.getDictionary().getType(result.getParentScope().getName());
		}
		return result;
	}

	public String table(SType<?> type) {
		return type.asSQL().getTable();
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

	public String column(SType<?> field) {
		return field.asSQL().getColumn();
	}

	public RelationalForeignColumn foreignColumn(SType<?> field) {
		return field.asSQL().getForeignColumn();
	}

	public PersistenceStrategy persistenceStrategy(SType<?> field) {
		PersistenceStrategy result = PersistenceStrategy.COLUMN;
		if (column(field) == null && foreignColumn(field) == null) {
			if (table(field) != null || RelationalSQL.isListWithTableBound(field)) {
				result = PersistenceStrategy.TABLE;
			}
		}
		return result;
	}
}
