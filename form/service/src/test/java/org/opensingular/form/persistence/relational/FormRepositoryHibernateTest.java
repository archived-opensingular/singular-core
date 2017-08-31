package org.opensingular.form.persistence.relational;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.inject.Inject;

import org.hibernate.SessionFactory;
import org.hibernate.jdbc.Work;
import org.junit.Test;
import org.junit.runner.RunWith;
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

	@Test
	public void insertAndSelect() {
		sessionFactory.openSession().doWork(new Work() {
			public void execute(Connection connection) throws SQLException {
				String sql = "INSERT INTO DBSINGULAR.TB_TIPO_FORMULARIO (CO_TIPO_FORMULARIO, SG_TIPO_FORMULARIO, NO_LABEL_FORMULARIO, NU_VERSAO_CACHE) VALUES (?, ?, ?, ?)";
				PreparedStatement statement = connection.prepareStatement(sql);
				statement.setInt(1, 1);
				statement.setString(2, "F1");
				statement.setString(3, "FORM 1");
				statement.setInt(4, 1);
				int rows = statement.executeUpdate();
				System.out.println("> " + rows + " inserted");
				//
				sql = "SELECT CO_TIPO_FORMULARIO, SG_TIPO_FORMULARIO, NO_LABEL_FORMULARIO, NU_VERSAO_CACHE FROM DBSINGULAR.TB_TIPO_FORMULARIO";
				statement = connection.prepareStatement(sql);
				ResultSet rs = statement.executeQuery();
				while (rs.next())
					System.out.println("selected: " + rs.getInt("CO_TIPO_FORMULARIO") + " | "
							+ rs.getString("SG_TIPO_FORMULARIO") + " | " + rs.getString("NO_LABEL_FORMULARIO") + " | "
							+ rs.getInt("NU_VERSAO_CACHE"));
				rs.close();
			}
		});
	}

	@Test
	public void insertWithGeneratedValue() {
		sessionFactory.openSession().doWork(new Work() {
			public void execute(Connection connection) throws SQLException {
				String sql = "CREATE TABLE DBSINGULAR.TEST (CODE INT IDENTITY, NAME VARCHAR(200) NOT NULL, PRIMARY KEY (CODE))";
				Statement statement = connection.createStatement();
				statement.executeUpdate(sql);
				System.out.println("> table created");
				//
				sql = "INSERT INTO DBSINGULAR.TEST (NAME) VALUES ('My name')";
				statement = connection.createStatement();
				statement.executeUpdate(sql, new String[] { "CODE" });
				ResultSet rs = statement.getGeneratedKeys();
				if (rs.next())
					System.out.println("> inserted: #" + rs.getInt(1));
				rs.close();
			}
		});
	}
}
