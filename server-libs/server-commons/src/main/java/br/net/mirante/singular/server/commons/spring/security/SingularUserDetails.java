package br.net.mirante.singular.server.commons.spring.security;


import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import br.net.mirante.singular.server.commons.config.IServerContext;

public interface SingularUserDetails extends UserDetails {

    public default boolean isContext(IServerContext context) {
        return context.equals(getServerContext());
    }

    public IServerContext getServerContext();

    public String getDisplayName();

    public List<SingularPermission> getPermissions();

    public default List<String> getPermissionsSingular() {
        return getPermissions().stream()
                .map(SingularPermission::getSingularId)
                .collect(Collectors.toList());
    }

    public default List<Serializable> getPermissionsInternal() {
        return getPermissions().stream()
                .map(SingularPermission::getInternalId)
                .collect(Collectors.toList());
    }

    @Override
    public default Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public default String getPassword() {
        return null;
    }


    public void addPermission(SingularPermission role);

    public default void addPermissions(SingularPermission... roles) {
        addPermissions(Arrays.asList(roles));
    }

    public default void addPermissions(List<SingularPermission> roles) {
        if (roles != null) {
            for (SingularPermission role : roles) {
                addPermission(role);
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
