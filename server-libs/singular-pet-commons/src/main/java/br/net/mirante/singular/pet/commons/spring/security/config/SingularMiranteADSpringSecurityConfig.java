package br.net.mirante.singular.pet.commons.spring.security.config;


import br.net.mirante.singular.pet.commons.exception.SingularServerException;
import br.net.mirante.singular.pet.commons.spring.security.AbstractSingularSpringSecurityAdapter;
import br.net.mirante.singular.pet.commons.spring.security.SingularUserDetailsService;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Optional;


public abstract class SingularMiranteADSpringSecurityConfig extends AbstractSingularSpringSecurityAdapter {

    @Inject
    @Named("peticionamentoUserDetailService")
    private Optional<SingularUserDetailsService> peticionamentoUserDetailService;


    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .regexMatcher(getContext().getPathRegex())
                .csrf().disable()
                .headers().frameOptions().sameOrigin()
                .and()
                .authorizeRequests()
                .antMatchers(getDefaultPublicUrls()).permitAll()
                .antMatchers(getContext().getUrlPath() + "/login*").permitAll()
                .antMatchers(getContext().getUrlPath() + "/**").authenticated()
                .antMatchers("/**").permitAll()
                .and()
                .formLogin()
                .loginPage(getContext().getUrlPath() + "/login")
                .loginProcessingUrl(getContext().getUrlPath() + "/login")
                .failureUrl(getContext().getUrlPath() + "/login?error=true")
                .defaultSuccessUrl(getContext().getUrlPath() + "/", false)
                .and()
                .logout().logoutUrl(getContext().getUrlPath() + "/logout");

    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .ldapAuthentication()
                .userSearchFilter("(sAMAccountName={0})")
                .userSearchBase("OU=Mirante User,DC=miranteinfo,DC=com")
                .rolePrefix("ROLE_")
                .groupSearchBase("OU=GruposGS,DC=miranteinfo,DC=com")
                .groupSearchFilter("(member={0})")
                .userDetailsContextMapper(peticionamentoUserDetailService.orElseThrow(() ->
                                        new SingularServerException(
                                                String.format("Bean %s do tipo %s não pode ser nulo. Para utilizar a configuração de segurança %s é preciso declarar um bean do tipo %s identificado pelo nome %s .",
                                                        SingularUserDetailsService.class.getName(),
                                                        "SingularUserDetailsService",
                                                        SingularMiranteADSpringSecurityConfig.class.getName(),
                                                        SingularUserDetailsService.class.getName(),
                                                        "SingularUserDetailsService"
                                                ))
                        )
                )
                .contextSource()
                .managerDn("tomcatLogin")
                .managerPassword("jnditomcat")
                .root("DC=miranteinfo,DC=com")
                .url("ldap://LUA.miranteinfo.com:389/");

    }

    @Override
    public void configure(WebSecurity web) throws Exception {
    }
}
