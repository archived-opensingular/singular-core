package br.net.mirante.singular.pet.server.wicket.view.acompanhamento;

import br.net.mirante.singular.pet.module.wicket.view.template.Content;
import br.net.mirante.singular.pet.server.wicket.template.PetServerTemplate;
import org.wicketstuff.annotation.mount.MountPath;

@MountPath("acompanhamento")
public class AcompanhamentoPage extends PetServerTemplate {


    @Override
    protected Content getContent(String id) {
        return new AcompanhamentoContent(id);
    }
}
