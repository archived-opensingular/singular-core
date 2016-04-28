package br.net.mirante.singular.server.commons.spring;

import static springfox.documentation.builders.PathSelectors.regex;

import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import br.net.mirante.singular.form.mform.context.SingularFormContext;
import br.net.mirante.singular.form.spring.SpringServiceRegistry;
import br.net.mirante.singular.form.wicket.SingularFormConfigWicket;
import br.net.mirante.singular.form.wicket.SingularFormConfigWicketImpl;
import br.net.mirante.singular.persistence.service.ProcessRetrieveService;
import br.net.mirante.singular.support.spring.util.AutoScanDisabled;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableWebMvc
@EnableWebSecurity
@Configuration
@ComponentScan(
        basePackages = {"br.net.mirante.singular"},
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ANNOTATION,
                        value = AutoScanDisabled.class)
        })

public class SingularServerSpringAppConfig  {


    @Bean
    public SpringServiceRegistry getSpringServiceRegistry() {
        return new SpringServiceRegistry();
    }

    @Bean
    public SingularFormConfigWicket getSingularFormConfig(SpringServiceRegistry springServiceRegistry) {
        SingularFormConfigWicket singularFormConfigWicket = new SingularFormConfigWicketImpl();
        singularFormConfigWicket.setServiceRegistry(springServiceRegistry);
        return singularFormConfigWicket;
    }

    @Bean
    public SingularFormContext getSingularFormContext(SingularFormConfigWicket singularFormConfigWicket) {
        return singularFormConfigWicket.createContext();
    }

    @Bean
    public ProcessRetrieveService getProcessRetrieveService(SessionFactory sessionFactory) {
        ProcessRetrieveService processRetrieveService = new ProcessRetrieveService();
        processRetrieveService.setSessionLocator(sessionFactory::getCurrentSession);
        return processRetrieveService;
    }

}