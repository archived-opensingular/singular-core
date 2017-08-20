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

import static org.opensingular.form.persistence.relational.RelationalMapper.ASPECT_RELATIONAL_MAP;
import static org.opensingular.form.persistence.relational.RelationalQuery.select;

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
import org.opensingular.form.TestCaseForm;
import org.opensingular.form.TypeBuilder;
import org.opensingular.form.persistence.FormPersistenceInSQLTest.TestPackage.TestEntityA;
import org.opensingular.form.persistence.relational.BasicRelationalMapper;
import org.opensingular.form.persistence.relational.RelationalQuery;
import org.opensingular.form.type.core.STypeString;

/**
 * @author Edmundo Andrade
 */
@RunWith(Parameterized.class)
public class FormPersistenceInSQLTest extends TestCaseForm {
	private TestEntityA entityTypeA;

	public FormPersistenceInSQLTest(TestFormConfig testFormConfig) {
		super(testFormConfig);
	}

	@Before
	public void setUp() {
		SDictionary dictionary = createTestDictionary();
		entityTypeA = dictionary.getType(TestEntityA.class);
	}

	@Test
	public void query() {
		RelationalQuery query = select(entityTypeA.getFields()).orderBy(entityTypeA.name);
		assertEquals("select T1.name from testPackage.TestEntityA T1 order by T1.name", query.toSQL());
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