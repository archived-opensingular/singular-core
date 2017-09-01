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

import org.opensingular.form.SInstance;
import org.opensingular.form.SType;

/**
 * Basic implementation of a Relational Mapper.
 *
 * @author Edmundo Andrade
 */
public class BasicRelationalMapper implements RelationalMapper {
	public String table(SType<?> type) {
		String result = type.as(AtrSQL::new).getTable();
		if (result == null) {
			if (hasParentType(type)) {
				SType<?> parentType = getParentType(type);
				result = parentType.as(AtrSQL::new).getTable();
				if (result == null)
					result = parentType.getNameSimple();
			} else
				result = type.getNameSimple();
		}
		return result;
	}

	public List<String> tablePK(SType<?> type) {
		List<String> result = new ArrayList<>();
		String pk = type.as(AtrSQL::new).getTablePK();
		if (pk == null && hasParentType(type))
			pk = getParentType(type).as(AtrSQL::new).getTablePK();
		if (pk != null)
			for (String key : pk.split(","))
				result.add(key.trim());
		return result;
	}

	public List<RelationalFK> tableFKs(SType<?> field) {
		return field.as(AtrSQL::new).getTableFKs();
	}

	public String column(SType<?> field) {
		String result = field.as(AtrSQL::new).getColumn();
		if (result == null)
			result = field.getNameSimple();
		return result;
	}

	public List<RelationalData> data(SInstance fieldInstance) {
		SType<?> field = fieldInstance.getType();
		List<RelationalData> list = new ArrayList<>();
		list.add(new RelationalData(table(field), null, column(field), null));
		return list;
	}

	protected boolean hasParentType(SType<?> type) {
		return !type.getParentScope().equals(type.getPackage());
	}

	protected SType<?> getParentType(SType<?> type) {
		return type.getDictionary().getType(type.getParentScope().getName());
	}
}
