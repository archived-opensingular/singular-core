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
import java.util.Map;

import org.opensingular.form.SType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.persistence.FormKey;
import org.opensingular.form.persistence.FormKeyRelational;

/**
 * Builder for SQL insertions on Relational DBMS.
 *
 * @author Edmundo Andrade
 */
public class RelationalSQLDelete extends RelationalSQL {
	private List<RelationalColumn> keyColumns;
	private Map<String, Object> mapColumnToValue;

	public RelationalSQLDelete(STypeComposite<?> type, FormKey formKey) {
		this.keyColumns = new ArrayList<>();
		for (SType<?> field : type.getFields()) {
			if (!field.isComposite()) {
				collectKeyColumns(field, keyColumns);
			}
		}
		mapColumnToValue = ((FormKeyRelational) formKey).getValue();
	}

	@Override
	public List<RelationalSQLCommmand> toSQLScript() {
		List<RelationalSQLCommmand> lines = new ArrayList<>();
		for (SType<?> tableContext : targetTables) {
			String tableName = RelationalSQL.table(tableContext);
			List<Object> params = new ArrayList<>();
			lines.add(new RelationalSQLCommmand("delete from " + tableName + " " + tableAlias(tableName) + " where "
					+ where(tableName, keyColumns, mapColumnToValue, params), params, null, null));
		}
		return lines;
	}
}
