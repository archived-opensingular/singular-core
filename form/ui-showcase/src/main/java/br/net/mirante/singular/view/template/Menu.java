package br.net.mirante.singular.view.template;

import br.net.mirante.singular.showcase.ShowCaseTable;
import br.net.mirante.singular.util.wicket.menu.MetronicMenu;
import br.net.mirante.singular.util.wicket.menu.MetronicMenuGroup;
import br.net.mirante.singular.util.wicket.menu.MetronicMenuItem;
import br.net.mirante.singular.view.page.form.crud.CrudPage;
import br.net.mirante.singular.view.page.showcase.ShowCasePage;
import br.net.mirante.singular.wicket.UIAdminWicketFilterContext;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import javax.inject.Inject;

@SuppressWarnings("serial")
public class Menu extends Panel {

    @Inject
    private UIAdminWicketFilterContext uiAdminWicketFilterContext;

    public Menu(String id) {
        super(id);
        add(buildMenu());
    }

    private MetronicMenu buildMenu() {
        MetronicMenu menu = new MetronicMenu("menu");

        menu.addItem(new MetronicMenuItem("icon-home", "Início").setResponsePageClass(CrudPage.class));
        menu.addItem(new MetronicMenuItem("icon-rocket", "Demo").setResponsePageClass(CrudPage.class));

        new ShowCaseTable().getGroups().forEach((group -> {
            MetronicMenuGroup showCaseGroup = new MetronicMenuGroup("icon-puzzle", group.getGroupName());
            group.getItens().forEach(item -> {
                showCaseGroup.addItem(new MetronicMenuItem(item.getComponentName())
                        .setResponsePageClass(ShowCasePage.class)
                        .setParameters(new PageParameters().add("ch", item.getComponentName().hashCode())));
            });
            menu.addGroup(showCaseGroup);
        }));

        return menu;
    }
}
