package br.net.mirante.singular.server.p.core.wicket.rascunho;

import br.net.mirante.singular.server.commons.wicket.view.template.Content;
import br.net.mirante.singular.server.core.wicket.template.ServerTemplate;
import org.wicketstuff.annotation.mount.MountPath;
import static br.net.mirante.singular.server.commons.util.Parameters.MENU_PARAM_NAME;
import static br.net.mirante.singular.server.commons.util.Parameters.PROCESS_GROUP_PARAM_NAME;
@MountPath("rascunho")
public class RascunhoPage extends ServerTemplate {

    @Override
    protected Content getContent(String id) {
        return new RascunhoContent(id,
                getPageParameters().get(PROCESS_GROUP_PARAM_NAME).toString(),
                getPageParameters().get(MENU_PARAM_NAME).toString()
        );
    }
}
