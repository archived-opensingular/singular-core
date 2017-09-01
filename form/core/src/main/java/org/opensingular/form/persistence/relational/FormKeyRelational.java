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

import java.sql.Types;
import java.util.HashMap;

import javax.annotation.Nonnull;

import org.opensingular.form.persistence.AbstractFormKey;

/**
 * Generic form key for relational persistence.
 *
 * @author Edmundo Andrade
 */
public class FormKeyRelational extends AbstractFormKey<HashMap<String, Object>> {
	private static final long serialVersionUID = 1L;
	private static final String SERIALIZATION_SEPARATOR = "___";

	public FormKeyRelational(@Nonnull String persistenceString) {
		super(persistenceString);
	}

	public FormKeyRelational(@Nonnull HashMap<String, Object> keyValue) {
		super(keyValue);
	}

	public Object getColumnValue(String column) {
		return getValue().get(column);
	}

	@Override
	protected HashMap<String, Object> parseValuePersistenceString(String persistenceString) {
		HashMap<String, Object> result = new HashMap<>();
		String[] pairs = persistenceString.split(SERIALIZATION_SEPARATOR);
		for (String pair : pairs) {
			String[] parts = pair.split("\\$", 3);
			int sqlType = Integer.parseInt(parts[1]);
			if (sqlType == Types.INTEGER)
				result.put(parts[0], Integer.parseInt(parts[2]));
			else
				result.put(parts[0], parts[2]);
		}
		return result;
	}
}
