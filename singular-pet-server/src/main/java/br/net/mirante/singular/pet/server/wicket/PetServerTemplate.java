package br.net.mirante.singular.pet.server.wicket;

import br.net.mirante.singular.pet.module.wicket.view.template.Content;
import br.net.mirante.singular.pet.module.wicket.view.template.Template;
import br.net.mirante.singular.pet.server.wicket.view.content.HomeContent;

public class PetServerTemplate extends Template {


    @Override
    protected Content getContent(String id) {
        return new HomeContent(id);
    }
}
