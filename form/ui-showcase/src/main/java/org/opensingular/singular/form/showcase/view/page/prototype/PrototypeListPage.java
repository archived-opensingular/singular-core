/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.form.showcase.view.page.prototype;

import org.wicketstuff.annotation.mount.MountPath;

import org.opensingular.singular.form.showcase.view.template.Content;
import org.opensingular.singular.form.showcase.view.template.Template;

@MountPath("prototype/list")
public class PrototypeListPage extends Template {

    @Override
    protected Content getContent(String id) {
        return new PrototypeListContent(id);
    }
}
