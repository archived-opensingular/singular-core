package org.opensingular.server.p.commons.auth;

import com.google.common.hash.Hashing;
import org.opensingular.server.commons.spring.security.DefaultUserDetails;
import org.opensingular.server.p.commons.config.PServerContext;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

import java.nio.charset.StandardCharsets;

public class AdministrationAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {

    }

    @Override
    protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        String password     = authentication.getCredentials().toString();
        String passwordhash = Hashing.sha1().hashString(password, StandardCharsets.UTF_8).toString();
        if (username.equalsIgnoreCase("admin") && "0aca995b93addee9348dcef9016c0f9624dfae3a".equals(passwordhash)) {
            return new DefaultUserDetails(username, null, username, PServerContext.ADMINISTRATION);
        }
        throw new BadCredentialsException("erro");
    }

}