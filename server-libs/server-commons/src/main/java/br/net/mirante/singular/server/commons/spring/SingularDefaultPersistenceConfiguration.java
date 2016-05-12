package br.net.mirante.singular.server.commons.spring;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

@EnableTransactionManagement(proxyTargetClass = true)
public class SingularDefaultPersistenceConfiguration {

    @Value("classpath:db/ddl/create-function.sql")
    private Resource sqlCreateFunction;

    @Value("classpath:db/ddl/create-constraints.sql")
    private Resource sqlCreateConstraints;

    @Value("classpath:db/ddl/create-tables-flow.sql")
    private Resource sqlCreateTablesFlow;

    @Value("classpath:db/ddl/create-tables.sql")
    private Resource sqlCreateTables;

    @Value("classpath:db/ddl/create-tables-actor.sql")
    private Resource sqlCreateTablesActor;

    @Value("classpath:db/ddl/drops.sql")
    private Resource drops;

    @Value("classpath:db/dml/insert-flow-data.sql")
    private Resource insertDadosSingular;

    @Value("classpath:db/dml/insert-test-data.sql")
    private Resource insertTestData;

    protected DatabasePopulator databasePopulator() {
        final ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.setSqlScriptEncoding("UTF-8");
        populator.addScript(drops);
        populator.addScript(sqlCreateTables);
        populator.addScript(sqlCreateTablesActor);
        populator.addScript(sqlCreateTablesFlow);
        populator.addScript(sqlCreateConstraints);
        populator.addScript(insertDadosSingular);
        populator.addScript(insertTestData);
        return populator;
    }

    @Bean
    public DataSourceInitializer createFunctionInitializer(final DataSource dataSource) {
        final DataSourceInitializer initializer = new DataSourceInitializer();
        initializer.setDataSource(dataSource);
        final ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.setSqlScriptEncoding("UTF-8");
        populator.addScript(sqlCreateFunction);
        initializer.setDatabasePopulator(databasePopulator());
        return initializer;
    }

    @Bean
    public DataSourceInitializer scriptsInitializer(final DataSource dataSource) {
        final DataSourceInitializer initializer = new DataSourceInitializer();
        initializer.setDataSource(dataSource);
        initializer.setDatabasePopulator(databasePopulator());
        return initializer;
    }

    @Bean
    public DriverManagerDataSource dataSource() {
        final DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl("jdbc:h2:file:./singularserverdb;AUTO_SERVER=TRUE;mode=ORACLE;CACHE_SIZE=4096;MULTI_THREADED=1;EARLY_FILTER=1");
        dataSource.setUsername("sa");
        dataSource.setPassword("sa");
        final Properties connectionProperties = new Properties();
        connectionProperties.setProperty("removeAbandoned", "true");
        connectionProperties.setProperty("initialSize", "5");
        connectionProperties.setProperty("maxActive", "10");
        connectionProperties.setProperty("minIdle", "1");
        dataSource.setDriverClassName("org.h2.Driver");
        return dataSource;
    }


    @Bean
    public LocalSessionFactoryBean sessionFactory(final DataSource dataSource) {
        final LocalSessionFactoryBean sessionFactoryBean = new LocalSessionFactoryBean();
        sessionFactoryBean.setDataSource(dataSource);
        sessionFactoryBean.setHibernateProperties(hibernateProperties());
        sessionFactoryBean.setPackagesToScan("br.net.mirante.singular");
        return sessionFactoryBean;
    }

    @Bean
    public HibernateTransactionManager transactionManager(final SessionFactory sessionFactory, final DataSource dataSource) {
        final HibernateTransactionManager tx = new HibernateTransactionManager(sessionFactory);
        tx.setDataSource(dataSource);
        return tx;
    }


    private Properties hibernateProperties() {
        final Properties hibernateProperties = new Properties();
        hibernateProperties.put("hibernate.dialect", "org.hibernate.dialect.Oracle10gDialect");
        hibernateProperties.put("hibernate.connection.isolation", "2");
        hibernateProperties.put("hibernate.jdbc.batch_size", "30");
        hibernateProperties.put("hibernate.show_sql", "true");
        hibernateProperties.put("hibernate.format_sql", "true");
        hibernateProperties.put("hibernate.cache.use_second_level_cache", "false");
        hibernateProperties.put("hibernate.jdbc.use_get_generated_keys", "true");
        return hibernateProperties;
    }
}
