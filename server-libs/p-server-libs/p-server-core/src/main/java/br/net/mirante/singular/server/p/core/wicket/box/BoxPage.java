/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.server.p.core.wicket.box;

import org.wicketstuff.annotation.mount.MountPath;

import br.net.mirante.singular.commons.lambda.IFunction;
import br.net.mirante.singular.server.commons.wicket.view.template.Content;
import br.net.mirante.singular.server.commons.wicket.view.template.Header;
import br.net.mirante.singular.server.commons.wicket.view.template.Menu;
import br.net.mirante.singular.server.core.wicket.template.ServerTemplate;

@MountPath("caixa")
public class BoxPage extends ServerTemplate {

    private IFunction<String, Content> contentFunction;
    private IFunction<String, Header> headerFunction;
    private IFunction<String, Menu> menuFunction;

    public BoxPage(IFunction<String, Content> contentFunction,
                   IFunction<String, Header> headerFunction,
                   IFunction<String, Menu> menuFunction) {
        this.contentFunction = contentFunction;
        this.headerFunction = headerFunction;
        this.menuFunction = menuFunction;
    }

    @Override
    protected Content getContent(String id) {
        return contentFunction.apply(id);
    }

    @Override
    protected Header configureHeader(String id) {
        return headerFunction.apply(id);
    }

    @Override
    protected Menu configureMenu(String id) {
        return menuFunction.apply(id);
    }
}
