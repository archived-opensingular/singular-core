package br.net.mirante.singular.server.commons.wicket.view.template;

import static br.net.mirante.singular.server.commons.service.IServerMetadataREST.PATH_BOX_SEARCH;
import static br.net.mirante.singular.server.commons.util.Parameters.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import br.net.mirante.singular.commons.lambda.ISupplier;
import br.net.mirante.singular.persistence.entity.ProcessGroupEntity;
import br.net.mirante.singular.server.commons.persistence.filter.QuickFilter;
import br.net.mirante.singular.server.commons.service.PetitionService;
import br.net.mirante.singular.server.commons.service.dto.ItemBox;
import br.net.mirante.singular.server.commons.service.dto.MenuGroup;
import br.net.mirante.singular.server.commons.service.dto.ProcessDTO;
import br.net.mirante.singular.server.commons.wicket.SingularApplication;
import br.net.mirante.singular.server.commons.wicket.SingularSession;
import br.net.mirante.singular.util.wicket.menu.MetronicMenu;
import br.net.mirante.singular.util.wicket.menu.MetronicMenuGroup;
import br.net.mirante.singular.util.wicket.menu.MetronicMenuItem;
import br.net.mirante.singular.util.wicket.resource.Icone;

public class Menu extends Panel {

    /**
     *
     */
    private static final long serialVersionUID = 7622791136418841943L;

    public static final String MENU_CACHE = "MENU_CACHE";

    protected static final Logger LOGGER = LoggerFactory.getLogger(Menu.class);

    protected List<ProcessGroupEntity> categorias;

    @SuppressWarnings("rawtypes")
    @Inject
    protected PetitionService petitionService;

    public Menu(String id) {
        super(id);
        add(buildMenu());
    }

    protected MetronicMenu buildMenu() {
        MetronicMenu menu = new MetronicMenu("menu");

        menu.addItem(new MetronicMenuItem(Icone.HOME, "Início", SingularApplication.get().getHomePage()));

        return menu;
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
            final String        encoding = "UTF-8";
            final StringBuilder json     = new StringBuilder();
            json.append("{");
            for (int i = 0; i < itens.size(); i++) {
                json.append("\"item").append(i).append("\"").append(":").append(itens.get(i).getRight().get());
                if (i + 1 != itens.size()) {
                    json.append(",");
                }
            }
            json.append("}");
            RequestCycle.get().scheduleRequestHandlerAfterCurrent(new TextRequestHandler(type, encoding, json.toString()));
        }
    }

    protected MenuSessionConfig getMenuSessionConfig() {
        final SingularSession session = SingularSession.get();
        MenuSessionConfig menuSessionConfig = (MenuSessionConfig) session.getAttribute(MENU_CACHE);
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
            menuSessionConfig.initialize(categorias, getMenuContext());
        }
    }

    public String getMenuContext() {
        return "";
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
        return petitionService.listarTodosGruposProcesso();
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
                .collect(Collectors.toList());

        List<MenuItemConfig> configs = new ArrayList<>();

        for (ItemBox itemBoxDTO : menuGroup.getItemBoxes()) {
            final ISupplier<String> countSupplier = createCountSupplier(itemBoxDTO, siglas, processGroup, tipos);
            configs.add(MenuItemConfig.of(getBoxPageClass(), itemBoxDTO.getName(), itemBoxDTO.getIcone(), countSupplier));

        }

        return configs;
    }

    private ISupplier<String> createCountSupplier(ItemBox itemBoxDTO, List<String> siglas, ProcessGroupEntity processGroup, List<String> tipos) {
        return () -> {
            final String connectionURL = processGroup.getConnectionURL();
            final String url           = connectionURL + PATH_BOX_SEARCH + itemBoxDTO.getCountEndpoint();
            long         qtd;
            try {
                QuickFilter filter = new QuickFilter()
                        .withProcessesAbbreviation(siglas)
                        .withTypesNames(tipos)
                        .withRascunho(itemBoxDTO.isShowDraft())
                        .withIdUsuarioLogado(getIdUsuarioLogado());
                qtd = new RestTemplate().postForObject(url, filter, Long.class);
            } catch (Exception e) {
                LOGGER.error("Erro ao acessar serviço: " + url, e);
                qtd = 0;
            }

            return String.valueOf(qtd);
        };
    }

    protected String getIdUsuarioLogado() {
        return null;
    }

    public Class<? extends WebPage> getBoxPageClass() {
        return null;
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
