/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.server.commons.wicket.view.template;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.component.IRequestablePage;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.handler.TextRequestHandler;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.opensingular.flow.persistence.entity.ProcessGroupEntity;
import org.opensingular.lib.commons.lambda.ISupplier;
import org.opensingular.lib.wicket.util.menu.MetronicMenu;
import org.opensingular.lib.wicket.util.menu.MetronicMenuGroup;
import org.opensingular.lib.wicket.util.menu.MetronicMenuItem;
import org.opensingular.lib.wicket.util.resource.Icone;
import org.opensingular.server.commons.config.IServerContext;
import org.opensingular.server.commons.config.SingularServerConfiguration;
import org.opensingular.server.commons.persistence.filter.QuickFilter;
import org.opensingular.server.commons.service.PetitionService;
import org.opensingular.server.commons.service.dto.FormDTO;
import org.opensingular.server.commons.service.dto.ItemBox;
import org.opensingular.server.commons.service.dto.MenuGroup;
import org.opensingular.server.commons.service.dto.ProcessDTO;
import org.opensingular.server.commons.spring.security.SingularUserDetails;
import org.opensingular.server.commons.wicket.SingularSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import static org.opensingular.server.commons.service.IServerMetadataREST.PATH_BOX_SEARCH;
import static org.opensingular.server.commons.util.DispatcherPageParameters.*;

public class Menu extends Panel {

    /**
     *
     */
    private static final long serialVersionUID = 7622791136418841943L;

    public static final String MENU_CACHE = "MENU_CACHE";

    protected static final Logger LOGGER = LoggerFactory.getLogger(Menu.class);

    private List<ProcessGroupEntity> categorias;

    private Class<? extends WebPage> boxPageClass;

    private MetronicMenu menu;

    @SuppressWarnings("rawtypes")
    @Inject
    protected PetitionService petitionService;

    @Inject
    private SingularServerConfiguration singularServerConfiguration;

    public Menu(String id, Class<? extends WebPage>  boxPageClass) {
        super(id);
        this.boxPageClass = boxPageClass;
        add(buildMenu());
    }

    protected MetronicMenu buildMenu() {
        this.loadMenuGroups();
        this.menu = new MetronicMenu("menu");
        this.buildMenuSelecao();
        this.getCategorias().forEach((processGroup) -> this.buildMenuGroup(this.menu, processGroup));
        return this.menu;
    }


    protected void buildMenuSelecao() {
        SelecaoMenuItem selecaoMenuItem = new SelecaoMenuItem(categorias);
        menu.addItem(selecaoMenuItem);
    }

    protected static class AddContadoresBehaviour extends AbstractDefaultAjaxBehavior {

        private final List<Pair<Component, ISupplier<String>>> itens;

        public AddContadoresBehaviour(List<Pair<Component, ISupplier<String>>> itens) {
            this.itens = itens;
        }

        @Override
        public void renderHead(Component component, IHeaderResponse response) {
            super.renderHead(component, response);
            StringBuilder js = new StringBuilder();
            js.append(" window.Singular = window.Singular || {};");
            js.append(" window.Singular.contadores =  window.Singular.contadores || []; ");
            js.append(" (function() {");
            js.append("     var novoContador = function(){ ");
            js.append("         $(document).ready(function(){ ");
            js.append("             $(document).ready(function(){");
            js.append("                 $.getJSON('").append(getCallbackUrl()).append("', function(json) { ");
            for (int i = 0; i < itens.size(); i++) {
                final String markupId    = itens.get(i).getLeft().getMarkupId();
                final String currentItem = "item" + i;
                js.append("var ").append(currentItem).append(" = ").append(" $('#").append(markupId).append("');");
                js.append(currentItem).append(".hide(); ");
                js.append(currentItem).append(".addClass('badge badge-danger'); ");
                js.append(currentItem).append(".html(json.").append(currentItem).append(");");
                js.append(currentItem).append(".fadeIn('slow'); ");
            }
            js.append("                 });");
            js.append("             });");
            js.append("         });");
            js.append("     };");
            js.append("     novoContador(); ");
            js.append("     window.Singular.contadores.push(novoContador); ");
            js.append(" }());");
            js.append(" window.Singular.atualizarContadores  = function(){$(window.Singular.contadores).each(function(){this();});}; ");
            response.render(OnDomReadyHeaderItem.forScript(js));
        }

        @Override
        protected void respond(AjaxRequestTarget target) {
            final String        type     = "application/json";
            final String        encoding = StandardCharsets.UTF_8.name();
            final StringBuilder json     = new StringBuilder();
            json.append('{');
            for (int i = 0; i < itens.size(); i++) {
                json.append("\"item").append(i).append('\"').append(':').append(itens.get(i).getRight().get());
                if (i + 1 != itens.size()) {
                    json.append(',');
                }
            }
            json.append('}');
            RequestCycle.get().scheduleRequestHandlerAfterCurrent(new TextRequestHandler(type, encoding, json.toString()));
        }
    }

    protected MenuSessionConfig getMenuSessionConfig() {
        final SingularSession session           = SingularSession.get();
        MenuSessionConfig     menuSessionConfig = (MenuSessionConfig) session.getAttribute(MENU_CACHE);
        if (menuSessionConfig == null) {
            menuSessionConfig = new MenuSessionConfig();
            session.setAttribute(MENU_CACHE, menuSessionConfig);
        }

        return menuSessionConfig;
    }

    protected void loadMenuGroups() {
        categorias = buscarCategorias();
        final MenuSessionConfig menuSessionConfig = getMenuSessionConfig();
        if (!menuSessionConfig.isInitialized()) {
            menuSessionConfig.initialize(categorias, getMenuContext(), getIdUsuarioLogado());
        }
    }

    public IServerContext getMenuContext() {
        return IServerContext.getContextFromRequest(this.getRequest(), singularServerConfiguration.getContexts());
    }

    protected List<ProcessGroupEntity> getCategorias() {
        final ProcessGroupEntity categoriaSelecionada = SingularSession.get().getCategoriaSelecionada();
        if (categoriaSelecionada == null) {
            return categorias;
        } else {
            return Collections.singletonList(categoriaSelecionada);
        }
    }

    protected List<ProcessGroupEntity> buscarCategorias() {
        return petitionService.listAllProcessGroups();
    }

    protected void buildMenuGroup(MetronicMenu menu, ProcessGroupEntity processGroup) {
        for (MenuGroup menuGroup : getMenuSessionConfig().getMenusPorCategoria(processGroup)) {
            List<MenuItemConfig> subMenus;
            if (menuGroup.getItemBoxes() == null) {
                subMenus = buildDefaultSubMenus(menuGroup, processGroup);
            } else {
                subMenus = buildSubMenus(menuGroup, processGroup);
            }

            if (!subMenus.isEmpty()) {
                buildMenus(menu, menuGroup, processGroup, subMenus);
            }
        }
    }

    protected List<MenuItemConfig> buildDefaultSubMenus(MenuGroup menuGroup, ProcessGroupEntity processGroup) {
        return Collections.emptyList();
    }

    private void buildMenus(MetronicMenu menu, MenuGroup menuGroup,
                            ProcessGroupEntity processGroup, List<MenuItemConfig> subMenus) {
        MetronicMenuGroup group = new MetronicMenuGroup(Icone.LAYERS, menuGroup.getLabel());
        menu.addItem(group);
        final List<Pair<Component, ISupplier<String>>> itens = new ArrayList<>();

        for (MenuItemConfig t : subMenus) {
            PageParameters pageParameters = new PageParameters();
            pageParameters.add(PROCESS_GROUP_PARAM_NAME, processGroup.getCod());
            pageParameters.add(MENU_PARAM_NAME, menuGroup.getLabel());
            pageParameters.add(ITEM_PARAM_NAME, t.name);

            MetronicMenuItem i = new MetronicMenuItem(t.icon, t.name, t.pageClass, t.page, pageParameters);
            group.addItem(i);
            itens.add(Pair.of(i.getHelper(), t.counterSupplier));
        }
        menu.add(new AddContadoresBehaviour(itens));
    }

    private List<MenuItemConfig> buildSubMenus(MenuGroup menuGroup, ProcessGroupEntity processGroup) {

        List<String> siglas = menuGroup.getProcesses().stream()
                .map(ProcessDTO::getAbbreviation)
                .collect(Collectors.toList());

        List<String> tipos = menuGroup.getProcesses().stream()
                .map(ProcessDTO::getFormName)
                .filter(s -> s!=null)
                .collect(Collectors.toList());

        List<MenuItemConfig> configs = new ArrayList<>();

        for (ItemBox itemBoxDTO : menuGroup.getItemBoxes()) {
            final ISupplier<String> countSupplier = createCountSupplier(itemBoxDTO, siglas, processGroup, tipos, menuGroup.getForms());
            configs.add(MenuItemConfig.of(getBoxPageClass(), itemBoxDTO.getName(), itemBoxDTO.getIcone(), countSupplier));

        }

        return configs;
    }

    protected ISupplier<String> createCountSupplier(ItemBox itemBoxDTO, List<String> siglas, ProcessGroupEntity processGroup, List<String> tipos, List<FormDTO> menuFormTypes) {
        return () -> {
            final String connectionURL = processGroup.getConnectionURL();
            final String url           = connectionURL + PATH_BOX_SEARCH + itemBoxDTO.getCountEndpoint();
            long         qtd;
            try {
                QuickFilter filter = new QuickFilter()
                        .withProcessesAbbreviation(siglas)
                        .withTypesNames(tipos.isEmpty()? menuFormTypes.stream().map(FormDTO::getName).collect(Collectors.toList()) : tipos)
                        .withRascunho(itemBoxDTO.isShowDraft())
                        .withEndedTasks(itemBoxDTO.getEndedTasks())
                        .withIdUsuarioLogado(getIdUsuarioLogado());
                qtd = new RestTemplate().postForObject(url, filter, Long.class);
            } catch (Exception e) {
                LOGGER.error("Erro ao acessar serviço: " + url, e);
                qtd = 0;
            }

            return String.valueOf(qtd);
        };
    }

    protected String getIdPessoa() {
        return getIdUsuarioLogado();
    }

    protected String getIdUsuarioLogado() {
        SingularUserDetails singularUserDetails = SingularSession.get().getUserDetails();
        return Optional.ofNullable(singularUserDetails)
                .map(SingularUserDetails::getUsername)
                .orElse(null);
    }

    public Class<? extends WebPage> getBoxPageClass(){
        return boxPageClass;
    }

    protected static class MenuItemConfig {
        public IRequestablePage                  page;
        public String                            name;
        public Class<? extends IRequestablePage> pageClass;
        public Icone                             icon;
        public ISupplier<String>                 counterSupplier;

        public static MenuItemConfig of(Class<? extends IRequestablePage> pageClass, String name, Icone icon, ISupplier<String> counterSupplier) {
            MenuItemConfig mic = new MenuItemConfig();
            mic.pageClass = pageClass;
            mic.name = name;
            mic.icon = icon;
            mic.counterSupplier = counterSupplier;
            return mic;
        }

        static MenuItemConfig of(IRequestablePage page, String name, Icone icon, ISupplier<String> counterSupplier) {
            MenuItemConfig mic = new MenuItemConfig();
            mic.page = page;
            mic.name = name;
            mic.icon = icon;
            mic.counterSupplier = counterSupplier;
            return mic;
        }
    }

}
