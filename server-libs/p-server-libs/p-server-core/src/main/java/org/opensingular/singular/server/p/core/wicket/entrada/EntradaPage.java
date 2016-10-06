package org.opensingular.singular.server.p.core.wicket.entrada;


import org.opensingular.singular.server.commons.wicket.view.template.Content;
import org.opensingular.singular.server.core.wicket.template.ServerTemplate;
import org.wicketstuff.annotation.mount.MountPath;

import static org.opensingular.singular.server.commons.util.Parameters.MENU_PARAM_NAME;
import static org.opensingular.singular.server.commons.util.Parameters.PROCESS_GROUP_PARAM_NAME;

@MountPath("caixaentrada")
public class EntradaPage extends ServerTemplate {


    @Override
    protected Content getContent(String id) {
        return new EntradaContent(id,
                getPageParameters().get(PROCESS_GROUP_PARAM_NAME).toString(),
                getPageParameters().get(MENU_PARAM_NAME).toString());
    }
}
