/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.bam.wicket;

import org.apache.wicket.Session;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import org.springframework.security.core.context.SecurityContextHolder;

import br.net.mirante.singular.flow.core.MUser;

public class UIAdminSession extends AuthenticatedWebSession {

    private Roles roles = new Roles();

    public UIAdminSession(Request request, Response response) {
        super(request);
        this.roles.add(Roles.USER);
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

    public String getUserId(){
        return String.valueOf(getUser().getCod());
    }
    
    public MUser getUser(){
        return (MUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}