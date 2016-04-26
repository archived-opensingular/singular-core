package br.net.mirante.singular.server.core.wicket.historico;



import br.net.mirante.singular.server.commons.wicket.historico.AbstractHistoricoContent;
import br.net.mirante.singular.server.commons.wicket.view.template.Content;
import br.net.mirante.singular.server.core.wicket.entrada.EntradaAnalisePage;
import br.net.mirante.singular.server.core.wicket.template.ServerTemplate;
import org.apache.wicket.Page;
import org.wicketstuff.annotation.mount.MountPath;


@MountPath("historico")
public class HistoricoPage extends ServerTemplate {

    private static final long serialVersionUID = -3344810189307767761L;

    @Override
    protected Content getContent(String id) {
        return new AbstractHistoricoContent(id){

            @Override
            protected Class<? extends Page> getBackPage() {
                return EntradaAnalisePage.class;
            }
        };
    }

}
