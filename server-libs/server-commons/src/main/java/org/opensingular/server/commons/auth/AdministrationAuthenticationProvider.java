package org.opensingular.server.commons.auth;

import org.opensingular.server.commons.config.IServerContext;
import org.opensingular.server.commons.spring.security.DefaultUserDetails;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

public class AdministrationAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

    private final AdminCredentialChecker credentialChecker;
    private final IServerContext         serverContext;

    public AdministrationAuthenticationProvider(AdminCredentialChecker credentialChecker,
                                                IServerContext serverContext) {
        this.credentialChecker = credentialChecker;
        this.serverContext = serverContext;
    }

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails,
                                                  UsernamePasswordAuthenticationToken authentication)
            throws AuthenticationException {

    }

    @Override
    protected UserDetails retrieveUser(String principal,
                                       UsernamePasswordAuthenticationToken authentication)
            throws AuthenticationException {
        if (credentialChecker.check(principal, authentication.getCredentials().toString())) {
            return new DefaultUserDetails(principal, null, principal, serverContext);
        }
        throw new BadCredentialsException("Não foi possivel autenticar o usuario informado");
    }

}