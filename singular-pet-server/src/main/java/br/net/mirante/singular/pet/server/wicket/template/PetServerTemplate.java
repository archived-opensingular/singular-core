package br.net.mirante.singular.pet.server.wicket.template;

import br.net.mirante.singular.pet.module.wicket.view.template.Template;

public abstract class PetServerTemplate extends Template {


    @Override
    protected MenuPeticionamento configureMenu(String id) {
        return new MenuPeticionamento(id);
    }
}
