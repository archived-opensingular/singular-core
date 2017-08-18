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
 * Relational data to persist into a Relational DBMS.
 *
 * @author Edmundo Andrade on 17/08/2017.
 */
public class RelationalData {
	private String tableName;
	private Object[] tupleKey;
	private String fieldName;
	private Object fieldValue;

	public RelationalData(String tableName, Object[] tupleKey, String fieldName, Object fieldValue) {
		this.tableName = tableName;
		this.tupleKey = tupleKey;
		this.fieldName = fieldName;
		this.fieldValue = fieldValue;
	}

	public String getTableName() {
		return tableName;
	}

	public Object[] getTupleKey() {
		return tupleKey;
	}

	public String getFieldName() {
		return fieldName;
	}

	public Object getFieldValue() {
		return fieldValue;
	}
}
