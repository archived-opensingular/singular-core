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

package org.opensingular.form.persistence.relational.strategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.opensingular.form.SInstance;
import org.opensingular.form.SType;
import org.opensingular.form.persistence.FormKey;
import org.opensingular.form.persistence.relational.FormKeyRelational;
import org.opensingular.form.persistence.relational.RelationalData;
import org.opensingular.form.persistence.relational.RelationalSQL;

/**
 * Column-based strategy for saving/loading SInstance data.
 *
 * @author Edmundo Andrade
 */
public class PersistenceStrategyColumn implements PersistenceStrategy {
	public void save(SInstance instance, List<RelationalData> toList) {
		SType<?> field = instance.getType();
		String tableName = RelationalSQL.table(field);
		List<Object> tupleKey = tupleKey(instance);
		String fieldName = RelationalSQL.column(field);
		Object fieldValue = instance.getValue();
		toList.add(new RelationalData(tableName, tupleKey, fieldName, fieldValue));
	}

	public void load(SInstance instance, List<RelationalData> fromList) {
		SType<?> field = instance.getType();
		String tableName = RelationalSQL.table(field);
		List<Object> tupleKey = tupleKey(instance);
		String fieldName = RelationalSQL.column(field);
		for (RelationalData data : fromList) {
			if (data.getTableName().equals(tableName) && data.getTupleKey().equals(tupleKey)
					&& data.getFieldName().equals(fieldName)) {
				instance.setValue(data.getFieldValue());
			}
		}
	}

	protected List<Object> tupleKey(SInstance instance) {
		List<Object> result = new ArrayList<>();
		HashMap<String, Object> map = ((FormKeyRelational) FormKey.from(instance)).getValue();
		List<String> pk = RelationalSQL.tablePK(instance.getType());
		pk.stream().forEach(keyColumn -> result.add(map.get(keyColumn)));
		return result;
	}
}
