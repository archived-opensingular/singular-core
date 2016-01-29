package br.net.mirante.singular.pet.server.wicket.view.entrada;

import br.net.mirante.singular.pet.module.wicket.view.template.Content;
import br.net.mirante.singular.pet.server.wicket.template.PetServerTemplate;
import org.wicketstuff.annotation.mount.MountPath;

@MountPath("caixaentrada")
public class EntradaPage extends PetServerTemplate {


    @Override
    protected Content getContent(String id) {
        return new EntradaContent(id);
    }
}
