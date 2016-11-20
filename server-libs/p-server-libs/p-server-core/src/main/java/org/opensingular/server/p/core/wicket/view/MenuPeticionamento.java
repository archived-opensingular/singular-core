package org.opensingular.server.p.core.wicket.view;

import org.opensingular.flow.persistence.entity.ProcessGroupEntity;
import org.opensingular.lib.wicket.util.menu.MetronicMenu;
import org.opensingular.lib.wicket.util.resource.Icone;
import org.opensingular.server.commons.config.IServerContext;
import org.opensingular.server.commons.persistence.entity.form.PetitionEntity;
import org.opensingular.server.commons.persistence.filter.QuickFilter;
import org.opensingular.server.commons.service.PetitionService;
import org.opensingular.server.commons.service.dto.MenuGroup;
import org.opensingular.server.commons.wicket.view.template.Menu;
import org.opensingular.server.p.commons.config.PServerContext;
import org.opensingular.server.p.core.wicket.acompanhamento.AcompanhamentoPage;
import org.opensingular.server.p.core.wicket.rascunho.RascunhoPage;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class MenuPeticionamento extends Menu {

    private static final long serialVersionUID = 7622791136418841943L;

    @Inject
    private PetitionService<PetitionEntity> peticaoService;

    private MetronicMenu menu;

    public MenuPeticionamento(String id) {
        super(id);
    }

    @Override
    protected void onBeforeRender() {
        replace(buildMenu());
        super.onBeforeRender();
    }

    @Override
    protected MetronicMenu buildMenu() {
        loadMenuGroups();

        menu = new MetronicMenu("menu");

        buildMenuSelecao();

        getCategorias().forEach((processGroup) -> buildMenuGroup(menu, processGroup));

        return menu;
    }

    private void buildMenuSelecao() {
        SelecaoMenuItem selecaoMenuItem = new SelecaoMenuItem(categorias);
        menu.addItem(selecaoMenuItem);
    }

    @Override
    protected List<MenuItemConfig> buildDefaultSubMenus(MenuGroup menuGroup, ProcessGroupEntity processGroup) {
        List<MenuItemConfig> defaultMenus = new ArrayList<>();

        QuickFilter quickFilterRascunho = new QuickFilter();
        quickFilterRascunho.withRascunho(true);
        defaultMenus.add(MenuItemConfig.of(RascunhoPage.class, "Rascunho", Icone.DOCS, () -> String.valueOf(peticaoService.countQuickSearch(quickFilterRascunho))));

        QuickFilter quickFilterAcompanhamento = new QuickFilter();
        quickFilterAcompanhamento.withRascunho(false);
        defaultMenus.add(MenuItemConfig.of(AcompanhamentoPage.class, "Acompanhamento", Icone.CLOCK, () -> String.valueOf(peticaoService.countQuickSearch(quickFilterAcompanhamento))));

        return defaultMenus;
    }

    @Override
    public IServerContext getMenuContext() {
        return PServerContext.PETITION;
    }
}
