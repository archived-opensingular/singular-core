package org.opensingular.singular.sso;

import org.jasig.cas.authentication.HandlerResult;
import org.jasig.cas.authentication.PreventedException;
import org.jasig.cas.authentication.UsernamePasswordCredential;
import org.jasig.cas.authentication.handler.support.AbstractUsernamePasswordAuthenticationHandler;
import org.jasig.cas.authentication.principal.SimplePrincipal;
import org.springframework.util.StringUtils;

import java.security.GeneralSecurityException;

public class HardCodedAuthHandler extends AbstractUsernamePasswordAuthenticationHandler {
    @Override
    protected HandlerResult authenticateUsernamePasswordInternal(UsernamePasswordCredential credential) throws GeneralSecurityException, PreventedException {
        if (credential.getUsername() != null && !StringUtils.isEmpty(credential.getUsername())) {
            return createHandlerResult(credential, new SimplePrincipal(credential.getUsername()), null);
        }
        return null;
    }
}
