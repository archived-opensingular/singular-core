package org.opensingular.singular.server.core.wicket.historico;


import org.opensingular.singular.server.commons.wicket.historico.AbstractHistoricoContent;
import org.opensingular.singular.server.commons.wicket.view.template.Content;
import org.opensingular.singular.server.core.wicket.template.ServerTemplate;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.wicketstuff.annotation.mount.MountPath;


@MountPath("historico")
public class HistoricoPage extends ServerTemplate {

    private static final long serialVersionUID = -3344810189307767761L;

    @Override
    protected Content getContent(String id) {
        return new AbstractHistoricoContent(id) {
            @Override
            protected void onCancelar(AjaxRequestTarget t) {
                t.appendJavaScript("window.history.go(-1);");
            }
        };
    }

}
