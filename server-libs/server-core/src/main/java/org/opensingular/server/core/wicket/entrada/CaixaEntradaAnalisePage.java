package org.opensingular.server.core.wicket.entrada;

import org.opensingular.server.commons.wicket.view.template.Content;
import org.opensingular.server.core.wicket.template.ServerTemplate;
import org.wicketstuff.annotation.mount.MountPath;

@MountPath("caixaentrada")
public class CaixaEntradaAnalisePage extends ServerTemplate {


    @Override
    protected String getPageTitleLocalKey() {
        return "worklist.page.title";
    }

    @Override
    protected Content getContent(String id) {
        return new EntradaContent(id);
    }
}
