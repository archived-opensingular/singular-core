/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.server.commons.wicket.error;

import org.wicketstuff.annotation.mount.MountPath;

import br.net.mirante.singular.server.commons.wicket.view.template.Content;
import br.net.mirante.singular.server.commons.wicket.view.template.Template;

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
