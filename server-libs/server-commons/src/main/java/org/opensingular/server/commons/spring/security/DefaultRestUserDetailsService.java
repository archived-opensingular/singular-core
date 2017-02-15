package org.opensingular.server.commons.spring.security;

import org.opensingular.lib.commons.base.SingularProperties;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class DefaultRestUserDetailsService implements RestUserDetailsService {

    public static final String SUBJECT_PRINCIPAL_REGEX = "CN=(.*?)(?:,|$)";

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (getAllowedCommonName().equals(username)) {
            return createUserDetails(username);
        }
        throw new UsernameNotFoundException("Não foi possivel autenticar o certificado informado");
    }

    protected UserDetails createUserDetails(String username){
        return new User(username, "", AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER"));
    }

    @Override
    public String getSubjectPrincipalRegex() {
        return SUBJECT_PRINCIPAL_REGEX;
    }

    protected String getAllowedCommonName() {
        return SingularProperties.get().getProperty(SingularProperties.REST_ALLOWED_COMMON_NAME, "");
    }

}