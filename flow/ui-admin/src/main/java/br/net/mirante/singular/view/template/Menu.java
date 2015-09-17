package br.net.mirante.singular.view.template;

import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;

import br.net.mirante.singular.dao.MenuItemDTO;
import br.net.mirante.singular.flow.core.dto.IMenuItemDTO;
import br.net.mirante.singular.service.MenuService;
import br.net.mirante.singular.util.wicket.util.WicketUtils;
import br.net.mirante.singular.view.page.dashboard.DashboardPage;
import br.net.mirante.singular.wicket.UIAdminWicketFilterContext;

public class Menu extends Panel {

    @Inject
    private UIAdminWicketFilterContext uiAdminWicketFilterContext;

    @Inject
    private MenuService menuService;

    public Menu(String id) {
        super(id);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        queue(new WebMarkupContainer("dashboard").add(
                WicketUtils.$b.attr("href", uiAdminWicketFilterContext.getRelativeContext().concat("dashboard"))));
        queue(new WebMarkupContainer("process").add(
                WicketUtils.$b.attr("href", uiAdminWicketFilterContext.getRelativeContext().concat("process"))));
        queue(mountCategories());
    }

    private MarkupContainer mountCategories() {
        return new Fragment("categoriesContainer", "categoriesFragment", this).add(createCategoriesMenu());
    }

    private RepeatingView createCategoriesMenu() {
        final List<MenuItemDTO> categories = menuService.retrieveAllCategories();
        final RepeatingView categoriesMenu = new RepeatingView("categories");
        for (IMenuItemDTO item : categories) {
            final WebMarkupContainer categoryMenu = new WebMarkupContainer(categoriesMenu.newChildId());
            categoryMenu.setOutputMarkupId(true);
            categoryMenu.setMarkupId(String.format("_categoryMenu_%d", item.getId()));
            categoryMenu.add(new Label("categoryLabel", item.getName())).add(createDefinitionsMenu(item.getItens()));
            categoriesMenu.add(categoryMenu);
        }
        return categoriesMenu;
    }

    private RepeatingView createDefinitionsMenu(List<IMenuItemDTO> definitions) {
        final RepeatingView definitionsMenu = new RepeatingView("definitions");
        for (IMenuItemDTO item : definitions) {
            final WebMarkupContainer definitionMenu = new WebMarkupContainer(definitionsMenu.newChildId());
            definitionMenu.setOutputMarkupId(true);
            definitionMenu.setMarkupId(String.format("_definitionMenu_%d", item.getId()));
            definitionMenu.add(new WebMarkupContainer("link")
                    .add(new Label("counter", item.getCounter()).setVisible(false))
                    .add(new Label("definitionLabel", item.getName()))
                    .add(WicketUtils.$b.attr("href", (item.getCode() == null ? "#"
                            : uiAdminWicketFilterContext.getRelativeContext().concat("dashboard")
                            .concat("?").concat(DashboardPage.PROCESS_DEFINITION_COD_PARAM)
                            .concat("=").concat(item.getCode())))));
            definitionsMenu.add(definitionMenu);
        }
        return definitionsMenu;
    }
}
