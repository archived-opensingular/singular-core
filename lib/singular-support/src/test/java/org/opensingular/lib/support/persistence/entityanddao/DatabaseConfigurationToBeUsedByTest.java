package org.opensingular.lib.support.persistence.entityanddao;

import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@ComponentScan(basePackages = {"org.opensingular.lib.support.persistence.entityanddao"})
@EnableTransactionManagement(proxyTargetClass = true)
public class DatabaseConfigurationToBeUsedByTest {

    @Bean
    public PlatformTransactionManager platformTransactionManager(SessionFactory factory){
        return new HibernateTransactionManager(factory);
    }


    @Bean
    public DataSource getDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl("jdbc:h2:test");
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUsername("h2");

        return dataSource;
    }

    @Bean
    public LocalSessionFactoryBean entityManagerFactory(DataSource dataSource) {
        LocalSessionFactoryBean factory = new LocalSessionFactoryBean();
        factory.setDataSource(dataSource);

        Properties hibernateProperties = new Properties();
        hibernateProperties.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        hibernateProperties.put("hibernate.show_sql", "true");
        hibernateProperties.put("hibernate.hbm2ddl.auto", "create-drop");
        factory.setHibernateProperties(hibernateProperties);

        factory.setPackagesToScan("org.opensingular.lib.support.persistence");

        return factory;
    }

}
