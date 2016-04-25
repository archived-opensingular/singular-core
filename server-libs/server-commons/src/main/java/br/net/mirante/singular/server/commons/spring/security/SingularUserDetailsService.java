package br.net.mirante.singular.server.commons.spring.security;

import br.net.mirante.singular.server.commons.config.IServerContext;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.Collection;

@Transactional
public interface SingularUserDetailsService extends UserDetailsService, UserDetailsContextMapper {


    @Override
    public default SingularUserDetails mapUserFromContext(DirContextOperations dirContextOperations, String s, Collection<? extends GrantedAuthority> collection) {
        return loadUserByUsername(s);
    }

    @Override
    public default void mapUserToContext(UserDetails userDetails, DirContextAdapter dirContextAdapter) {
    }

    @Override
    public default SingularUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        return loadUserByUsername(username, IServerContext.getContextFromRequest(request, getContexts()));
    }


    public SingularUserDetails loadUserByUsername(String username, IServerContext context) throws UsernameNotFoundException;

    public IServerContext[] getContexts();

}
