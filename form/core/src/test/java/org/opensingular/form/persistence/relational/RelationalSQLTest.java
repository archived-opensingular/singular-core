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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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
	public void selectOrdered() {
		RelationalSQL query = select(master.getFields()).orderBy(master.name);
		List<RelationalSQLCommmand> script = query.toSQLScript();
		assertEquals(1, script.size());
		assertEquals("select T1.name, T1.obs, T1.id from MasterEntity T1 order by T1.name", script.get(0).getSQL());
		assertEquals(0, script.get(0).getParameters().size());
		assertNull(script.get(0).getInstance());
	}

	@Test
	public void selectByKey() {
		HashMap<String, Object> keyMap = new HashMap<>();
		keyMap.put("id", 42);
		RelationalSQL query = select(master.getFields()).where(master, new FormKeyRelational(keyMap));
		List<RelationalSQLCommmand> script = query.toSQLScript();
		assertEquals(1, script.size());
		assertEquals("select T1.name, T1.obs, T1.id from MasterEntity T1 where T1.id = ?", script.get(0).getSQL());
		assertEquals(1, script.get(0).getParameters().size());
		assertEquals(42, script.get(0).getParameters().get(0));
		assertNull(script.get(0).getInstance());
	}

	@Test
	public void joinSelect() {
		ItemEntity items = master.items.getElementsType();
		RelationalSQL query = select(Arrays.asList(master.name, master.observation), items.getFields())
				.orderBy(master.name, items.mnemo);
		List<RelationalSQLCommmand> script = query.toSQLScript();
		assertEquals(1, script.size());
		assertEquals(
				"select T2.name, T2.obs, T1.mnemo, T1.desc, T1.price, T2.id, T1.masterID from Items T1 left join MasterEntity T2 on T1.masterID = T2.id order by T2.name, T1.mnemo",
				script.get(0).getSQL());
		assertEquals(0, script.get(0).getParameters().size());
		assertNull(script.get(0).getInstance());
	}

	@Test
	public void joinSelectDetailByMasterKey() {
		ItemEntity items = master.items.getElementsType();
		RelationalSQL query = select(items.getFields()).where(master, masterKey(77));
		List<RelationalSQLCommmand> script = query.toSQLScript();
		assertEquals(1, script.size());
		assertEquals(
				"select T1.mnemo, T1.desc, T1.price, T1.masterID from Items T1 left join MasterEntity T2 on T1.masterID = T2.id where T2.id = ?",
				script.get(0).getSQL());
		assertEquals(1, script.get(0).getParameters().size());
		assertEquals(77, script.get(0).getParameters().get(0));
		assertNull(script.get(0).getInstance());
	}

	@Test
	public void testInsert() {
		SIComposite masterInstance = master.newInstance();
		masterInstance.setValue("name", "My name");
		RelationalSQL insert = insert(masterInstance);
		List<RelationalSQLCommmand> script = insert.toSQLScript();
		assertEquals(1, script.size());
		assertEquals("insert into MasterEntity (name) values (?)", script.get(0).getSQL());
		assertEquals(1, script.get(0).getParameters().size());
		assertEquals("My name", script.get(0).getParameters().get(0));
		assertEquals(masterInstance, script.get(0).getInstance());
		//
		FormKey.setOnInstance(masterInstance, masterKey(33));
		SIComposite itemInstance = addItem("My mnemo", masterInstance);
		RelationalSQL insertItem = insert(itemInstance);
		script = insertItem.toSQLScript();
		assertEquals("insert into Items (mnemo, masterID) values (?, ?)", script.get(0).getSQL());
		assertEquals(2, script.get(0).getParameters().size());
		assertEquals("My mnemo", script.get(0).getParameters().get(0));
		// assertEquals(33, script.get(0).getParameters().get(1));
		assertEquals(itemInstance, script.get(0).getInstance());
		//
		SIComposite itemDetailInstance = addItemDetail("My title", itemInstance);
		RelationalSQL insertItemDetail = insert(itemDetailInstance);
		script = insertItemDetail.toSQLScript();
		assertEquals("insert into ItemDetailEntity (title) values (?)", script.get(0).getSQL());
		assertEquals(1, script.get(0).getParameters().size());
		assertEquals("My title", script.get(0).getParameters().get(0));
		assertEquals(itemDetailInstance, script.get(0).getInstance());
	}

	@Test
	public void testUpdate() {
		SIComposite masterInstance = master.newInstance();
		masterInstance.setValue("name", "My name");
		FormKey.setOnInstance(masterInstance, masterKey(4242));
		RelationalSQL update = RelationalSQL.update(masterInstance);
		List<RelationalSQLCommmand> script = update.toSQLScript();
		assertEquals(1, script.size());
		assertEquals("update MasterEntity T1 set T1.name = ?, T1.obs = ? where T1.id = ?", script.get(0).getSQL());
		assertEquals(3, script.get(0).getParameters().size());
		assertEquals("My name", script.get(0).getParameters().get(0));
		assertNull(script.get(0).getParameters().get(1));
		assertEquals(4242, script.get(0).getParameters().get(2));
		assertEquals(masterInstance, script.get(0).getInstance());
	}

	@Test
	public void testDelete() {
		RelationalSQL delete = delete(master, masterKey(42));
		List<RelationalSQLCommmand> script = delete.toSQLScript();
		assertEquals(1, script.size());
		assertEquals("delete from MasterEntity T1 where T1.id = ?", script.get(0).getSQL());
		assertEquals(1, script.get(0).getParameters().size());
		assertEquals(42, script.get(0).getParameters().get(0));
		assertNull(script.get(0).getInstance());
	}

	private FormKey masterKey(int id) {
		HashMap<String, Object> key = new HashMap<>();
		key.put("id", id);
		return new FormKeyRelational(key);
	}

	private SIComposite addItem(String mnemo, SIComposite master) {
		return master.getFieldList("items", SIComposite.class).addNew(instance -> instance.setValue("mnemo", mnemo));
	}

	private SIComposite addItemDetail(String title, SIComposite item) {
		return item.getFieldList("details", SIComposite.class).addNew(instance -> instance.setValue("title", title));
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
				asSQL().defineColumn("id", Types.INTEGER).tablePK("id");
				name = addFieldString("name");
				observation = addFieldString("observation");
				observation.asSQL().column("obs");
				items = addFieldListOf("items", ItemEntity.class);
			}
		}

		@SInfoType(name = "ItemEntity", spackage = TestPackage.class)
		public static final class ItemEntity extends STypeComposite<SIComposite> {
			public STypeString mnemo;
			public STypeString description;
			public STypeMonetary price;
			public STypeList<ItemDetailEntity, SIComposite> details;

			@Override
			protected void onLoadType(TypeBuilder tb) {
				asAtr().label("Item entity");
				asSQL().table("Items").tablePK("masterID, mnemo").addTableFK("masterID", MasterEntity.class);
				mnemo = addFieldString("mnemo");
				description = addFieldString("description");
				description.asSQL().column("desc");
				price = addFieldMonetary("price");
				details = addFieldListOf("details", ItemDetailEntity.class);
			}
		}

		@SInfoType(name = "ItemDetailEntity", spackage = TestPackage.class)
		public static final class ItemDetailEntity extends STypeComposite<SIComposite> {
			public STypeString title;

			@Override
			protected void onLoadType(TypeBuilder tb) {
				asAtr().label("Item Detail entity");
				asSQL().tablePK("id").addTableFK("itemID", ItemEntity.class);
				title = addFieldString("title");
			}
		}
	}
}