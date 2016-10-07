/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.server.sso;

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
