package br.net.mirante.singular.pet.module.wicket.view.template;

import br.net.mirante.singular.pet.module.wicket.PetApplication;
import br.net.mirante.singular.util.wicket.menu.MetronicMenu;
import br.net.mirante.singular.util.wicket.menu.MetronicMenuItem;
import br.net.mirante.singular.util.wicket.resource.Icone;
import org.apache.wicket.markup.html.panel.Panel;

public class Menu extends Panel {

    /**
     *
     */
    private static final long serialVersionUID = 7622791136418841943L;

    public Menu(String id) {
        super(id);
        add(buildMenu());
    }

    protected MetronicMenu buildMenu() {
        MetronicMenu menu = new MetronicMenu("menu");

        menu.addItem(new MetronicMenuItem(Icone.HOME, "Início", PetApplication.get().getHomePage()));

        return menu;
    }
}
