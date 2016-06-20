/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.server.p.core.wicket.caixa;

import org.wicketstuff.annotation.mount.MountPath;

import br.net.mirante.singular.commons.lambda.IFunction;
import br.net.mirante.singular.server.commons.wicket.view.template.Content;
import br.net.mirante.singular.server.core.wicket.template.ServerTemplate;

@MountPath("caixa")
public class CaixaPage extends ServerTemplate {

    private IFunction<String, Content> contentFunction;

    public CaixaPage(IFunction<String, Content> contentFunction) {
        this.contentFunction = contentFunction;
    }

    @Override
    protected Content getContent(String id) {
        return contentFunction.apply(id);
    }

}
