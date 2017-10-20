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
import static org.opensingular.form.persistence.relational.RelationalSQL.selectCount;
import static org.opensingular.form.persistence.relational.RelationalSQL.selectDistinct;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.annotation.Nonnull;

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
import org.opensingular.form.SingularFormException;
import org.opensingular.form.TestCaseForm;
import org.opensingular.form.TypeBuilder;
import org.opensingular.form.document.SDocument;
import org.opensingular.form.persistence.FormKey;
import org.opensingular.form.persistence.FormKeyRelational;
import org.opensingular.form.persistence.relational.RelationalSQLTest.TestPackage.ItemEntity;
import org.opensingular.form.persistence.relational.RelationalSQLTest.TestPackage.MasterEntity;
import org.opensingular.form.persistence.relational.RelationalSQLTest.TestPackage.PartiallyMappedEntity;
import org.opensingular.form.type.core.STypeMonetary;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.type.core.attachment.STypeAttachment;
import org.opensingular.form.type.ref.STypeRef;
import org.opensingular.internal.lib.commons.test.SingularTestUtil;

/**
 * @author Edmundo Andrade
 */
@RunWith(Parameterized.class)
public class RelationalSQLTest extends TestCaseForm {
	private SDictionary dictionary;
	private MasterEntity master;

	public RelationalSQLTest(TestFormConfig testFormConfig) {
		super(testFormConfig);
	}

	@Before
	public void setUp() {
		dictionary = createTestDictionary();
		master = dictionary.getType(MasterEntity.class);
	}

	@Test
	public void selectOrdered() {
		RelationalSQL query = select(master.getFields()).orderBy(master.name);
		List<RelationalSQLCommmand> script = query.toSQLScript();
		assertEquals(1, script.size());
		assertEquals(
				"select T1.name, T1.category, T2.name, T1.obs, T1.file, T1.id from MasterEntity T1 left join Category T2 on T1.category = T2.id order by T1.name",
				script.get(0).getSQL());
		assertEquals(0, script.get(0).getParameters().size());
		assertNull(script.get(0).getInstance());
	}

	@Test
	public void selectByKey() {
		RelationalSQL query = select(master.getFields()).where(master, masterKey(42));
		List<RelationalSQLCommmand> script = query.toSQLScript();
		assertEquals(1, script.size());
		assertEquals(
				"select T1.name, T1.category, T2.name, T1.obs, T1.file, T1.id from MasterEntity T1 left join Category T2 on T1.category = T2.id where T1.id = ?",
				script.get(0).getSQL());
		assertEquals(1, script.get(0).getParameters().size());
		assertEquals(42, script.get(0).getParameters().get(0));
		assertNull(script.get(0).getInstance());
	}

	@Test
	public void selectWithAggregatorCount() {
		RelationalSQL query = selectCount(master);
		List<RelationalSQLCommmand> script = query.toSQLScript();
		assertEquals(1, script.size());
		assertEquals("select count(*) from MasterEntity T1", script.get(0).getSQL());
		assertEquals(0, script.get(0).getParameters().size());
		assertNull(script.get(0).getInstance());
	}

	@Test
	public void selectWithAggregatorDistinct() {
		RelationalSQL query = selectDistinct(Arrays.asList(master.name));
		List<RelationalSQLCommmand> script = query.toSQLScript();
		assertEquals(1, script.size());
		assertEquals("select distinct T1.name from MasterEntity T1", script.get(0).getSQL());
		assertEquals(0, script.get(0).getParameters().size());
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
	public void selectPartiallyMappedEntity() {
		PartiallyMappedEntity entity = dictionary.getType(PartiallyMappedEntity.class);
		SingularTestUtil.assertException(() -> select(entity.getFields()), SingularFormException.class,
				"Relational mapping should provide table name for the type 'testPackage.PartiallyMappedEntity'.", "");
	}

	@Test
	public void testInsert() {
		SIComposite masterInstance = master.newInstance();
		masterInstance.setValue(master.name, "My name");
		masterInstance.getField(master.category).setValue(master.category.key, 2);
		masterInstance.getField(master.category).setValue(master.category.display, "Category 2");
		RelationalSQL insert = insert(masterInstance);
		List<RelationalSQLCommmand> script = insert.toSQLScript();
		assertEquals(1, script.size());
		assertEquals("insert into MasterEntity (name, category) values (?, ?)", script.get(0).getSQL());
		assertEquals(2, script.get(0).getParameters().size());
		assertEquals("My name", script.get(0).getParameters().get(0));
		assertEquals(2, script.get(0).getParameters().get(1));
		assertEquals(masterInstance, script.get(0).getInstance());
		//
		FormKey.setOnInstance(masterInstance, masterKey(33));
		SIComposite itemInstance = addItem("My mnemo", masterInstance);
		RelationalSQL insertItem = insert(itemInstance);
		script = insertItem.toSQLScript();
		assertEquals("insert into Items (mnemo, masterID) values (?, ?)", script.get(0).getSQL());
		assertEquals(2, script.get(0).getParameters().size());
		assertEquals("My mnemo", script.get(0).getParameters().get(0));
		assertEquals(33, script.get(0).getParameters().get(1));
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
		masterInstance.setValue(master.name, "My name");
		FormKey.setOnInstance(masterInstance, masterKey(4242));
		RelationalSQL update = RelationalSQL.update(masterInstance);
		List<RelationalSQLCommmand> script = update.toSQLScript();
		assertEquals(1, script.size());
		assertEquals("update MasterEntity T1 set T1.name = ?, T1.category = ?, T1.obs = ?, T1.file = ? where T1.id = ?",
				script.get(0).getSQL());
		assertEquals(5, script.get(0).getParameters().size());
		assertEquals("My name", script.get(0).getParameters().get(0));
		assertNull(script.get(0).getParameters().get(1));
		assertNull(script.get(0).getParameters().get(2));
		assertNull(script.get(0).getParameters().get(3));
		assertEquals(4242, script.get(0).getParameters().get(4));
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
		HashMap<String, Object> key = new LinkedHashMap<>();
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
		@SInfoType(name = "CategoryEntity", spackage = TestPackage.class)
		public static final class CategoryEntity extends STypeComposite<SIComposite> {
			public STypeString name;

			@Override
			protected void onLoadType(TypeBuilder tb) {
				asAtr().label("Category entity");
				name = addFieldString("name");
				// relational mapping
				asSQL().table("Category").tablePK("id");
				name.asSQL().column();
			}
		}

		@SInfoType(name = "CategoryRef", spackage = TestPackage.class)
		public static class CategoryRef extends STypeRef<SIComposite> {
			@Override
			protected String getKeyValue(SIComposite instance) {
				return FormKeyRelational.columnValuefromInstance("id", instance).toString();
			}

			@Override
			protected String getDisplayValue(SIComposite instance) {
				return instance.getValue(CategoryEntity.class, c -> c.name);
			}

			@Override
			protected List<SIComposite> loadValues(SDocument document) {
				List<SIComposite> result = new ArrayList<>();
				for (int i = 1; i <= 3; i++) {
					SIComposite instance = document.getRoot().getDictionary().newInstance(CategoryEntity.class);
					instance.setValue("name", "Category " + i);
					FormKey.setOnInstance(instance, new FormKeyRelational("id$Integer$" + i));
					result.add(instance);
				}
				return result;
			}

			@Override
			protected void onLoadType(@Nonnull TypeBuilder tb) {
				super.onLoadType(tb);
				// relational mapping
				key.asSQL().column("category").columnConverter(IntegerConverter::new);
				display.asSQL().foreignColumn("name", "category", CategoryEntity.class);
			}
		}

		@SInfoType(name = "MasterEntity", spackage = TestPackage.class)
		public static final class MasterEntity extends STypeComposite<SIComposite> {
			public STypeString name;
			public CategoryRef category;
			public STypeString observation;
			public STypeAttachment file;
			public STypeList<ItemEntity, SIComposite> items;

			@Override
			protected void onLoadType(TypeBuilder tb) {
				asAtr().label("Master entity");
				name = addFieldString("name");
				category = addField("category", CategoryRef.class);
				observation = addFieldString("observation");
				file = addField("file", STypeAttachment.class);
				items = addFieldListOf("items", ItemEntity.class);
				// relational mapping
				asSQL().table().tablePK("id");
				asSQL().addTableFK("category", CategoryEntity.class);
				name.asSQL().column();
				observation.asSQL().column("obs");
				file.asSQL().column().columnConverter(BLOBConverter::new);
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
				mnemo = addFieldString("mnemo");
				description = addFieldString("description");
				price = addFieldMonetary("price");
				details = addFieldListOf("details", ItemDetailEntity.class);
				// relational mapping
				asSQL().table("Items").tablePK("masterID, mnemo");
				asSQL().addTableFK("masterID", MasterEntity.class);
				mnemo.asSQL().column();
				description.asSQL().column("desc");
				price.asSQL().column();
			}
		}

		@SInfoType(name = "ItemDetailEntity", spackage = TestPackage.class)
		public static final class ItemDetailEntity extends STypeComposite<SIComposite> {
			public STypeString title;

			@Override
			protected void onLoadType(TypeBuilder tb) {
				asAtr().label("Item Detail entity");
				title = addFieldString("title");
				// relational mapping
				asSQL().table().tablePK("id");
				asSQL().addTableFK("itemID", ItemEntity.class);
				title.asSQL().column();
			}
		}

		@SInfoType(name = "PartiallyMappedEntity", spackage = TestPackage.class)
		public static final class PartiallyMappedEntity extends STypeComposite<SIComposite> {
			public STypeString name;

			@Override
			protected void onLoadType(TypeBuilder tb) {
				asAtr().label("Category entity");
				name = addFieldString("name");
				// relational mapping
				name.asSQL().column();
			}
		}
	}
}