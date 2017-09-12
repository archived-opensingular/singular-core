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

import static java.util.Collections.emptyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
import org.opensingular.form.TestCaseForm;
import org.opensingular.form.TypeBuilder;
import org.opensingular.form.persistence.FormPersistenceInRelationalDBTest.TestPackage.Form;
import org.opensingular.form.type.core.STypeString;

/**
 * @author Edmundo Andrade
 */
@RunWith(Parameterized.class)
public class FormPersistenceInRelationalDBTest extends TestCaseForm {
	private RelationalDatabase db;
	private FormRespository<Form, SIComposite> repo;
	private Form form;

	public FormPersistenceInRelationalDBTest(TestFormConfig testFormConfig) {
		super(testFormConfig);
	}

	@Before
	public void setUp() {
		db = mock(RelationalDatabase.class);
		repo = new FormPersistenceInRelationalDB<>(db, null, Form.class);
		SDictionary dictionary = createTestDictionary();
		form = dictionary.getType(Form.class);
	}

	@Test
	public void keyFromObject() {
		FormKey key = repo.keyFromObject("CODE$Integer$1");
		assertEquals("CODE$Integer$1", key.toStringPersistence());
		HashMap<String, Object> internalMap = ((FormKeyRelational) key).getValue();
		assertEquals(1, internalMap.size());
		assertEquals(1, internalMap.get("CODE"));
		assertEquals("CODE$Integer$1", repo.keyFromObject(internalMap).toStringPersistence());
	}

	@Test
	public void countAll() {
		when(db.query("select count(*) from FORM T1", emptyList())).thenReturn(querySingleResult(42L));
		assertEquals(42L, repo.countAll());
	}

	@Test
	public void update() {
		SIComposite formInstance = form.newInstance();
		formInstance.setValue("name", "My form name");
		FormKey.setOnInstance(formInstance, formKey(4242));
		when(db.exec("update FORM T1 set T1.name = ? where T1.CODE = ?", Arrays.asList("My form name", 4242)))
				.thenReturn(1);
		repo.update(formInstance, null);
	}

	private List<Object[]> querySingleResult(Object value) {
		List<Object[]> result = new ArrayList<>();
		result.add(new Object[] { value });
		return result;
	}

	private FormKey formKey(int id) {
		HashMap<String, Object> key = new LinkedHashMap<>();
		key.put("CODE", id);
		return new FormKeyRelational(key);
	}

	@SInfoPackage(name = "testPackage")
	public static final class TestPackage extends SPackage {
		@SInfoType(name = "Form", spackage = TestPackage.class)
		public static final class Form extends STypeComposite<SIComposite> {
			public STypeString name;

			@Override
			protected void onLoadType(TypeBuilder tb) {
				asAtr().label("Formulary");
				name = addFieldString("name");
				// relational mapping
				asSQL().table("FORM").tablePK("CODE");
			}
		}
	}
}