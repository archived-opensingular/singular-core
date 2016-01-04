package br.net.mirante.singular.showcase.view.template;

import br.net.mirante.singular.showcase.component.ShowCaseTable;
import br.net.mirante.singular.util.wicket.menu.MetronicMenu;
import br.net.mirante.singular.util.wicket.menu.MetronicMenuGroup;
import br.net.mirante.singular.util.wicket.menu.MetronicMenuItem;
import br.net.mirante.singular.util.wicket.resource.Icone;
import br.net.mirante.singular.showcase.view.page.showcase.ComponentPage;
import br.net.mirante.singular.showcase.wicket.UIAdminWicketFilterContext;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import javax.inject.Inject;
import java.util.Collection;

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

        final String relativeContext = uiAdminWicketFilterContext.getRelativeContext();

        menu.addItem(new MetronicMenuItem(Icone.HOME, "Início", relativeContext + "form/list"));
        menu.addItem(new MetronicMenuItem(Icone.ROCKET, "Demo", relativeContext + "form/crud"));

        final ShowCaseTable showCaseTable = new ShowCaseTable();
        final Collection<ShowCaseTable.ShowCaseGroup> groups = showCaseTable.getGroups();

        groups.forEach(group -> {
            final MetronicMenuGroup showCaseGroup = new MetronicMenuGroup(group.getIcon(), group.getGroupName());
            final Collection<ShowCaseTable.ShowCaseItem> itens = group.getItens();
            itens.forEach(item -> {
                final PageParameters pageParameters = new PageParameters();
                final String componentName = item.getComponentName();
                showCaseGroup.addItem(
                        new MetronicMenuItem(null, item.getComponentName(), ComponentPage.class,
                                pageParameters.add("cn", componentName.toLowerCase())));
            });
            menu.addItem(showCaseGroup);
        });

        return menu;
    }
}
