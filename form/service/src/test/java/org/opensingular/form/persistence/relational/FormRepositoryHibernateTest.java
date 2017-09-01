package org.opensingular.form.persistence.relational;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import javax.inject.Inject;

import org.hibernate.SessionFactory;
import org.hibernate.jdbc.Work;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opensingular.form.SDictionary;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SInfoPackage;
import org.opensingular.form.SInfoType;
import org.opensingular.form.SPackage;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.TypeBuilder;
import org.opensingular.form.document.SDocumentFactory;
import org.opensingular.form.persistence.FormRespository;
import org.opensingular.form.persistence.relational.FormRepositoryHibernateTest.TestPackage.Form;
import org.opensingular.form.type.core.STypeString;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@Rollback(value = true)
public class FormRepositoryHibernateTest {
	@Inject
	protected SessionFactory sessionFactory;
	private Form form;
	private FormRespository<Form, SIComposite> formRepository;

	@Before
	public void setUp() {
		SDictionary dictionary = SDictionary.create();
		form = dictionary.getType(Form.class);
		SDocumentFactory documentFactory = SDocumentFactory.empty();
		formRepository = new FormRepositoryHibernate<>(sessionFactory, documentFactory, Form.class);
	}

	@Test
	public void insertAndDeleteForGeneratedKey() {
		sessionFactory.openSession().doWork(new Work() {
			public void execute(Connection connection) throws SQLException {
				String sql = "CREATE TABLE DBSINGULAR.FORM (CODE INT IDENTITY, NAME VARCHAR(200) NOT NULL, OBS VARCHAR(250), PRIMARY KEY (CODE))";
				System.out.println(sql);
				Statement statement = connection.createStatement();
				statement.executeUpdate(sql);
			}
		});
		//
		SIComposite formInstance = form.newInstance();
		formInstance.setValue("name", "My form");
		FormKeyRelational insertedKey = (FormKeyRelational) formRepository.insert(formInstance, null);
		int code = (int) insertedKey.getColumnValue("CODE");
		//
		sessionFactory.openSession().doWork(new Work() {
			public void execute(Connection connection) throws SQLException {
				String sql = "SELECT NAME, OBS FROM DBSINGULAR.FORM WHERE CODE = ?";
				PreparedStatement statement = connection.prepareStatement(sql);
				statement.setInt(1, code);
				try (ResultSet rs = statement.executeQuery()) {
					assertTrue(rs.next());
					assertEquals("My form", rs.getString("NAME"));
					assertNull(rs.getString("OBS"));
				}
			}
		});
		//
		formRepository.delete(insertedKey);
		//
		sessionFactory.openSession().doWork(new Work() {
			public void execute(Connection connection) throws SQLException {
				String sql = "SELECT COUNT(*) FROM DBSINGULAR.FORM WHERE CODE = ?";
				PreparedStatement statement = connection.prepareStatement(sql);
				statement.setInt(1, code);
				try (ResultSet rs = statement.executeQuery()) {
					assertTrue(rs.next());
					assertEquals(0, rs.getInt(1));
				}
			}
		});
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
				as(AtrRelational::new).table("DBSINGULAR.FORM").defineColumn("CODE", Types.INTEGER).tablePK("CODE");
				name = addFieldString("name");
				observation = addFieldString("observation");
				observation.as(AtrRelational::new).column("OBS");
			}
		}
	}
}
