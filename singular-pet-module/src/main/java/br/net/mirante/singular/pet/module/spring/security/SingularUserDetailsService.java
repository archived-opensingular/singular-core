package br.net.mirante.singular.pet.module.spring.security;

import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;

import java.util.Collection;

public interface SingularUserDetailsService extends UserDetailsService, UserDetailsContextMapper {


    @Override
    public default SingularUserDetails mapUserFromContext(DirContextOperations dirContextOperations, String s, Collection<? extends GrantedAuthority> collection) {
        return loadUserByUsername(s);
    }

    @Override
    public default void mapUserToContext(UserDetails userDetails, DirContextAdapter dirContextAdapter) {
    }

    @Override
    public SingularUserDetails loadUserByUsername(String username) throws UsernameNotFoundException;

}
