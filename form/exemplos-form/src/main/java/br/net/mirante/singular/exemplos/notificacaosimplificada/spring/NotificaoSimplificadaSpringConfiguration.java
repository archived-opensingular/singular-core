package br.net.mirante.singular.exemplos.notificacaosimplificada.spring;

import java.util.Properties;

import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import br.net.mirante.singular.support.spring.util.AutoScanDisabled;

@AutoScanDisabled
@Configuration
@EnableTransactionManagement(proxyTargetClass = true)
@ComponentScan("br.net.mirante.singular.exemplos")
public class NotificaoSimplificadaSpringConfiguration {

    @Value("classpath:data/notificacaosimplificada/drops.sql")
    private Resource drops;

    @Value("classpath:data/notificacaosimplificada/create_tables.sql")
    private Resource createTables;

    @Value("classpath:data/notificacaosimplificada/inserts.sql")
    private Resource inserts;

    @Value("classpath:data/notificacaosimplificada/insert_geral.sql")
    private Resource insertGeral;

    @Value("classpath:data/notificacaosimplificada/create-tables-anvisa.sql")
    private Resource createTablesAnvisa;

    @Value("classpath:data/notificacaosimplificada/insert-usuario.sql")
    private Resource insertUsuario;

    @Bean
    public DriverManagerDataSource dataSource() {
        final DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl("jdbc:h2:file:./notificacaodb;AUTO_SERVER=TRUE;mode=ORACLE;CACHE_SIZE=4096;MULTI_THREADED=1;EARLY_FILTER=1");
        dataSource.setUsername("sa");
        dataSource.setPassword("sa");
        dataSource.setDriverClassName("org.h2.Driver");
        return dataSource;
    }

    @Bean
    public LocalSessionFactoryBean sessionFactoryBean(final DataSource dataSource) {
        final LocalSessionFactoryBean sessionFactoryBean = new LocalSessionFactoryBean();
        sessionFactoryBean.setDataSource(dataSource);
        sessionFactoryBean.setHibernateProperties(hibernateProperties());
        sessionFactoryBean.setPackagesToScan("br.net.mirante.singular.exemplos.notificacaosimplificada.domain");
        return sessionFactoryBean;
    }

    @Bean
    public HibernateTransactionManager transactionManager(final SessionFactory sessionFactory, final DataSource dataSource) {
        final HibernateTransactionManager tx = new HibernateTransactionManager(sessionFactory);
        tx.setDataSource(dataSource);
        return tx;
    }

    @Bean
    public DataSourceInitializer dataSourceInitializer(final DataSource dataSource) {
        final DataSourceInitializer initializer = new DataSourceInitializer();
        initializer.setDataSource(dataSource);
        initializer.setDatabasePopulator(databasePopulator());
        return initializer;
    }

    private DatabasePopulator databasePopulator() {
        final ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.setSqlScriptEncoding("UTF-8");
        if (Boolean.valueOf(System.getProperty("anvisa.enabled.h2.inserts", "true"))) {
            populator.addScript(drops);
            populator.addScript(createTables);
            populator.addScript(inserts);
            populator.addScript(insertGeral);
            populator.addScript(createTablesAnvisa);
            populator.addScript(insertUsuario);
        }
        return populator;
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