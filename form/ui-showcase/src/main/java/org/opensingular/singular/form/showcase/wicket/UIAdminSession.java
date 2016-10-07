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

package org.opensingular.singular.form.showcase.wicket;

import org.apache.wicket.Session;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;

public class UIAdminSession extends AuthenticatedWebSession {

    private String name;
    private String avatar;
    private String logout;

    private Roles roles;

    public UIAdminSession(Request request, @SuppressWarnings("UnusedParameters") Response response) {
        super(request);
        this.name = request.getRequestParameters().getParameterValue("name").toString("Admin");
        this.avatar = request.getRequestParameters().getParameterValue("avatar").toString(null);
        this.logout = request.getRequestParameters().getParameterValue("logout").toString(null);
        this.roles = new Roles();
        this.roles.add(Roles.USER);
        this.roles.add(Roles.ADMIN);
    }

    public static UIAdminSession get() {
        return (UIAdminSession) Session.get();
    }

    @Override
    protected boolean authenticate(String username, String password) {
        return false;
    }

    @Override
    public Roles getRoles() {
        return roles;
    }

    public void addRole(String roleKey) {
        roles.add(roleKey);
    }

    public boolean hasAdminRole() {
        return roles.hasRole(Roles.ADMIN);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getLogout() {
        return logout;
    }

    public void setLogout(String logout) {
        this.logout = logout;
    }
}
