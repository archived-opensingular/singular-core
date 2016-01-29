package br.net.mirante.singular.pet.server.wicket.template;

import br.net.mirante.singular.pet.module.wicket.view.template.Menu;
import br.net.mirante.singular.pet.server.wicket.view.acompanhamento.AcompanhamentoPage;
import br.net.mirante.singular.pet.server.wicket.view.entrada.EntradaPage;
import br.net.mirante.singular.pet.server.wicket.view.racunho.RascunhoPage;
import br.net.mirante.singular.util.wicket.menu.MetronicMenu;
import br.net.mirante.singular.util.wicket.menu.MetronicMenuGroup;
import br.net.mirante.singular.util.wicket.menu.MetronicMenuItem;
import br.net.mirante.singular.util.wicket.resource.Icone;

public class MenuPeticionamento extends Menu {

    /**
     *
     */
    private static final long serialVersionUID = 7622791136418841943L;

    public MenuPeticionamento(String id) {
        super(id);
    }

    @Override
    protected MetronicMenu buildMenu() {
        MetronicMenu menu = new MetronicMenu("menu");
        MetronicMenuGroup group = new MetronicMenuGroup(Icone.LAYERS, "Petições");
        menu.addItem(group);
        group.addItem(new MetronicMenuItem(Icone.NOTE, "Rascunho", RascunhoPage.class));
        group.addItem(new MetronicMenuItem(Icone.CLOCK, "Acompanhamento", AcompanhamentoPage.class));
        group.addItem(new MetronicMenuItem(Icone.DOCS, "Caixa de Entrada", EntradaPage.class));

        return menu;
    }
}
