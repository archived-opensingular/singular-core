package br.net.mirante.singular.pet.server.spring.security;

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * Responsável por fazer a configuração do spring security
 * os métodos implementados são utilizados pelo {@link WebSecurityConfigurerAdapter} default do singular:
 * {@link PetServerSpringSecurity}
 */
public interface SingularSpringSecurityConfigurer {

    default void configure(WebSecurity web) throws Exception {

    }

    default void configure(HttpSecurity http) throws Exception {

    }

    default void configure(AuthenticationManagerBuilder auth) throws Exception {

    }
}
