package br.net.mirante.singular.view.template;

import br.net.mirante.singular.showcase.ShowCaseTable;
import br.net.mirante.singular.util.wicket.menu.MetronicMenu;
import br.net.mirante.singular.util.wicket.menu.MetronicMenuGroup;
import br.net.mirante.singular.util.wicket.menu.MetronicMenuItem;
import br.net.mirante.singular.util.wicket.resource.Icone;
import br.net.mirante.singular.view.page.form.ListPage;
import br.net.mirante.singular.view.page.form.crud.CrudPage;
import br.net.mirante.singular.view.page.showcase.ComponentPage;
import br.net.mirante.singular.wicket.UIAdminWicketFilterContext;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import javax.inject.Inject;

public class Menu extends Panel {

    /**
     *
     */
    private static final long serialVersionUID = 7622791136418841943L;

    @Inject
    private UIAdminWicketFilterContext uiAdminWicketFilterContext;

    public Menu(String id) {
        super(id);
        add(buildMenu());
    }

    private MetronicMenu buildMenu() {
        MetronicMenu menu = new MetronicMenu("menu");

        menu.addItem(new MetronicMenuItem(Icone.HOME, "Início", uiAdminWicketFilterContext.getRelativeContext().concat("form/list")));
        menu.addItem(new MetronicMenuItem(Icone.ROCKET, "Demo", uiAdminWicketFilterContext.getRelativeContext().concat("form/crud")));

        new ShowCaseTable().getGroups().forEach((group -> {
            MetronicMenuGroup showCaseGroup = new MetronicMenuGroup(group.getIcon(), group.getGroupName());
            group.getItens().forEach(item -> {
                showCaseGroup.addItem(
                        new MetronicMenuItem(null, item.getComponentName(), ComponentPage.class,
                                new PageParameters().add("cn", item.getComponentName().toLowerCase())));
            });
            menu.addItem(showCaseGroup);
        }));

        return menu;
    }
}
