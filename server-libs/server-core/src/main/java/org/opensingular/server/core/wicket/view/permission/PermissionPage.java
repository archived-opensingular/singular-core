/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.server.core.wicket.view.permission;

import org.wicketstuff.annotation.mount.MountPath;

import org.opensingular.server.commons.wicket.view.template.Content;
import org.opensingular.server.core.wicket.template.ServerTemplate;

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
