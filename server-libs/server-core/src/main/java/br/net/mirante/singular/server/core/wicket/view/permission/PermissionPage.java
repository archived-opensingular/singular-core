/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.server.core.wicket.view.permission;

import org.wicketstuff.annotation.mount.MountPath;

import br.net.mirante.singular.server.commons.wicket.view.template.Content;
import br.net.mirante.singular.server.core.wicket.template.ServerTemplate;

@MountPath("permission/manager")
public class PermissionPage extends ServerTemplate {

    @Override
    protected Content getContent(String id) {
        return new PermissionContent(id);
    }

    @Override
    protected boolean withMenu() {
        return false;
    }
}
