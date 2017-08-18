/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
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

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SDictionary;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SInstance;
import org.opensingular.form.SType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.TestCaseForm;
import org.opensingular.form.aspect.AspectRef;
import org.opensingular.form.persistence.relational.RelationalData;

/**
 * @author Edmundo Andrade on 17/08/2017.
 */
@RunWith(Parameterized.class)
public class FormPersistenceInSQLTest extends TestCaseForm {
	public final AspectRef<RelationalMapper> ASPECT_RELATIONAL_MAP = new AspectRef<>(RelationalMapper.class);

	public interface RelationalMapper {
		boolean isPrimaryKey(SType<SInstance> fieldType);

		List<RelationalData> toRelational(SInstance fieldInstance);
	}

	public class BasicRelationalMapper implements RelationalMapper {
		public boolean isPrimaryKey(SType<SInstance> fieldType) {
			return fieldType.getNameSimple().equalsIgnoreCase("id");
		}

		public List<RelationalData> toRelational(SInstance fieldInstance) {
			List<RelationalData> list = new ArrayList<>();
			list.add(new RelationalData(fieldInstance.getParent().getName(), null, fieldInstance.getName(), null));
			return list;
		}
	}

	public FormPersistenceInSQLTest(TestFormConfig testFormConfig) {
		super(testFormConfig);
	}

	@Test
	public void select() {
		STypeComposite<SIComposite> entityTypeA = createEntityTypeA();
		SIComposite entityInstanceA = entityTypeA.newInstance();
		assertEquals("select id, name from schema.entityA", sqlSelectList(entityInstanceA)[0]);
	}

	private STypeComposite<SIComposite> createEntityTypeA() {
		SDictionary dictionary = createTestDictionary();
		PackageBuilder pack = dictionary.createNewPackage("schema");
		STypeComposite<SIComposite> entityType = pack.createCompositeType("entityA");
		entityType.addFieldString("id").setAspect(ASPECT_RELATIONAL_MAP, BasicRelationalMapper::new);
		entityType.addFieldString("name").setAspect(ASPECT_RELATIONAL_MAP, BasicRelationalMapper::new);
		return entityType;
	}

	private String[] sqlSelectList(SIComposite entityInstance) {
		// FormKey key = FormKey.from(entityInstance);
		// List<RelationalTable> tableRefs = tableReferences(entityInstance);
		return new String[] { "select " + concatenateColumnNames(entityInstance.getAllFields(), ", ") + " from "
				+ tableName(entityInstance) };
	}

	private String concatenateColumnNames(List<SInstance> fields, String separator) {
		StringJoiner sj = new StringJoiner(separator);
		fields.forEach((field) -> sj.add(field.getName()));
		return sj.toString();
	}

	private String tableName(SIComposite entityInstance) {
		return entityInstance.getType().getName();
	}
}