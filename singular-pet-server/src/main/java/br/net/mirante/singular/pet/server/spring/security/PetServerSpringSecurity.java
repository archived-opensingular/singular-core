package br.net.mirante.singular.pet.server.spring.security;


import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Provedor padrão do spring security do Singular
 * Delega a congfiguração do spring security a um bean que implementa a interface
 * {@link SingularSpringSecurityConfigurer}. Esse bean deve ser único
 */
@Configuration
@EnableWebSecurity
public class PetServerSpringSecurity extends WebSecurityConfigurerAdapter {

    @Inject
    private SingularSpringSecurityConfigurer configurer;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        configurer.configure(http);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        configurer.configure(web);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        configurer.configure(auth);
    }
}
