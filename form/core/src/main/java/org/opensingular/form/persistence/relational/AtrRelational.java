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

import static org.opensingular.form.persistence.SPackageFormPersistence.ATR_COLUMN;
import static org.opensingular.form.persistence.SPackageFormPersistence.ATR_TABLE;
import static org.opensingular.form.persistence.SPackageFormPersistence.ATR_TABLE_FKS;
import static org.opensingular.form.persistence.SPackageFormPersistence.ATR_TABLE_PK;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import org.opensingular.form.SAttributeEnabled;
import org.opensingular.form.STranslatorForAttribute;

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
		setAttributeValue(ATR_TABLE, table);
		return this;
	}

	public String getTable() {
		return getAttributeValue(ATR_TABLE);
	}

	public AtrRelational tablePK(String tablePK) {
		setAttributeValue(ATR_TABLE_PK, tablePK);
		return this;
	}

	public String getTablePK() {
		return getAttributeValue(ATR_TABLE_PK);
	}

	public AtrRelational addTableFK(String keyColumns, String foreignTable, String foreignPK) {
		return addTableFK(new RelationalFK(keyColumns, foreignTable, foreignPK));
	}

	public AtrRelational addTableFK(RelationalFK fk) {
		List<RelationalFK> list = getTableFKs();
		list.add(fk);
		StringJoiner sj = new StringJoiner(";");
		list.forEach(item -> sj.add(item.toStringPersistence()));
		setAttributeValue(ATR_TABLE_FKS, sj.toString());
		return this;
	}

	public List<RelationalFK> getTableFKs() {
		List<RelationalFK> result = new ArrayList<>();
		String value = getAttributeValue(ATR_TABLE_FKS);
		if (value == null)
			return result;
		String[] items = value.split(";");
		for (String item : items)
			result.add(RelationalFK.fromStringPersistence(item));
		return result;
	}

	public AtrRelational column(String column) {
		setAttributeValue(ATR_COLUMN, column);
		return this;
	}

	public String getColumn() {
		return getAttributeValue(ATR_COLUMN);
	}
}
