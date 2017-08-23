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

import static org.opensingular.form.persistence.relational.RelationalSQL.insert;
import static org.opensingular.form.persistence.relational.RelationalSQL.select;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SDictionary;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SInfoPackage;
import org.opensingular.form.SInfoType;
import org.opensingular.form.SPackage;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.STypeList;
import org.opensingular.form.TestCaseForm;
import org.opensingular.form.TypeBuilder;
import org.opensingular.form.persistence.RelationalSQLTest.TestPackage.TestEntityA;
import org.opensingular.form.persistence.relational.AtrRelational;
import org.opensingular.form.persistence.relational.RelationalSQL;
import org.opensingular.form.type.core.STypeMonetary;
import org.opensingular.form.type.core.STypeString;

/**
 * @author Edmundo Andrade
 */
@RunWith(Parameterized.class)
public class RelationalSQLTest extends TestCaseForm {
	private TestEntityA entityTypeA;

	public RelationalSQLTest(TestFormConfig testFormConfig) {
		super(testFormConfig);
	}

	@Before
	public void setUp() {
		SDictionary dictionary = createTestDictionary();
		entityTypeA = dictionary.getType(TestEntityA.class);
	}

	@Test
	public void testSelect() {
		RelationalSQL query = select(entityTypeA.getFields()).orderBy(entityTypeA.name);
		assertEquals("select T1.id, T1.name, T1.obs from testPackage.TestEntityA T1 order by T1.name",
				query.toSQLScript()[0]);
	}

	@Test
	public void testInsert() {
		SIComposite entityA = entityTypeA.newInstance();
		entityA.setValue("name", "MyName");
		RelationalSQL insert = insert(entityA);
		assertEquals("insert into testPackage.TestEntityA (id, name) values (?, ?)", insert.toSQLScript()[0]);
	}

	@Test
	public void testDelete() {
		SIComposite entityA = entityTypeA.newInstance();
		entityA.setValue("name", "MyName");
		RelationalSQL delete = RelationalSQL.delete(entityA);
		assertEquals("delete from testPackage.TestEntityA where id = ?", delete.toSQLScript()[0]);
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
			public STypeString observation;
			public STypeList<TestEntityB, SIComposite> itemsB;

			@Override
			protected void onLoadType(TypeBuilder tb) {
				asAtr().required(true);
				asAtr().label("Entity A");
				as(AtrRelational::new).tablePK("id");
				name = addFieldString("name");
				observation = addFieldString("observation");
				observation.as(AtrRelational::new).column("obs");
				itemsB = addFieldListOf("itemsB", TestEntityB.class);
			}
		}

		@SInfoType(name = "TestEntityB", spackage = TestPackage.class)
		public static final class TestEntityB extends STypeComposite<SIComposite> {
			public STypeString mnemo;
			public STypeString description;
			public STypeMonetary price;

			@Override
			protected void onLoadType(TypeBuilder tb) {
				asAtr().required(true);
				asAtr().label("Entity B");
				as(AtrRelational::new).tablePK("entityA, mnemo");
				mnemo = addFieldString("mnemo");
				description = addFieldString("description");
				description.as(AtrRelational::new).column("desc");
				price = addFieldMonetary("price");
			}
		}
	}
}