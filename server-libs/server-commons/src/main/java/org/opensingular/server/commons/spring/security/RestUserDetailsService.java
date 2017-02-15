package org.opensingular.server.commons.spring.security;

import org.springframework.security.core.userdetails.UserDetailsService;

public interface RestUserDetailsService extends UserDetailsService {

    String getSubjectPrincipalRegex();

}