package br.net.mirante.singular.pet.server.spring;

import br.net.mirante.singular.pet.server.spring.security.SingularSpringSecurityConfigurer;
import br.net.mirante.singular.pet.server.spring.security.config.SingularMiranteADSpringSecurityConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@EnableWebMvc
@EnableWebSecurity
@Configuration
@ComponentScan("br.net.mirante.singular, br.gov.anvisa")
public class PetServerSpringAppConfig extends WebMvcConfigurerAdapter {


    public Class<? extends SingularSpringSecurityConfigurer> getSingularSpringSecurityConfigurer() {
        return SingularMiranteADSpringSecurityConfigurer.class;
    }

    @Bean
    public SingularSpringSecurityConfigurer getSingularSpringConfigBean() throws IllegalAccessException, InstantiationException {
        return getSingularSpringSecurityConfigurer().newInstance();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/resources/**").addResourceLocations("/resources/");
    }

}
