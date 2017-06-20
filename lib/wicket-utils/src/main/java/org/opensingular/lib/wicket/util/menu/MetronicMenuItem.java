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

package org.opensingular.lib.wicket.util.menu;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.Page;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.request.component.IRequestablePage;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.opensingular.lib.wicket.util.resource.Icon;

import java.util.regex.Pattern;

import static org.opensingular.lib.wicket.util.util.WicketUtils.$b;

public class MetronicMenuItem extends AbstractMenuItem {

    private WebMarkupContainer                menuItem;
    private IRequestablePage                  page;
    private PageParameters                    parameters;
    private Class<? extends IRequestablePage> responsePageClass;
    private String                            menuItemUrl;
    private String                            href;
    private String                            target;
    private WebMarkupContainer helper = new WebMarkupContainer("helper");

    public MetronicMenuItem(Icon icon, String title, Class<? extends IRequestablePage> responsePageClass,
                            PageParameters parameters) {
        this(icon, title, responsePageClass, null, parameters);
    }

    public MetronicMenuItem(Icon icon, String title, Class<? extends IRequestablePage> responsePageClass,
                            IRequestablePage page, PageParameters parameters) {
        this(icon, title);
        this.responsePageClass = responsePageClass;
        this.page = page;
        this.parameters = parameters;
        add(buildMenuItem());
    }

    public MetronicMenuItem(Icon icon, String title, Class<? extends IRequestablePage> responsePageClass) {
        this(icon, title, responsePageClass, null, null);
    }

    public MetronicMenuItem(Icon icon, String title, String href) {
        this(icon, title);
        this.href = href;
        add(buildMenuItem());
    }

    public MetronicMenuItem(Icon icon, String title, String href, String target) {
        this(icon, title);
        this.href = href;
        this.target = target;
        add(buildMenuItem());
    }

    public MetronicMenuItem(Icon icon, String title) {
        super("menu-item");
        this.icon = icon;
        this.title = title;
    }

    protected WebMarkupContainer buildMenuItem() {

        menuItem = new WebMarkupContainer("menu-item");

        MarkupContainer anchor;

        if (href != null) {
            anchor = new WebMarkupContainer("anchor");
            anchor.add($b.attr("href", href));
            if (target != null) {
                anchor.add($b.attr("target", target));
            }
            this.menuItemUrl = href;
        } else if (responsePageClass != null) {
            anchor = new BookmarkablePageLink("anchor", responsePageClass, parameters);
            menuItemUrl = anchor.urlFor((Class<Page>) responsePageClass, parameters).toString();
        } else if (page != null) {
            anchor = new Link("anchor") {
                @Override
                public void onClick() {
                    setResponsePage(page);
                }
            };
        } else {
            throw new WicketRuntimeException("É necessario informar o destino do item");
        }

        WebMarkupContainer iconMarkup = new WebMarkupContainer("icon");

        if (icon != null) {
            iconMarkup.add($b.classAppender(icon.getCssClass()));
        } else {
            iconMarkup.setVisible(false);
        }

        anchor.add(new Label("title", title));
        anchor.add(helper);
        anchor.add(iconMarkup);

        menuItem.add(anchor);
        return menuItem;

    }

    @Override
    protected boolean configureActiveItem() {
        if (menuItemUrl != null && isActive()) {
            menuItem.add($b.classAppender("active"));
            return true;
        }
        return false;
    }

    protected boolean isActive() {
        Pattern onlyLetters = Pattern.compile("[^a-zA-Z0-9]");
        String  url         = onlyLetters.matcher(getRequest().getUrl().toString()).replaceAll("");
        String  thisUrl     = onlyLetters.matcher(menuItemUrl).replaceAll("");
        return url.endsWith(thisUrl);
    }

    public WebMarkupContainer getHelper() {
        return helper;
    }

}