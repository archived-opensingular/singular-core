package br.net.mirante.singular.pet.server.wicket.view.acompanhamento;

import br.net.mirante.singular.pet.module.wicket.view.template.Content;
import org.apache.wicket.model.IModel;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.$m;

public class AcompanhamentoContent extends Content {

    public AcompanhamentoContent(String id) {
        super(id);
    }

    public AcompanhamentoContent(String id, boolean withInfoLink, boolean withBreadcrumb) {
        super(id, withInfoLink, withBreadcrumb);
    }

    @Override
    protected IModel<?> getContentTitlelModel() {
        return $m.ofValue("Página inicial");
    }

    @Override
    protected IModel<?> getContentSubtitlelModel() {
        return $m.ofValue("Página inicial");
    }
}
