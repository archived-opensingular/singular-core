package br.net.mirante.singular.pet.server.spring.security.config;


import br.net.mirante.singular.pet.server.spring.security.SingularSpringSecurityConfigurer;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.core.userdetails.UserDetailsByNameServiceWrapper;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.authentication.preauth.j2ee.J2eePreAuthenticatedProcessingFilter;

import java.util.Arrays;


public class SingularCASSpringSecurityConfigurer implements SingularSpringSecurityConfigurer {

    @Override
    public void configure(WebSecurity web, UserDetailsService userDetailsService) throws Exception {

    }

    @Override
    public void configure(HttpSecurity http, UserDetailsService peticionamentoUserDetailService) throws Exception {
        PreAuthenticatedAuthenticationProvider casAuthenticationProvider = new PreAuthenticatedAuthenticationProvider();
        casAuthenticationProvider.setPreAuthenticatedUserDetailsService(new UserDetailsByNameServiceWrapper<>(peticionamentoUserDetailService));

        ProviderManager authenticationManager = new ProviderManager(Arrays.asList(new AuthenticationProvider[]{casAuthenticationProvider}));

        J2eePreAuthenticatedProcessingFilter j2eeFilter = new J2eePreAuthenticatedProcessingFilter();
        j2eeFilter.setAuthenticationManager(authenticationManager);

        http
                .httpBasic().authenticationEntryPoint(new Http403ForbiddenEntryPoint())
                .and()
                .csrf().disable()
                .jee().j2eePreAuthenticatedProcessingFilter(j2eeFilter)
                .and()
                .authorizeRequests().antMatchers("/rest/*").permitAll()
                .and()
                .authorizeRequests().antMatchers("/*").authenticated();
    }

    @Override
    public void configure(AuthenticationManagerBuilder http, UserDetailsService userDetailsService) throws Exception {

    }

}
