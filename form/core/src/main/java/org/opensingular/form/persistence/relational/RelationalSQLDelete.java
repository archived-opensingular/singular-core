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

import org.opensingular.form.ICompositeInstance;
import org.opensingular.form.SInstance;

/**
 * Builder for SQL insertions on Relational DBMS.
 *
 * @author Edmundo Andrade
 */
public class RelationalSQLDelete implements RelationalSQL {
	private SInstance instance;
	private List<String> targetTables;
	private List<RelationalColumn> keyColumns;

	public RelationalSQLDelete(SInstance instance) {
		this.instance = instance;
		this.targetTables = new ArrayList<String>();
		this.keyColumns = new ArrayList<RelationalColumn>();
		if (instance instanceof ICompositeInstance)
			for (SInstance child : ((ICompositeInstance) instance).getChildren())
				RelationalSQL.collectKeyColumns(child.getType(), keyColumns, targetTables);
	}

	public SInstance getInstance() {
		return instance;
	}

	public String[] toSQLScript() {
		List<String> lines = new ArrayList<>();
		for (String table : targetTables)
			lines.add("delete from " + table + " where " + RelationalSQL.where(table, keyColumns));
		return lines.toArray(new String[lines.size()]);
	}
}
