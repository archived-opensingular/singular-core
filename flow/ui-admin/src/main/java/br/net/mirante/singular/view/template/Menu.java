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
import br.net.mirante.singular.service.MenuService;
import br.net.mirante.singular.util.wicket.util.WicketUtils;

public class Menu extends Panel {

    @Inject
    private MenuService menuService;

    @Inject
    private String adminWicketFilterContext;

    public Menu(String id) {
        super(id);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        queue(new WebMarkupContainer("dashboard").add(
                WicketUtils.$b.attr("href", adminWicketFilterContext.concat("dashboard"))));
        queue(new WebMarkupContainer("process").add(
                WicketUtils.$b.attr("href", adminWicketFilterContext.concat("process"))));
        queue(mountCategories());
    }

    private MarkupContainer mountCategories() {
        return new Fragment("categoriesContainer", "categoriesFragment", this).add(createCategoriesMenu());
    }

    private RepeatingView createCategoriesMenu() {
        final List<MenuItemDTO> categories = menuService.retrieveAllCategories();
        final RepeatingView categoriesMenu = new RepeatingView("categories");
        for (MenuItemDTO item : categories) {
            final WebMarkupContainer categoryMenu = new WebMarkupContainer(categoriesMenu.newChildId());
            categoryMenu.setOutputMarkupId(true);
            categoryMenu.setMarkupId(String.format("_categoryMenu_%d", item.getId()));
            categoryMenu.add(new Label("categoryLabel", item.getLabel())).add(createDefinitionsMenu(item.getItens()));
            categoriesMenu.add(categoryMenu);
        }
        return categoriesMenu;
    }

    private RepeatingView createDefinitionsMenu(List<MenuItemDTO> definitions) {
        final RepeatingView definitionsMenu = new RepeatingView("definitions");
        for (MenuItemDTO item : definitions) {
            final WebMarkupContainer definitionMenu = new WebMarkupContainer(definitionsMenu.newChildId());
            definitionMenu.setOutputMarkupId(true);
            definitionMenu.setMarkupId(String.format("_definitionMenu_%d", item.getId()));
            definitionMenu.add(new WebMarkupContainer("link")
                    .add(new Label("counter", item.getCounter()))
                    .add(new Label("definitionLabel", item.getLabel()))
                    .add(WicketUtils.$b.attr("href", item.getHref())));
            definitionsMenu.add(definitionMenu);
        }
        return definitionsMenu;
    }
}
