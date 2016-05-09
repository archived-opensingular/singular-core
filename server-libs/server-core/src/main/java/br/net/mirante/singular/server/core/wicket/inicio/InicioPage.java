/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.server.core.wicket.inicio;

import org.wicketstuff.annotation.mount.MountPath;

import br.net.mirante.singular.server.commons.wicket.view.template.Content;
import br.net.mirante.singular.server.core.wicket.template.WorklistTemplate;

@MountPath("inicio")
public class InicioPage extends WorklistTemplate {


    @Override
    protected String getPageTitleLocalKey() {
        return "worklist.page.title";
    }

    @Override
    protected Content getContent(String id) {
        return new InicioContent(id);
    }
}
