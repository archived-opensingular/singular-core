package org.opensingular.server.p.core.wicket.view;

import org.opensingular.server.commons.wicket.view.template.Menu;
import org.opensingular.server.core.wicket.template.ServerTemplate;

public abstract class PeticionamentoTemplate extends ServerTemplate {

    @Override
    protected Menu configureMenu(String id) {
        return new MenuPeticionamento(id);
    }
}
