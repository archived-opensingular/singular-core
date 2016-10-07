/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.server.commons.wicket.error;

import org.wicketstuff.annotation.mount.MountPath;

import org.opensingular.server.commons.wicket.view.template.Content;
import org.opensingular.server.commons.wicket.view.template.Template;

@MountPath("acesso/negado")
public class AccessDeniedPage extends Template {

    @Override
    protected Content getContent(String id) {
        return new AccessDeniedContent(id);
    }

    @Override
    protected boolean withMenu() {
        return false;
    }

}
