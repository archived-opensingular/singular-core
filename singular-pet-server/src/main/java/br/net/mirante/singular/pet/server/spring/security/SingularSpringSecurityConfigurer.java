package br.net.mirante.singular.pet.server.spring.security;

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface SingularSpringSecurityConfigurer {

    void configure(WebSecurity web, UserDetailsService userDetailsService) throws Exception;

    void configure(HttpSecurity http, UserDetailsService userDetailsService) throws Exception;

    void configure(AuthenticationManagerBuilder auth, UserDetailsService userDetailsService) throws Exception;
}
