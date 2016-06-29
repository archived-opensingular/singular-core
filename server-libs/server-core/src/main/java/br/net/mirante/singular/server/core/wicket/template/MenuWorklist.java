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
import br.net.mirante.singular.server.commons.service.PetitionService;
import br.net.mirante.singular.server.commons.service.dto.MenuGroup;
import br.net.mirante.singular.server.commons.wicket.SingularSession;
import br.net.mirante.singular.server.commons.wicket.view.template.MenuSessionConfig;
import br.net.mirante.singular.server.core.wicket.concluida.ConcluidaPage;
import br.net.mirante.singular.server.core.wicket.inicio.InicioPage;
import br.net.mirante.singular.util.wicket.menu.MetronicMenu;
import br.net.mirante.singular.util.wicket.menu.MetronicMenuGroup;
import br.net.mirante.singular.util.wicket.menu.MetronicMenuItem;
import br.net.mirante.singular.util.wicket.resource.Icone;


@SuppressWarnings("serial")
public class MenuWorklist extends MenuAnalise {

    @SuppressWarnings("rawtypes")
    @Inject
    private PetitionService petitionService;

    public MenuWorklist(String id) {
        super(id);
    }

    @SuppressWarnings("unchecked")
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

        itens.add(Pair.of(entrada.getHelper(), () -> String.valueOf(petitionService.countTasks(null, SingularSession.get().getRoleIds(), null, false))));
        itens.add(Pair.of(concluidas.getHelper(), () -> String.valueOf(petitionService.countTasks(null, SingularSession.get().getRoleIds(), null, true))));
        menu.add(new AddContadoresBehaviour(itens));

        return menu;
    }

    private void loadMenuGroups() {
        final MenuSessionConfig menuSessionConfig = getMenuSessionConfig();
        if (!menuSessionConfig.isInitialized()) {
            final List<ProcessGroupEntity> categorias = petitionService.listarTodosGruposProcesso();
            menuSessionConfig.initialize(categorias);
        }
    }

    private List<MenuGroup> listMenus(ProcessGroupEntity processGroup) {
        final String url = processGroup.getConnectionURL() + PATH_LIST_MENU;
        try {
            return Arrays.asList(new RestTemplate().getForObject(url, MenuGroup[].class));
        } catch (Exception e) {
            LOGGER.error("Erro ao acessar serviço: " + url, e);
            return Collections.emptyList();
        }
    }
}
