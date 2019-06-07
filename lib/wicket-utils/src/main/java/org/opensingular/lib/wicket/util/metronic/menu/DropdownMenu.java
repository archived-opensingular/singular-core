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

package org.opensingular.lib.wicket.util.metronic.menu;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.Model;
import org.opensingular.lib.commons.lambda.IFunction;
import org.opensingular.lib.commons.ui.Icon;
import org.opensingular.lib.wicket.util.util.Shortcuts;

public class DropdownMenu extends Panel {
    private Icon          icon;
    private String        fixedLabel = "Novo";
    private RepeatingView menus      = new RepeatingView("menus");

    public DropdownMenu(String id) {
        super(id);
    }

    public DropdownMenu(String id, String fixedLabel) {
        super(id);
        this.fixedLabel = fixedLabel;
    }

    public DropdownMenu(String id, String fixedLabel, Icon icon) {
        this(id, fixedLabel);
        this.icon = icon;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(buildIcon());
        add(buildLabel());
        add(menus);
    }

    private WebMarkupContainer buildIcon() {
        WebMarkupContainer iconContainer = new WebMarkupContainer("icon");
        if (icon != null) {
            iconContainer.add(Shortcuts.$b.classAppender(icon.getCssClass()));
        } else {
            iconContainer.setVisible(false);
        }
        return iconContainer;
    }

    private Component buildLabel() {
        return new Label("label", new Model<String>() {
            @Override
            public String getObject() {
                return getLabel();
            }
        });
    }

    public <T> void adicionarMenu(IFunction<String, Link<T>> funcaoConstrutora) {
        Component item = new WebMarkupContainer(menus.newChildId())
                .add(funcaoConstrutora.apply("link"));
        menus.add(item);
    }

    public String getLabel() {
        return fixedLabel;
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        menus.replaceWith((menus = new RepeatingView("menus")));
        this.setVisible(menus.size() > 0);
    }
}
