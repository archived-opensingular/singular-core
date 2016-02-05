package br.net.mirante.singular.pet.server.spring.security.config;


import br.net.mirante.singular.pet.module.exception.SingularServerException;
import br.net.mirante.singular.pet.module.spring.security.SingularUserDetailsService;
import br.net.mirante.singular.pet.server.spring.security.SingularSpringSecurityConfigurer;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsByNameServiceWrapper;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.authentication.preauth.j2ee.J2eePreAuthenticatedProcessingFilter;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Arrays;
import java.util.Optional;


public class SingularCASSpringSecurityConfigurer implements SingularSpringSecurityConfigurer {

    @Inject
    @Named("peticionamentoUserDetailService")
    private Optional<SingularUserDetailsService> peticionamentoUserDetailService;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        PreAuthenticatedAuthenticationProvider casAuthenticationProvider = new PreAuthenticatedAuthenticationProvider();
        casAuthenticationProvider.setPreAuthenticatedUserDetailsService(
                new UserDetailsByNameServiceWrapper<>(peticionamentoUserDetailService.orElseThrow(() ->
                                new SingularServerException(
                                        String.format("Bean %s do tipo %s não pode ser nulo. Para utilizar a configuração de segurança %s é preciso declarar um bean do tipo %s identificado pelo nome %s .",
                                                UserDetailsService.class.getName(),
                                                "peticionamentoUserDetailService",
                                                SingularCASSpringSecurityConfigurer.class.getName(),
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
                .httpBasic().authenticationEntryPoint(new Http403ForbiddenEntryPoint())
                .and()
                .csrf().disable()
                .jee().j2eePreAuthenticatedProcessingFilter(j2eeFilter)
                .and()
                .authorizeRequests().antMatchers("/rest/*").permitAll()
                .and()
                .authorizeRequests().antMatchers("/*").authenticated();
    }


}
