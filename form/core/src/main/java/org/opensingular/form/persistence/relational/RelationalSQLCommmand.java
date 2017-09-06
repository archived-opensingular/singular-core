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

import org.opensingular.form.SIComposite;

/**
 * Relational SQL command, including its parameters.
 *
 * @author Edmundo Andrade
 */
public class RelationalSQLCommmand {
	private String command;
	private List<Object> parameters;
	private SIComposite instance;
	private List<RelationalColumn> columns;

	public RelationalSQLCommmand(String command, List<Object> parameters, SIComposite instance,
			List<RelationalColumn> columns) {
		this.command = command;
		this.parameters = parameters;
		this.instance = instance;
		this.columns = columns;
	}

	public String getCommand() {
		return command;
	}

	public List<Object> getParameters() {
		return parameters;
	}

	public SIComposite getInstance() {
		return instance;
	}

	public void setInstance(SIComposite instance) {
		this.instance = instance;
	}

	public List<RelationalColumn> getColumns() {
		return columns;
	}
}
