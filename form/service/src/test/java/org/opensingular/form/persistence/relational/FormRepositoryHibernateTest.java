package org.opensingular.form.persistence.relational;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.sql.Types;
import java.util.List;

import javax.inject.Inject;

import org.hibernate.SessionFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SInfoPackage;
import org.opensingular.form.SInfoType;
import org.opensingular.form.SPackage;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.STypeList;
import org.opensingular.form.TypeBuilder;
import org.opensingular.form.document.RefType;
import org.opensingular.form.document.SDocumentFactory;
import org.opensingular.form.persistence.FormKey;
import org.opensingular.form.persistence.relational.FormRepositoryHibernateTest.TestPackage.Form;
import org.opensingular.form.persistence.relational.FormRepositoryHibernateTest.TestPackage.Master;
import org.opensingular.form.type.core.STypeString;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:relational/applicationContext.xml")
@Rollback
@Transactional
public class FormRepositoryHibernateTest {
	@Inject
	protected SessionFactory sessionFactory;
	@Inject
	private SDocumentFactory documentFactory;

	@Test
	public void basicPersistenceWithGeneratedKey() {
		FormRepositoryHibernate<Form, SIComposite> db = new FormRepositoryHibernate<>(sessionFactory, documentFactory,
				Form.class);
		db.exec("CREATE TABLE FORM (CODE INT IDENTITY, NAME VARCHAR(200) NOT NULL, OBS CLOB, PRIMARY KEY (CODE))");
		//
		SIComposite formInstance = (SIComposite) documentFactory.createInstance(RefType.of(Form.class));
		formInstance.setValue("name", "My form");
		FormKey insertedKey = db.insert(formInstance, null);
		//
		Object code = ((FormKeyRelational) insertedKey).getColumnValue("CODE");
		assertEquals("My form", db.query("SELECT NAME FROM FORM WHERE CODE = ?", asList(code)).get(0)[0]);
		//
		SIComposite loaded = db.load(insertedKey);
		assertEquals("My form", loaded.getValue("name"));
		assertNull(loaded.getValue("observation"));
		assertEquals(insertedKey, FormKey.from(loaded));
		//
		assertEquals(1, db.loadAll().size());
		//
		db.delete(insertedKey);
		//
		assertEquals(0, db.loadAll().size());
	}

	@Test
	public void masterDetailPersistenceWithGeneratedKey() {
		FormRepositoryHibernate<Master, SIComposite> db = new FormRepositoryHibernate<>(sessionFactory, documentFactory,
				Master.class);
		db.exec("CREATE TABLE MASTER (ID INT IDENTITY, NAME VARCHAR(200) NOT NULL, PRIMARY KEY (ID))");
		db.exec("CREATE TABLE DETAIL (ID INT IDENTITY, MASTER INT NOT NULL, ITEM VARCHAR(80) NOT NULL, PRIMARY KEY (ID), FOREIGN KEY (MASTER) REFERENCES MASTER(ID))");
		//
		SIComposite master = (SIComposite) documentFactory.createInstance(RefType.of(Master.class));
		master.setValue("name", "Master X");
		addDetail("Item 1", master);
		addDetail("Item 2", master);
		addDetail("Item 3", master);
		FormKey insertedKey = db.insert(master, null);
		assertEquals("{ID=1}", insertedKey.toStringPersistence());
		//
		Object code = ((FormKeyRelational) insertedKey).getColumnValue("ID");
		List<Object[]> tuples = db.query("SELECT ITEM FROM DETAIL WHERE MASTER = ?", asList(code));
		assertEquals(3, tuples.size());
		assertEquals("Item 1", tuples.get(0)[0]);
		assertEquals("Item 2", tuples.get(1)[0]);
		assertEquals("Item 3", tuples.get(2)[0]);
		//
		SIComposite loaded = db.load(insertedKey);
		assertEquals("Master X", loaded.getValue("name"));
		assertEquals(insertedKey, FormKey.from(loaded));
		// List<SInstance> details = loaded.getValue("details");
		// assertEquals(3, details.size());
		//
		assertEquals(1, db.loadAll().size());
		//
		// db.delete(insertedKey);
		//
		// assertEquals(0, db.loadAll().size());
	}

	private SIComposite addDetail(String item, SIComposite master) {
		return master.getFieldList("details", SIComposite.class).addNew(instance -> instance.setValue("item", item));
	}

	@SInfoPackage(name = "testPackage")
	public static final class TestPackage extends SPackage {
		@SInfoType(name = "Form", spackage = TestPackage.class)
		public static final class Form extends STypeComposite<SIComposite> {
			public STypeString name;
			public STypeString observation;

			@Override
			protected void onLoadType(TypeBuilder tb) {
				asAtr().label("Formulary");
				asSQL().table("FORM").defineColumn("CODE", Types.INTEGER).tablePK("CODE");
				name = addFieldString("name");
				observation = addFieldString("observation");
				observation.asSQL().column("OBS");
			}
		}

		@SInfoType(name = "Master", spackage = TestPackage.class)
		public static final class Master extends STypeComposite<SIComposite> {
			public STypeString name;
			public STypeList<Detail, SIComposite> details;

			@Override
			protected void onLoadType(TypeBuilder tb) {
				asAtr().label("Master entity");
				asSQL().defineColumn("ID", Types.INTEGER).tablePK("ID");
				name = addFieldString("name");
				details = addFieldListOf("details", Detail.class);
			}
		}

		@SInfoType(name = "Detail", spackage = TestPackage.class)
		public static final class Detail extends STypeComposite<SIComposite> {
			public STypeString item;

			@Override
			protected void onLoadType(TypeBuilder tb) {
				item = addFieldString("item");
				asAtr().label("Detail entity");
				asSQL().defineColumn("ID", Types.INTEGER).tablePK("ID");
				asSQL().addTableFK("MASTER", Master.class);
			}
		}
	}
}
