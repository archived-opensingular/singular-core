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

package org.opensingular.form.persistence.relational;

import static org.opensingular.form.persistence.relational.RelationalSQL.delete;
import static org.opensingular.form.persistence.relational.RelationalSQL.insert;
import static org.opensingular.form.persistence.relational.RelationalSQL.select;

import java.sql.Types;
import java.util.HashMap;

import org.junit.Before;
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
import org.opensingular.form.persistence.FormKey;
import org.opensingular.form.persistence.relational.RelationalSQLTest.TestPackage.ItemEntity;
import org.opensingular.form.persistence.relational.RelationalSQLTest.TestPackage.MasterEntity;
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
		RelationalSQLCommmand[] script = query.toSQLScript();
		assertEquals(1, script.length);
		assertEquals("select T1.name, T1.obs, T1.id from MasterEntity T1 order by T1.name", script[0].getCommand());
		assertEquals(0, script[0].getParameters().size());
		assertNull(script[0].getInstance());
	}

	@Test
	public void joinSelect() {
		ItemEntity items = master.items.getElementsType();
		RelationalSQL query = select(master.getFields(), items.getFields()).orderBy(master.name, items.mnemo);
		RelationalSQLCommmand[] script = query.toSQLScript();
		assertEquals(1, script.length);
		assertEquals(
				"select T1.name, T1.obs, T2.mnemo, T2.desc, T2.price, T1.id, T2.masterID from MasterEntity T1 left join Items T2 on T2.masterID = T1.id order by T1.name, T2.mnemo",
				script[0].getCommand());
		assertEquals(0, script[0].getParameters().size());
		assertNull(script[0].getInstance());
	}

	@Test
	public void testInsert() {
		SIComposite masterInstance = master.newInstance();
		masterInstance.setValue("name", "My name");
		RelationalSQL insert = insert(masterInstance);
		RelationalSQLCommmand[] script = insert.toSQLScript();
		assertEquals(1, script.length);
		assertEquals("insert into MasterEntity (name) values (?)", script[0].getCommand());
		assertEquals(1, script[0].getParameters().size());
		assertEquals("My name", script[0].getParameters().get(0));
		assertEquals(masterInstance, script[0].getInstance());
	}

	@Test
	public void testDelete() {
		RelationalSQL delete = delete(master, masterKey(42));
		RelationalSQLCommmand[] script = delete.toSQLScript();
		assertEquals(1, script.length);
		assertEquals("delete from MasterEntity where id = ?", script[0].getCommand());
		assertEquals(1, script[0].getParameters().size());
		assertEquals(42, script[0].getParameters().get(0));
		assertNull(script[0].getInstance());
	}

	private FormKey masterKey(int id) {
		HashMap<String, Object> key = new HashMap<>();
		key.put("id", id);
		return new FormKeyRelational(key);
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
				as(AtrRelational::new).defineColumn("id", Types.INTEGER).tablePK("id");
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