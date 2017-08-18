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
 * Relational table metadata to persist into a Relational DBMS.
 *
 * @author Edmundo Andrade
 */
public class RelationalTable {
	private String name;
	private List<SType<SInstance>> columns = new ArrayList<>();

	public RelationalTable(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public List<SType<SInstance>> getColumns() {
		return columns;
	}

	public void addColumn(SType<SInstance> columnType) {
		columns.add(columnType);
	}
}
