package org.opensingular.server.commons.config;

import org.opensingular.lib.support.spring.util.AutoScanDisabled;
import org.opensingular.server.commons.spring.security.RestUserDetailsService;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.inject.Inject;

@EnableWebMvc
@Configuration
@AutoScanDisabled
public class DefaultRestSecurity extends WebSecurityConfigurerAdapter {

    @Inject
    private RestUserDetailsService restUserDetailsService;

    public static final String REST_ANT_PATTERN = "/rest/**";

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.antMatcher(REST_ANT_PATTERN)
                .authorizeRequests().anyRequest().authenticated()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .csrf().disable()
                .x509()
                .subjectPrincipalRegex(restUserDetailsService.getSubjectPrincipalRegex())
                .userDetailsService(restUserDetailsService);

    }

}