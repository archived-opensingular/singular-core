package br.net.mirante.singular.server.commons.spring.security;


import br.net.mirante.singular.server.commons.config.IServerContext;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public interface SingularUserDetails extends UserDetails {

    public default boolean isContext(IServerContext context) {
        return context.equals(getServerContext());
    }

    public IServerContext getServerContext();

    public String getDisplayName();

    public List<String> getRoles();

    @Override
    public default Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public default String getPassword() {
        return null;
    }


    public void addRole(String role);

    public default void addRoles(String... roles) {
        addRoles(Arrays.asList(roles));
    }

    public default void addRoles(List<String> roles) {
        if (roles != null) {
            for (String role : roles) {
                addRole(role);
            }
        }
    }

    @Override
    public default boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public default boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public default boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public default boolean isEnabled() {
        return true;
    }
}
