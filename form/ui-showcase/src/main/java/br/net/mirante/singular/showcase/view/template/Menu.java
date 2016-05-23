/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.view.template;

import java.util.Collection;

import javax.inject.Inject;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;

import br.net.mirante.singular.showcase.component.ShowCaseTable;
import br.net.mirante.singular.showcase.view.page.ComponentPage;
import br.net.mirante.singular.showcase.view.page.form.ListPage;
import br.net.mirante.singular.showcase.view.page.form.crud.CrudPage;
import br.net.mirante.singular.showcase.view.page.prototype.PrototypeListPage;
import br.net.mirante.singular.util.wicket.menu.MetronicMenu;
import br.net.mirante.singular.util.wicket.menu.MetronicMenuGroup;
import br.net.mirante.singular.util.wicket.menu.MetronicMenuItem;
import br.net.mirante.singular.util.wicket.resource.Icone;

public class Menu extends Panel {

    private static final long serialVersionUID = 7622791136418841943L;

    @Inject
    private ShowCaseTable showCaseTable;

    public Menu(String id) {
        super(id);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(buildMenu());
    }

    private MetronicMenu buildMenu() {
        MetronicMenu menu = new MetronicMenu("menu");

        final StringValue tipoValue = getPage().getPageParameters().get(ListPage.PARAM_TIPO);

        if (tipoValue.isNull() || ListPage.Tipo.FORM.toString().equals(tipoValue.toString())) {
            menu.addItem(new MetronicMenuItem(Icone.ROCKET, "Demo", CrudPage.class));
            menu.addItem(new MetronicMenuItem(Icone.PENCIL, "Protótipo", PrototypeListPage.class));
        }

        final Collection<ShowCaseTable.ShowCaseGroup> groups = showCaseTable.getGroups(tipoValue);

        groups.forEach(group -> {
            final MetronicMenuGroup showCaseGroup = new MetronicMenuGroup(group.getIcon(), group.getGroupName());
            final Collection<ShowCaseTable.ShowCaseItem> itens = group.getItens();
            itens.forEach(item -> {
                final PageParameters pageParameters = new PageParameters();
                final PageParameters old = getPage().getPageParameters();
                final StringValue tipo = old.get(ListPage.PARAM_TIPO);
                if (!tipo.isNull()) {
                    pageParameters.add(ListPage.PARAM_TIPO, tipo);
                }
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
