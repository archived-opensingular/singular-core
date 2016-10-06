package org.opensingular.server.p.core.wicket.acompanhamento;

import org.opensingular.server.commons.wicket.view.template.Content;
import org.opensingular.server.core.wicket.template.ServerTemplate;
import org.wicketstuff.annotation.mount.MountPath;

import static org.opensingular.server.commons.util.Parameters.MENU_PARAM_NAME;
import static org.opensingular.server.commons.util.Parameters.PROCESS_GROUP_PARAM_NAME;

@MountPath("acompanhamento")
public class AcompanhamentoPage extends ServerTemplate {

    @Override
    protected Content getContent(String id) {
        return new AcompanhamentoContent(id,
                getPageParameters().get(PROCESS_GROUP_PARAM_NAME).toString(),
                getPageParameters().get(MENU_PARAM_NAME).toString()
        );
    }

}
