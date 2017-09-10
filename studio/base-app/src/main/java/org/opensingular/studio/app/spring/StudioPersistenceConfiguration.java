package org.opensingular.studio.app.spring;

import java.util.Properties;

import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.opensingular.form.persistence.relational.RelationalDatabase;
import org.opensingular.form.persistence.service.RelationalDatabaseHibernate;
import org.opensingular.lib.commons.base.SingularException;
import org.opensingular.lib.commons.util.Loggable;
import org.springframework.context.annotation.Bean;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@EnableTransactionManagement(proxyTargetClass = true)
public class StudioPersistenceConfiguration implements Loggable {

    @Bean
    public DataSource dataSource() {
        try {
            HikariConfig hc = new HikariConfig();
            hc.setUsername("sa");
            hc.setPassword("sa");
            hc.setDriverClassName("org.h2.Driver");
            hc.setJdbcUrl(getUrlConnection());
            return new HikariDataSource(hc);//NOSONAR;
        } catch (Exception e) {
            throw SingularException.rethrow(e.getMessage(), e);
        }
    }

    @Bean
    public LocalSessionFactoryBean sessionFactory(final DataSource dataSource) {
        LocalSessionFactoryBean sessionFactoryBean = new LocalSessionFactoryBean();
        sessionFactoryBean.setDataSource(dataSource);
        sessionFactoryBean.setHibernateProperties(hibernateProperties());
        sessionFactoryBean.setPackagesToScan(hibernatePackagesToScan());
        return sessionFactoryBean;
    }

    @Bean
    public RelationalDatabase relationalDatabase(final SessionFactory sessionFactory) {
        return new RelationalDatabaseHibernate(sessionFactory);
    }

    @Bean
    public HibernateTransactionManager transactionManager(final SessionFactory sessionFactory, final DataSource dataSource) {
        final HibernateTransactionManager tx = new HibernateTransactionManager(sessionFactory);
        tx.setDataSource(dataSource);
        return tx;
    }

    protected String getUrlConnection() {
        return "jdbc:h2:./singularstudiodb;AUTO_SERVER=TRUE;CACHE_SIZE=4096;EARLY_FILTER=1;MULTI_THREADED=1;LOCK_TIMEOUT=15000;";
    }

    protected Properties hibernateProperties() {
        return new Properties();
    }

    protected String[] hibernatePackagesToScan() {
        return new String[0];
    }
}
