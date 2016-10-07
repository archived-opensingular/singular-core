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

package org.opensingular.server.commons.wicket.view.template;

import org.opensingular.lib.commons.lambda.IFunction;
import org.opensingular.lib.commons.util.Loggable;
import org.opensingular.server.commons.wicket.SingularSession;
import org.opensingular.server.commons.wicket.view.SingularToastrHelper;
import de.alpharogroup.wicket.js.addon.toastr.ToastrType;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;

import static org.opensingular.server.commons.wicket.view.template.Menu.MENU_CACHE;
import static org.opensingular.lib.wicket.util.util.WicketUtils.$b;

public abstract class Content extends Panel implements Loggable {

    private boolean withBreadcrumb;
    private boolean withInfoLink;
    private RepeatingView toolbar;

    protected WebMarkupContainer pageHead   = new WebMarkupContainer("pageHead");
    protected WebMarkupContainer breadcrumb = new WebMarkupContainer("breadcrumb");

    public Content(String id) {
        this(id, false, false);
    }

    public Content(String id, boolean withInfoLink, boolean withBreadcrumb) {
        super(id);
        this.withBreadcrumb = withBreadcrumb;
        this.withInfoLink = withInfoLink;
    }

    public Content addSideBar() {
        return this;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(pageHead);

        pageHead.add(new Label("contentTitle", getContentTitleModel()));
        pageHead.add(new Label("contentSubtitle", getContentSubtitleModel()));
        pageHead.add(toolbar = new RepeatingView("toolbarItems"));

        add(breadcrumb);

        breadcrumb.add(new WebMarkupContainer("breadcrumbDashboard").add($b.attr("href", "null null")));
        breadcrumb.add(getBreadcrumbLinks("_BreadcrumbLinks"));

        if (!withBreadcrumb) {
            breadcrumb.add(new AttributeAppender("class", "hide", " "));
        }

    }

    protected MenuSessionConfig getMenuSessionConfig() {
        return (MenuSessionConfig) SingularSession.get().getAttribute(MENU_CACHE);
    }

    protected StringResourceModel getMessage(String prop) {
        return new StringResourceModel(prop.trim(), this, null);
    }

    public MarkupContainer addToolbarItem(IFunction<String, Component> toolbarItem) {
        return toolbar.add(toolbarItem.apply(toolbar.newChildId()));
    }

    public SingularSession getPetSession() {
        return SingularSession.get();
    }

    public void addToastrSuccessMessage(String messageKey, String... args) {
        new SingularToastrHelper(this).
                addToastrMessage(ToastrType.SUCCESS, messageKey, args);
    }

    public void addToastrErrorMessage(String messageKey, String... args) {
        new SingularToastrHelper(this).
                addToastrMessage(ToastrType.ERROR, messageKey, args);
    }

    public void addToastrWarningMessage(String messageKey, String... args) {
        new SingularToastrHelper(this).
                addToastrMessage(ToastrType.WARNING, messageKey, args);
    }

    public void addToastrInfoMessage(String messageKey, String... args) {
        new SingularToastrHelper(this).
                addToastrMessage(ToastrType.INFO, messageKey, args);
    }

    public void addToastrSuccessMessageWorklist(String messageKey, String... args) {
        new SingularToastrHelper(this).
                addToastrMessageWorklist(ToastrType.SUCCESS, messageKey, args);
    }

    public void addToastrErrorMessageWorklist(String messageKey, String... args) {
        new SingularToastrHelper(this).
                addToastrMessageWorklist(ToastrType.ERROR, messageKey, args);
    }

    public void addToastrWarningMessageWorklist(String messageKey, String... args) {
        new SingularToastrHelper(this).
                addToastrMessageWorklist(ToastrType.WARNING, messageKey, args);
    }

    protected void addToastrInfoMessageWorklist(String messageKey, String... args) {
        new SingularToastrHelper(this).
                addToastrMessageWorklist(ToastrType.INFO, messageKey, args);
    }

    protected WebMarkupContainer getBreadcrumbLinks(String id) {
        return new WebMarkupContainer(id);
    }

    protected WebMarkupContainer getInfoLink(String id) {
        return new WebMarkupContainer(id);
    }

    protected abstract IModel<?> getContentTitleModel();

    protected abstract IModel<?> getContentSubtitleModel();
}
