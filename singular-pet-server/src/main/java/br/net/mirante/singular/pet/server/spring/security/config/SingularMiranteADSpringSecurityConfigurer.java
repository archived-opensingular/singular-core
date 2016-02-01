package br.net.mirante.singular.pet.server.spring.security.config;


import br.net.mirante.singular.pet.server.spring.security.SingularSpringSecurityConfigurer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;


public class SingularMiranteADSpringSecurityConfigurer implements SingularSpringSecurityConfigurer {


    @Override
    public void configure(WebSecurity web, UserDetailsService peticionamentoUserDetailService) throws Exception {

    }

    @Override
    public void configure(HttpSecurity http, UserDetailsService userDetailsService) throws Exception {
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
    public void configure(AuthenticationManagerBuilder auth, UserDetailsService userDetailsService) throws Exception {
        auth
                .ldapAuthentication()
                .userSearchFilter("(sAMAccountName={0})")
                .userSearchBase("OU=Mirante User,DC=miranteinfo,DC=com")
                .rolePrefix("ROLE_")
                .groupSearchBase("OU=GruposGS,DC=miranteinfo,DC=com")
                .groupSearchFilter("(member={0})")
                .userDetailsContextMapper((UserDetailsContextMapper) userDetailsService)
                .contextSource()
                .managerDn("tomcatLogin")
                .managerPassword("jnditomcat")
                .root("DC=miranteinfo,DC=com")
                .url("ldap://LUA.miranteinfo.com:389/");

    }

}
