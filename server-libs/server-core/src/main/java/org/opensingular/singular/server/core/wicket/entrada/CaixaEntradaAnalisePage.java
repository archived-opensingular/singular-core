package org.opensingular.singular.server.core.wicket.entrada;

import org.opensingular.singular.server.commons.wicket.view.template.Content;
import org.opensingular.singular.server.core.wicket.template.ServerTemplate;
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
