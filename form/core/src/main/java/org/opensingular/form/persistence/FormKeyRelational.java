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

package org.opensingular.form.persistence;

import java.util.HashMap;
import java.util.StringJoiner;

import javax.annotation.Nonnull;

/**
 * Generic form key for relational persistence.
 *
 * @author Edmundo Andrade
 */
public class FormKeyRelational extends AbstractFormKey<HashMap<String, Object>> {
	private static final long serialVersionUID = 1L;
	private static final String SERIALIZATION_SEPARATOR = "___";
	private static final String IMPLICIT_JAVA_PACKAGE = "java.lang.";

	public FormKeyRelational(@Nonnull String persistenceString) {
		super(persistenceString);
	}

	public FormKeyRelational(@Nonnull HashMap<String, Object> keyValue) {
		super(keyValue);
	}

	public Object getColumnValue(String column) {
		return getValue().get(column);
	}

	/**
	 * Method responsible to convert objects to a valid FormKeyRelational instance.
	 * 
	 * This method is intended to be called by {@link FormKeyManager}.
	 * 
	 * @param objectValueToBeConverted
	 *            non-null object to be converted
	 * @return a valid FormKeyRelational instance
	 */
	@SuppressWarnings("unchecked")
	public static FormKeyRelational convertToKey(Object objectValueToBeConverted) {
		if (objectValueToBeConverted == null) {
			throw new SingularFormPersistenceException("Null value cannot be converted to a valid FormKey.");
		} else if (objectValueToBeConverted instanceof FormKeyRelational) {
			return (FormKeyRelational) objectValueToBeConverted;
		} else if (objectValueToBeConverted instanceof HashMap<?, ?>) {
			return new FormKeyRelational((HashMap<String, Object>) objectValueToBeConverted);
		} else if (objectValueToBeConverted instanceof String) {
			return new FormKeyRelational((String) objectValueToBeConverted);
		}
		throw new SingularFormPersistenceException("The given value cannot be converted to a valid FormKey.")
				.add("value", objectValueToBeConverted).add("value type", objectValueToBeConverted.getClass());
	}

	@Override
	protected HashMap<String, Object> parseValuePersistenceString(String persistenceString) {
		HashMap<String, Object> result = new HashMap<>();
		String[] pairs = persistenceString.split(SERIALIZATION_SEPARATOR);
		for (String pair : pairs) {
			String[] parts = pair.split("\\$", 3);
			result.put(parts[0], columnValue(parts[1], parts[2]));
		}
		return result;
	}

	@Override
	public String toStringPersistence() {
		StringJoiner sj = new StringJoiner(SERIALIZATION_SEPARATOR);
		getValue().keySet().forEach(
				column -> sj.add(column + "$" + className(getValue().get(column)) + "$" + getValue().get(column)));
		return sj.toString();
	}

	private Object columnValue(String className, String value) {
		try {
			return Class.forName(resolveClassName(className)).getConstructor(String.class).newInstance(value);
		} catch (ReflectiveOperationException e) {
			throw new SingularFormPersistenceException("A column value defined in FormKey cannot be deserialized.")
					.add("className", className).add("value", value);
		}
	}

	private String className(Object value) {
		return value.getClass().getName().replace(IMPLICIT_JAVA_PACKAGE, "");
	}

	private String resolveClassName(String className) {
		return className.contains(".") ? className : IMPLICIT_JAVA_PACKAGE + className;
	}
}
