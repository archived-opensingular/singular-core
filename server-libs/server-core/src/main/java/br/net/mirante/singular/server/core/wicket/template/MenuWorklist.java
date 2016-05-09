/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.server.core.wicket.template;


import static br.net.mirante.singular.server.commons.service.IServerMetadataREST.PATH_LIST_MENU;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.wicket.Component;
import org.springframework.web.client.RestTemplate;

import br.net.mirante.singular.commons.lambda.ISupplier;
import br.net.mirante.singular.persistence.entity.ProcessGroupEntity;
import br.net.mirante.singular.server.commons.persistence.dto.TaskInstanceDTO;
import br.net.mirante.singular.server.commons.service.AnalisePeticaoService;
import br.net.mirante.singular.server.commons.service.PetitionService;
import br.net.mirante.singular.server.commons.service.dto.MenuGroupDTO;
import br.net.mirante.singular.server.commons.wicket.SingularSession;
import br.net.mirante.singular.server.core.wicket.concluida.ConcluidaPage;
import br.net.mirante.singular.server.core.wicket.inicio.InicioPage;
import br.net.mirante.singular.util.wicket.menu.MetronicMenu;
import br.net.mirante.singular.util.wicket.menu.MetronicMenuGroup;
import br.net.mirante.singular.util.wicket.menu.MetronicMenuItem;
import br.net.mirante.singular.util.wicket.resource.Icone;


@SuppressWarnings("serial")
public class MenuWorklist extends MenuAnalise {

    @Inject
    private AnalisePeticaoService<TaskInstanceDTO> analisePeticaoService;

    @Inject
    private PetitionService petitionService;

    public MenuWorklist(String id) {
        super(id);
    }

    @Override
    protected MetronicMenu buildMenu() {
        loadMenuGroups();

        final MetronicMenu menu = new MetronicMenu("menu");
        //TODO prover solução melhor para todos os contextos de aplicação
        final MetronicMenuGroup group = new MetronicMenuGroup(Icone.LAYERS, "Worklist");
        final MetronicMenuItem entrada = new MetronicMenuItem(Icone.DOCS, "Inicio", InicioPage.class);
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

    private void loadMenuGroups() {
        if (SingularSession.get().getMap().isEmpty()) {
            final List<ProcessGroupEntity> categorias = petitionService.listarTodosGruposProcesso();
            for (ProcessGroupEntity categoria : categorias) {
                final List<MenuGroupDTO> menuGroupDTOs = listMenus(categoria);
                SingularSession.get().addMenu(categoria, menuGroupDTOs);
            }
        }
    }

    private List<MenuGroupDTO> listMenus(ProcessGroupEntity processGroup) {
        final String url = processGroup.getConnectionURL() + PATH_LIST_MENU;
        try {
            return Arrays.asList(new RestTemplate().getForObject(url, MenuGroupDTO[].class));
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
