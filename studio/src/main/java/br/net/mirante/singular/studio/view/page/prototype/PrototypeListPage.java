/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.studio.view.page.prototype;

import org.wicketstuff.annotation.mount.MountPath;

import br.net.mirante.singular.studio.view.template.Content;
import br.net.mirante.singular.studio.view.template.Template;

@MountPath("prototype/list")
public class PrototypeListPage extends Template {

    @Override
    protected Content getContent(String id) {
        return new PrototypeListContent(id);
    }
}
