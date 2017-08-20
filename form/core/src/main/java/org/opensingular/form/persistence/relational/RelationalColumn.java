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

/**
 * Relational metadata for identifying a specific column in a Relational DBMS.
 *
 * @author Edmundo Andrade
 */
public class RelationalColumn {
	private String table;
	private String name;

	public RelationalColumn(String table, String name) {
		this.table = table;
		this.name = name;
	}

	public String getTable() {
		return table;
	}

	public String getName() {
		return name;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof RelationalColumn)
			return ((RelationalColumn) obj).getTable().equals(getTable())
					&& ((RelationalColumn) obj).getName().equals(getName());
		return super.equals(obj);
	}
}
