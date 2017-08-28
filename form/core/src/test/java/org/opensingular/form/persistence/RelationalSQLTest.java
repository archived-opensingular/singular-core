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
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.opensingular.form.SDictionary;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SInfoPackage;
import org.opensingular.form.SInfoType;
import org.opensingular.form.SPackage;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.STypeList;
import org.opensingular.form.TestCaseForm;
import org.opensingular.form.TypeBuilder;
import org.opensingular.form.persistence.RelationalSQLTest.TestPackage.ItemEntity;
import org.opensingular.form.persistence.RelationalSQLTest.TestPackage.MasterEntity;
import org.opensingular.form.persistence.relational.AtrRelational;
import org.opensingular.form.persistence.relational.RelationalSQL;
import org.opensingular.form.type.core.STypeMonetary;
import org.opensingular.form.type.core.STypeString;

/**
 * @author Edmundo Andrade
 */
@RunWith(Parameterized.class)
public class RelationalSQLTest extends TestCaseForm {
	private MasterEntity master;

	public RelationalSQLTest(TestFormConfig testFormConfig) {
		super(testFormConfig);
	}

	@Before
	public void setUp() {
		SDictionary dictionary = createTestDictionary();
		master = dictionary.getType(MasterEntity.class);
	}

	@Test
	public void singleSelect() {
		RelationalSQL query = select(master.getFields()).orderBy(master.name);
		assertEquals("select T1.name, T1.obs, T1.id from MasterEntity T1 order by T1.name", query.toSQLScript()[0]);
	}

	@Test
	@Ignore
	public void joinSelect() {
		ItemEntity items = master.items.getElementsType();
		RelationalSQL query = select(master.getFields(), items.getFields()).orderBy(master.name, items.mnemo);
		assertEquals(
				"select T1.name, T1.obs, T2.mnemo, T2.desc, T2.price, T1.id, T2.masterID from MasterEntity T1 left join Items T2 on T2.masterID = T1.id order by T1.name, T2 mnemo",
				query.toSQLScript()[0]);
	}

	@Test
	public void testInsert() {
		SIComposite masterInstance = master.newInstance();
		masterInstance.setValue("name", "MyName");
		RelationalSQL insert = insert(masterInstance);
		assertEquals("insert into MasterEntity (id, name) values (?, ?)", insert.toSQLScript()[0]);
	}

	@Test
	public void testDelete() {
		SIComposite masterInstance = master.newInstance();
		masterInstance.setValue("name", "MyName");
		RelationalSQL delete = RelationalSQL.delete(masterInstance);
		assertEquals("delete from MasterEntity where id = ?", delete.toSQLScript()[0]);
	}

	@SInfoPackage(name = "testPackage")
	public static final class TestPackage extends SPackage {
		@SInfoType(name = "MasterEntity", spackage = TestPackage.class)
		public static final class MasterEntity extends STypeComposite<SIComposite> {
			public STypeString name;
			public STypeString observation;
			public STypeList<ItemEntity, SIComposite> items;

			@Override
			protected void onLoadType(TypeBuilder tb) {
				asAtr().label("Master entity");
				as(AtrRelational::new).tablePK("id");
				name = addFieldString("name");
				observation = addFieldString("observation");
				observation.as(AtrRelational::new).column("obs");
				items = addFieldListOf("items", ItemEntity.class);
			}
		}

		@SInfoType(name = "ItemEntity", spackage = TestPackage.class)
		public static final class ItemEntity extends STypeComposite<SIComposite> {
			public STypeString mnemo;
			public STypeString description;
			public STypeMonetary price;

			@Override
			protected void onLoadType(TypeBuilder tb) {
				asAtr().label("Item entity");
				as(AtrRelational::new).table("Items").tablePK("masterID, mnemo").addTableFK("masterID",
						MasterEntity.class);
				mnemo = addFieldString("mnemo");
				description = addFieldString("description");
				description.as(AtrRelational::new).column("desc");
				price = addFieldMonetary("price");
			}
		}
	}
}