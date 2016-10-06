package org.opensingular.server.commons.spring.security.config.cas;


import org.opensingular.lib.commons.base.SingularProperties;
import org.opensingular.server.commons.exception.SingularServerException;
import org.opensingular.server.commons.spring.security.AbstractSingularSpringSecurityAdapter;
import org.opensingular.server.commons.spring.security.SingularUserDetailsService;
import org.opensingular.server.commons.spring.security.config.SingularLogoutHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.core.userdetails.UserDetailsByNameServiceWrapper;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.authentication.preauth.j2ee.J2eePreAuthenticatedProcessingFilter;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Arrays;
import java.util.Optional;


public abstract class SingularCASSpringSecurityConfig extends AbstractSingularSpringSecurityAdapter {

    @Inject
    @Named("peticionamentoUserDetailService")
    protected Optional<SingularUserDetailsService> peticionamentoUserDetailService;

    @Bean
    public SingularLogoutHandler singularLogoutHandler() {
        return new SingularCASLogoutHandler(getCASLogoutURL());
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        if (SingularProperties.get().isTrue(SingularProperties.SINGULAR_DEV_MODE)){
            web.debug(true);
        }
        super.configure(web);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        PreAuthenticatedAuthenticationProvider casAuthenticationProvider = new PreAuthenticatedAuthenticationProvider();
        casAuthenticationProvider.setPreAuthenticatedUserDetailsService(
                new UserDetailsByNameServiceWrapper<>(peticionamentoUserDetailService.orElseThrow(() ->
                                new SingularServerException(
                                        String.format("Bean %s do tipo %s não pode ser nulo. Para utilizar a configuração de segurança %s é preciso declarar um bean do tipo %s identificado pelo nome %s .",
                                                UserDetailsService.class.getName(),
                                                "peticionamentoUserDetailService",
                                                SingularCASSpringSecurityConfig.class.getName(),
                                                UserDetailsService.class.getName(),
                                                "peticionamentoUserDetailService"
                                        ))
                )
                )
        );

        ProviderManager authenticationManager = new ProviderManager(Arrays.asList(new AuthenticationProvider[]{casAuthenticationProvider}));

        J2eePreAuthenticatedProcessingFilter j2eeFilter = new J2eePreAuthenticatedProcessingFilter();
        j2eeFilter.setAuthenticationManager(authenticationManager);

        http
                .regexMatcher(getContext().getPathRegex())
                .httpBasic().authenticationEntryPoint(new Http403ForbiddenEntryPoint())
                .and()
                .csrf().disable()
                .headers().frameOptions().sameOrigin()
                .and()
                .jee().j2eePreAuthenticatedProcessingFilter(j2eeFilter)
                .and()
                .authorizeRequests()
                .antMatchers(getContext().getContextPath()).authenticated();

    }


    public abstract String getCASLogoutURL();
}
