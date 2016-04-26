package br.net.mirante.singular.server.core.wicket.concluida;

import br.net.mirante.singular.server.commons.wicket.view.template.Content;
import br.net.mirante.singular.server.core.wicket.template.ServerTemplate;
import org.wicketstuff.annotation.mount.MountPath;


@MountPath("concluidas")
public class ConcluidaPage extends ServerTemplate {


    @Override
    protected String getPageTitleLocalKey() {
        return "worklist.page.title";
    }

    @Override
    protected Content getContent(String id) {
        return new ConcluidaContent(id);
    }
}
