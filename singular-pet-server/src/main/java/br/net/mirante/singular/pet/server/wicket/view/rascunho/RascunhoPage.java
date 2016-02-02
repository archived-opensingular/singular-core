package br.net.mirante.singular.pet.server.wicket.view.rascunho;

import br.net.mirante.singular.pet.module.wicket.view.template.Content;
import br.net.mirante.singular.pet.server.wicket.template.PetServerTemplate;
import org.wicketstuff.annotation.mount.MountPath;

@MountPath("rascunho")
public class RascunhoPage extends PetServerTemplate {


    @Override
    protected Content getContent(String id) {
        return new RascunhoContent(id);
    }
}
