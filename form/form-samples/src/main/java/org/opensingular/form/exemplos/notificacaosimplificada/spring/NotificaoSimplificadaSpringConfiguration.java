/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
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

package org.opensingular.form.exemplos.notificacaosimplificada.spring;

import org.opensingular.lib.commons.base.SingularProperties;
import org.opensingular.lib.support.spring.util.AutoScanDisabled;
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

import javax.sql.DataSource;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

@AutoScanDisabled
@Configuration
@EnableTransactionManagement(proxyTargetClass = true)
@ComponentScan("org.opensingular.form.exemplos.notificacaosimplificada")
public class NotificaoSimplificadaSpringConfiguration {

    @Value("classpath:data/exemplos/notificacaosimplificada/drops.sql")
    private Resource drops;

    @Value("classpath:data/exemplos/notificacaosimplificada/create_tables.sql")
    private Resource createTables;

    @Value("classpath:data/exemplos/notificacaosimplificada/inserts.sql")
    private Resource inserts;

    @Value("classpath:data/exemplos/notificacaosimplificada/insert_geral.sql")
    private Resource insertGeral;

    @Value("classpath:data/exemplos/notificacaosimplificada/create-tables-anvisa.sql")
    private Resource createTablesAnvisa;

    @Value("classpath:data/exemplos/notificacaosimplificada/insert-usuario.sql")
    private Resource insertUsuario;

    @Bean
    public DriverManagerDataSource dataSource() {
        final DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl("jdbc:h2:./notificacaodb;AUTO_SERVER=TRUE;mode=ORACLE;CACHE_SIZE=4096;EARLY_FILTER=1;MULTI_THREADED=1;LOCK_TIMEOUT=15000;");
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
        sessionFactoryBean.setPackagesToScan("org.opensingular.form.exemplos.notificacaosimplificada.domain");
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
        populator.setSqlScriptEncoding(StandardCharsets.UTF_8.name());
        if (!SingularProperties.get().isFalse("anvisa.enabled.h2.inserts")) {
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
        hibernateProperties.put("hibernate.show_sql", "false");
        hibernateProperties.put("hibernate.format_sql", "true");
        hibernateProperties.put("hibernate.cache.use_second_level_cache", "false");
        hibernateProperties.put("hibernate.jdbc.use_get_generated_keys", "true");
        return hibernateProperties;
    }

}