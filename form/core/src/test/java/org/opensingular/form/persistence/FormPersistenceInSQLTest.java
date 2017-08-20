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
import org.opensingular.form.SInfoPackage;
import org.opensingular.form.SInfoType;
import org.opensingular.form.SInstance;
import org.opensingular.form.SPackage;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.TestCaseForm;
import org.opensingular.form.TypeBuilder;
import org.opensingular.form.aspect.AspectRef;
import org.opensingular.form.persistence.FormPersistenceInSQLTest.TestPackage.TestEntityA;
import org.opensingular.form.persistence.relational.RelationalData;
import org.opensingular.form.type.core.STypeString;

/**
 * @author Edmundo Andrade on 17/08/2017.
 */
@RunWith(Parameterized.class)
public class FormPersistenceInSQLTest extends TestCaseForm {
	public FormPersistenceInSQLTest(TestFormConfig testFormConfig) {
		super(testFormConfig);
	}

	@Test
	public void select() {
		SDictionary dictionary = createTestDictionary();
		TestEntityA entityTypeA = dictionary.getType(TestEntityA.class);
		SIComposite entityInstanceA = entityTypeA.newInstance();
		assertEquals("select name from testPackage.TestEntityA", sqlSelectList(entityInstanceA)[0]);
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

	public static final AspectRef<RelationalMapper> ASPECT_RELATIONAL_MAP = new AspectRef<>(RelationalMapper.class);

	public interface RelationalMapper {
		List<RelationalData> toRelational(SInstance fieldInstance);
	}

	public static class BasicRelationalMapper implements RelationalMapper {
		public List<RelationalData> toRelational(SInstance fieldInstance) {
			List<RelationalData> list = new ArrayList<>();
			list.add(new RelationalData(fieldInstance.getParent().getName(), null, fieldInstance.getName(), null));
			return list;
		}
	}

	@SInfoPackage(name = "testPackage")
	public static final class TestPackage extends SPackage {
		@Override
		protected void onLoadPackage(PackageBuilder pb) {
			pb.createType(TestEntityA.class);
		}

		@SInfoType(name = "TestEntityA", spackage = TestPackage.class)
		public static final class TestEntityA extends STypeComposite<SIComposite> {
			public STypeString name;

			@Override
			protected void onLoadType(TypeBuilder tb) {
				asAtr().required(true);
				asAtr().label("Entity A");
				name = addFieldString("name");
				name.setAspect(ASPECT_RELATIONAL_MAP, BasicRelationalMapper::new);
			}
		}
	}
}