package org.opensingular.form.persistence;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;

import javax.inject.Inject;

import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SInfoPackage;
import org.opensingular.form.SInfoType;
import org.opensingular.form.SInstance;
import org.opensingular.form.SPackage;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.STypeList;
import org.opensingular.form.TypeBuilder;
import org.opensingular.form.document.RefType;
import org.opensingular.form.document.SDocumentFactory;
import org.opensingular.form.persistence.FormPersistenceInRelationalDBHibernateTest.TestPackage.Form;
import org.opensingular.form.persistence.FormPersistenceInRelationalDBHibernateTest.TestPackage.Master;
import org.opensingular.form.type.core.STypeString;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Edmundo Andrade
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:relational/applicationContext.xml")
@Rollback
@Transactional
public class FormPersistenceInRelationalDBHibernateTest {
	@Inject
	protected SessionFactory sessionFactory;
	@Inject
	private SDocumentFactory documentFactory;
	@Inject
	private RelationalDatabase db;
	private FormPersistenceInRelationalDB<Form, SIComposite> repoForm;
	private FormPersistenceInRelationalDB<Master, SIComposite> repoMaster;

	@Before
	public void setUp() {
		repoForm = new FormPersistenceInRelationalDB<>(db, documentFactory, Form.class);
		repoMaster = new FormPersistenceInRelationalDB<>(db, documentFactory, Master.class);
	}

	@Test
	public void basicPersistenceWithGeneratedKey() {
		db.exec("CREATE TABLE FORM (CODE INT IDENTITY, NAME VARCHAR(200) NOT NULL, OBS CLOB, PRIMARY KEY (CODE))");
		//
		FormKey firtsKey = repoForm.insert(createFormInstance("My form"), null);
		assertEquals("CODE$Integer$1", firtsKey.toStringPersistence());
		assertEquals(1, repoForm.countAll());
		assertEquals(1, repoForm.loadAll().size());
		//
		Object code = ((FormKeyRelational) firtsKey).getColumnValue("CODE");
		assertEquals("My form", db.query("SELECT NAME FROM FORM WHERE CODE = ?", asList(code)).get(0)[0]);
		//
		SIComposite loaded = repoForm.load(firtsKey);
		assertEquals("My form", loaded.getValue("name"));
		assertNull(loaded.getValue("observation"));
		assertEquals(firtsKey, FormKey.fromInstance(loaded));
		//
		repoForm.insert(createFormInstance("Second form"), null);
		repoForm.insert(createFormInstance("Third form"), null);
		assertEquals(3, repoForm.countAll());
		List<SIComposite> page1 = repoForm.loadAll(0, 2);
		assertEquals(2, page1.size());
		assertEquals("CODE$Integer$1", FormKey.fromInstance(page1.get(0)).toStringPersistence());
		assertEquals("CODE$Integer$2", FormKey.fromInstance(page1.get(1)).toStringPersistence());
		List<SIComposite> page2 = repoForm.loadAll(2, 2);
		assertEquals(1, page2.size());
		assertEquals("CODE$Integer$3", FormKey.fromInstance(page2.get(0)).toStringPersistence());
		//
		repoForm.delete(firtsKey);
		assertEquals(2, repoForm.countAll());
		assertEquals(2, repoForm.loadAll().size());
	}

	private SIComposite createFormInstance(String name) {
		SIComposite formInstance = (SIComposite) documentFactory.createInstance(RefType.of(Form.class));
		formInstance.setValue("name", name);
		return formInstance;
	}

	@Test
	public void masterDetailPersistenceWithGeneratedKey() {
		db.exec("CREATE TABLE MASTER (ID INT IDENTITY, NAME VARCHAR(200) NOT NULL, PRIMARY KEY (ID))");
		db.exec("CREATE TABLE DETAIL (ID INT IDENTITY, MASTER INT NOT NULL, ITEM VARCHAR(80) NOT NULL, PRIMARY KEY (ID), FOREIGN KEY (MASTER) REFERENCES MASTER(ID))");
		//
		SIComposite master = (SIComposite) documentFactory.createInstance(RefType.of(Master.class));
		master.setValue("name", "Master X");
		addDetail("Item 1", master);
		addDetail("Item 2", master);
		addDetail("Item 3", master);
		FormKey insertedKey = repoMaster.insert(master, null);
		assertEquals("ID$Integer$1", insertedKey.toStringPersistence());
		assertEquals(1, repoMaster.countAll());
		assertEquals(1, repoMaster.loadAll().size());
		//
		Object code = ((FormKeyRelational) insertedKey).getColumnValue("ID");
		List<Object[]> tuples = db.query("SELECT ITEM FROM DETAIL WHERE MASTER = ?", asList(code));
		assertEquals(3, tuples.size());
		assertEquals("Item 1", tuples.get(0)[0]);
		assertEquals("Item 2", tuples.get(1)[0]);
		assertEquals("Item 3", tuples.get(2)[0]);
		//
		SIComposite loaded = repoMaster.load(insertedKey);
		assertEquals("Master X", loaded.getValue("name"));
		assertEquals(insertedKey, FormKey.fromInstance(loaded));
		List<SInstance> details = loaded.getValue("details");
		assertEquals(3, details.size());
		//
		repoMaster.delete(insertedKey);
		assertEquals(0, repoMaster.countAll());
		assertEquals(0, repoMaster.loadAll().size());
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
				name = addFieldString("name");
				observation = addFieldString("observation");
				// relational mapping
				asSQL().table("FORM").tablePK("CODE");
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
				name = addFieldString("name");
				details = addFieldListOf("details", Detail.class);
				// relational mapping
				asSQL().tablePK("ID");
			}
		}

		@SInfoType(name = "Detail", spackage = TestPackage.class)
		public static final class Detail extends STypeComposite<SIComposite> {
			public STypeString item;

			@Override
			protected void onLoadType(TypeBuilder tb) {
				item = addFieldString("item");
				asAtr().label("Detail entity");
				// relational mapping
				asSQL().tablePK("ID");
				asSQL().addTableFK("MASTER", Master.class);
			}
		}
	}
}
