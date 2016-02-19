package br.net.mirante.singular.service;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.net.mirante.singular.dao.BaseDAO;
import br.net.mirante.singular.flow.core.MUser;
import br.net.mirante.singular.persistence.entity.Actor;

@Service
@Transactional(readOnly = true)
public class UserDetailsService extends BaseDAO implements org.springframework.security.core.userdetails.UserDetailsService, UserDetailsContextMapper {

    @Value("#{singularAdmin['springsecurity.username.prefix']}")
    private String usernamePrefix;

    @Override
    public UIAdminUser loadUserByUsername(String username) throws UsernameNotFoundException {
        UIAdminUser user = findUser(username);
        if(user == null){
            throw new UsernameNotFoundException("username="+username);
        }
        return user;
    }

    @Override
    public UserDetails mapUserFromContext(DirContextOperations ctx, String username, Collection<? extends GrantedAuthority> authorities) {
        UIAdminUser user = findUser(username);
        user.authoritiesnew.addAll(authorities);
        return user;
    }

    @Override
    public void mapUserToContext(UserDetails user, DirContextAdapter ctx) {
        throw new UnsupportedOperationException(
            "LdapUserDetailsMapper only supports reading from a context. Please"
                    + "use a subclass if mapUserToContext() is required.");
    }
    
    private UIAdminUser findUser(String username) {
        Criteria c = getSession().createCriteria(Actor.class);
        c.add(Restrictions.ilike("email", username + StringUtils.trimToEmpty(usernamePrefix)));
        Actor actor = (Actor) c.uniqueResult();
        
        return actor == null ? null : new UIAdminUser(username, actor.getCod(), actor.getSimpleName(), actor.getEmail());
    }
    
    public static final class UIAdminUser implements UserDetails, MUser {

        private final Collection<GrantedAuthority> authoritiesnew;

        private final String username;

        private final Integer userCod;

        private final String simpleName;

        private final String email;

        public UIAdminUser(String username) {
            super();
            this.username = username;
            this.userCod = null;
            this.simpleName = username;
            this.email = null;
            authoritiesnew = new ArrayList<>();
        }
        
        public UIAdminUser(String username, Integer userCod, String simpleName, String email) {
            super();
            this.username = username;
            this.userCod = userCod;
            this.simpleName = simpleName;
            this.email = email;
            authoritiesnew = new ArrayList<>(1);
            authoritiesnew.add(new SimpleGrantedAuthority("ROLE_USER"));
        }

        @Override
        public int compareTo(MUser o) {
            int compareTo = getSimpleName().compareTo(o.getSimpleName());
            if (compareTo != 0) {
                return compareTo;
            }
            return getCod().compareTo(o.getCod());
        }

        @Override
        public Integer getCod() {
            return userCod;
        }

        @Override
        public String getSimpleName() {
            return simpleName;
        }

        @Override
        public String getEmail() {
            return email;
        }

        @Override
        public String getCodUsuario() {
            return username;
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return authoritiesnew;
        }

        @Override
        public String getPassword() {
            return "1234";
        }

        @Override
        public String getUsername() {
            return username;
        }

        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        @Override
        public boolean isAccountNonLocked() {
            return true;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }

    }

}
