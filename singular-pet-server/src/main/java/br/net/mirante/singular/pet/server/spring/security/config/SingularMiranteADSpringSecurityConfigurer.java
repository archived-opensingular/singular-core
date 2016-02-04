package br.net.mirante.singular.pet.server.spring.security.config;


import br.net.mirante.singular.pet.module.exception.SingularServerException;
import br.net.mirante.singular.pet.server.spring.security.SingularSpringSecurityConfigurer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Optional;


public class SingularMiranteADSpringSecurityConfigurer implements SingularSpringSecurityConfigurer {


    @Inject
    @Named("peticionamentoUserDetailService")
    private Optional<UserDetailsContextMapper> peticionamentoUserDetailContextMapper;


    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .rememberMe().key("mirante").tokenValiditySeconds(604800).rememberMeParameter("remember")
                .and()
                .authorizeRequests()
                .antMatchers("/login*").permitAll()
                .antMatchers("/resources/**").permitAll()
                .antMatchers("/wicket/resource/**").permitAll()
                .antMatchers("/**").authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .failureUrl("/login?error=true")
                .defaultSuccessUrl("/", false)
                .and()
                .logout().logoutUrl("/logout");

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
                .userDetailsContextMapper(peticionamentoUserDetailContextMapper.orElseThrow(() ->
                                        new SingularServerException(
                                                String.format("Bean %s do tipo %s não pode ser nulo. Para utilizar a configuração de segurança %s é preciso declarar um bean do tipo %s identificado pelo nome %s .",
                                                        UserDetailsContextMapper.class.getName(),
                                                        "peticionamentoUserDetailService",
                                                        SingularMiranteADSpringSecurityConfigurer.class.getName(),
                                                        UserDetailsContextMapper.class.getName(),
                                                        "peticionamentoUserDetailService"
                                                ))
                        )
                )
                .contextSource()
                .managerDn("tomcatLogin")
                .managerPassword("jnditomcat")
                .root("DC=miranteinfo,DC=com")
                .url("ldap://LUA.miranteinfo.com:389/");

    }

}
