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

import org.opensingular.form.SAttributeEnabled;
import org.opensingular.form.STranslatorForAttribute;
import org.opensingular.form.persistence.SPackageFormPersistence;

/**
 * Decorates an Instance to enable persistence configuration.
 *
 * @author Edmundo Andrade
 */
public class AtrRelational extends STranslatorForAttribute {
	public AtrRelational() {
	}

	public AtrRelational(SAttributeEnabled target) {
		super(target);
	}

	public AtrRelational table(String table) {
		setAttributeValue(SPackageFormPersistence.ATR_TABLE, table);
		return this;
	}

	public String getTable() {
		return getAttributeValue(SPackageFormPersistence.ATR_TABLE);
	}

	public AtrRelational tablePK(String tablePK) {
		setAttributeValue(SPackageFormPersistence.ATR_TABLE_PK, tablePK);
		return this;
	}

	public String getTablePK() {
		return getAttributeValue(SPackageFormPersistence.ATR_TABLE_PK);
	}

	public AtrRelational references(String references) {
		setAttributeValue(SPackageFormPersistence.ATR_REFERENCES, references);
		return this;
	}

	public String getReferences() {
		return getAttributeValue(SPackageFormPersistence.ATR_REFERENCES);
	}

	public AtrRelational column(String column) {
		setAttributeValue(SPackageFormPersistence.ATR_COLUMN, column);
		return this;
	}

	public String getColumn() {
		return getAttributeValue(SPackageFormPersistence.ATR_COLUMN);
	}
}
