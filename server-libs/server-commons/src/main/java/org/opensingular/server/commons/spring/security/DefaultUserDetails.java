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

package org.opensingular.server.commons.spring.security;


import java.util.ArrayList;
import java.util.List;

import org.opensingular.server.commons.config.IServerContext;

public class DefaultUserDetails implements SingularUserDetails {

    private String displayName;

    private List<SingularPermission> permissions = new ArrayList<>();

    private IServerContext serverContext;

    private String username;

    public DefaultUserDetails(String username, List<SingularPermission> roles, String displayName, IServerContext context) {
        this.username = username;
        this.permissions = roles;
        this.displayName = displayName;
        this.serverContext = context;
    }

    @Override
    public void addPermission(SingularPermission role) {
        permissions.add(role);
    }

    @Override
    public Object getUserPermissionKey() {
        return this.getUsername();
    }

    @Override
    public IServerContext getServerContext() {
        return serverContext;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public List<SingularPermission> getPermissions() {
        return permissions;
    }

    @Override
    public String getUsername() {
        return username;
    }

}
