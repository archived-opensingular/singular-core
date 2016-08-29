package br.net.mirante.singular.server.core.wicket.historico;


import br.net.mirante.singular.server.commons.wicket.historico.AbstractHistoricoContent;
import br.net.mirante.singular.server.commons.wicket.view.template.Content;
import br.net.mirante.singular.server.core.wicket.entrada.CaixaEntradaAnalisePage;
import br.net.mirante.singular.server.core.wicket.template.ServerTemplate;
import org.apache.wicket.Page;
import org.apache.wicket.markup.html.link.Link;
import org.wicketstuff.annotation.mount.MountPath;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.$b;


@MountPath("historico")
public class HistoricoPage extends ServerTemplate {

    private static final long serialVersionUID = -3344810189307767761L;

    @Override
    protected Content getContent(String id) {
        return new AbstractHistoricoContent(id) {

            @Override
            protected Link getBtnCancelar() {
                Link btnCancelar = super.getBtnCancelar();
                btnCancelar.add($b.attrAppender("onclick", "window.history.go(-1);", ""));
                return btnCancelar;
            }

            @Override
            protected Class<? extends Page> getBackPage() {
                return CaixaEntradaAnalisePage.class;
            }
        };
    }

}
