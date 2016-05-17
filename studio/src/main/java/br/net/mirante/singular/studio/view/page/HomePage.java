/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.studio.view.page;

import org.apache.wicket.model.IModel;

import br.net.mirante.singular.studio.view.template.Content;
import br.net.mirante.singular.studio.view.template.Template;
import br.net.mirante.singular.util.wicket.util.WicketUtils;

// Página apenas para teste
public class HomePage extends Template {

    @Override
    protected Content getContent(String id) {
        return new Content(id) {
            @Override
            protected IModel<?> getContentTitleModel() {
                return WicketUtils.$m.ofValue("Singular Studio");
            }

            @Override
            protected IModel<?> getContentSubtitleModel() {
                return WicketUtils.$m.ofValue("Página de teste");
            }
        };
    }

}
