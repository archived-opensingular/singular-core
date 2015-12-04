package br.net.mirante.singular.view.template;

import br.net.mirante.singular.showcase.ShowCaseTable;
import br.net.mirante.singular.util.wicket.menu.MetronicMenu;
import br.net.mirante.singular.util.wicket.menu.MetronicMenuGroup;
import br.net.mirante.singular.util.wicket.menu.MetronicMenuItem;
import br.net.mirante.singular.view.page.form.crud.CrudPage;
import br.net.mirante.singular.view.page.showcase.ShowCasePage;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class Menu extends Panel {

    /**
	 * 
	 */
	private static final long serialVersionUID = 7622791136418841943L;

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
            menu.addItem(showCaseGroup);
        }));

        return menu;
    }
}
