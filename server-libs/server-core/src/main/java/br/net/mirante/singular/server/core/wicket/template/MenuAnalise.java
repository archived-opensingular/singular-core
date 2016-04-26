package br.net.mirante.singular.server.core.wicket.template;


import br.net.mirante.singular.commons.lambda.ISupplier;
import br.net.mirante.singular.server.commons.service.AnalisePeticaoService;
import br.net.mirante.singular.server.commons.persistence.dto.TaskInstanceDTO;
import br.net.mirante.singular.server.commons.wicket.view.template.Menu;
import br.net.mirante.singular.server.core.wicket.concluida.ConcluidaPage;
import br.net.mirante.singular.server.core.wicket.entrada.EntradaAnalisePage;
import br.net.mirante.singular.util.wicket.menu.MetronicMenu;
import br.net.mirante.singular.util.wicket.menu.MetronicMenuGroup;
import br.net.mirante.singular.util.wicket.menu.MetronicMenuItem;
import br.net.mirante.singular.util.wicket.resource.Icone;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.wicket.Component;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("serial")
public class MenuAnalise extends Menu {

    @Inject
    private AnalisePeticaoService<TaskInstanceDTO> analisePeticaoService;

    public MenuAnalise(String id) {
        super(id);
    }

    @Override
    protected MetronicMenu buildMenu() {

        final MetronicMenu menu = new MetronicMenu("menu");
        //TODO prover solução melhor para todos os contextos de aplicação
        final MetronicMenuGroup group = new MetronicMenuGroup(Icone.LAYERS, "Petições");
        final MetronicMenuItem entrada = new MetronicMenuItem(Icone.DOCS, "Caixa de Entrada", EntradaAnalisePage.class);
        final MetronicMenuItem concluidas = new MetronicMenuItem(Icone.DOCS, "Concluídas", ConcluidaPage.class);

        menu.addItem(group);
        group.addItem(entrada);
        group.addItem(concluidas);


        final List<Pair<Component, ISupplier<String>>> itens = new ArrayList<>();

        itens.add(Pair.of(entrada.getHelper(), () -> String.valueOf(analisePeticaoService.countTasks(null, null, false))));
        itens.add(Pair.of(concluidas.getHelper(), () -> String.valueOf(analisePeticaoService.countTasks(null, null, true))));
        menu.add(new AddContadoresBehaviour(itens));

        return menu;
    }
}
