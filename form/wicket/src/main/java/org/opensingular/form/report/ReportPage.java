/*
 *
 *  * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.opensingular.form.report;

import de.alpharogroup.wicket.js.addon.toastr.ToastrType;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.Session;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.lang.Objects;
import org.opensingular.form.report.extension.ReportMenuExtension;
import org.opensingular.lib.commons.extension.SingularExtensionUtil;
import org.opensingular.lib.commons.report.SingularReport;
import org.opensingular.lib.commons.ui.Icon;
import org.opensingular.lib.commons.util.Loggable;
import org.opensingular.lib.wicket.util.menu.MetronicMenu;
import org.opensingular.lib.wicket.util.menu.MetronicMenuGroup;
import org.opensingular.lib.wicket.util.menu.MetronicMenuItem;
import org.opensingular.lib.wicket.util.template.admin.SingularAdminTemplate;
import org.opensingular.lib.wicket.util.toastr.ToastrHelper;
import org.opensingular.lib.wicket.views.SingularReportPanel;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.List;

/**
 * A Box panel to show reports grouped by menus
 */
public abstract class ReportPage extends SingularAdminTemplate {
    public static final String IDENTITY_PARAM = "identity";

    private MetronicMenu   menu;
    private Component      body;
    private String         identity;
    private SingularReport activeReport;

    public ReportPage(PageParameters parameters) {
        super(parameters);
        if (parameters == null) {
            return;
        }
        this.identity = parameters.get(IDENTITY_PARAM).toString(null);
        Serializable successMessage = Session.get().getAttribute(successMessageAttribute(identity));
        if (successMessage != null) {
            new ToastrHelper(this).addToastrMessage(ToastrType.SUCCESS, (String) successMessage);
            Session.get().removeAttribute(successMessageAttribute(identity));
        }
    }

    @Nonnull
    private static String successMessageAttribute(String identity) {
        return "message_" + Objects.defaultIfNull(identity, "empty");
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        ReportMenuBuilder reportMenuBuilder = new ReportMenuBuilder();
        configureMenu(reportMenuBuilder);
        configureExtensionButton(reportMenuBuilder);
        addBody();
    }

    private void addBody() {
        if (activeReport != null) {
            body = new SingularReportPanel("body", activeReport);
        }
        if (body == null) {
            body = new WebMarkupContainer("body");
        }
        add(body);
    }

    @Nonnull
    @Override
    protected WebMarkupContainer buildPageMenu(String id) {
        return menu = new MetronicMenu("menu");
    }

    private void configureExtensionButton(ReportMenuBuilder reportMenuBuilder) {
        List<ReportMenuExtension> menuExtensions = SingularExtensionUtil.get().findExtensionsByClass(ReportMenuExtension.class);
        for (ReportMenuExtension menuExtension : menuExtensions) {
            menuExtension.configure(reportMenuBuilder);
        }
    }

    @Override
    protected IModel<String> getContentTitle() {
        return new Model<>();
    }

    @Override
    protected IModel<String> getContentSubtitle() {
        return new Model<>();
    }

    @Override
    protected boolean isWithMenu() {
        return true;
    }

    protected abstract void configureMenu(ReportMenuBuilder menu);

    protected class ReportAjaxMenuItem extends MetronicMenuItem {
        private final SingularReport report;

        ReportAjaxMenuItem(Icon icon, String title, SingularReport supplier) {
            super(icon, title, ReportPage.this.getClass(), new PageParameters().set("identity", supplier.getIdentity()));
            this.report = supplier;
        }

        @Override
        protected boolean isActive() {
            return report.getIdentity().equals(identity);
        }
    }

    public class ReportMenuBuilder implements Loggable {
        public ReportMenuGroupBuilder addGroup(Icon icon, String title) {
            MetronicMenuGroup group = new MetronicMenuGroup(icon, title);
            menu.addItem(group);
            return new ReportMenuGroupBuilder(group);
        }

        public ReportMenuBuilder addItem(Icon icon, String title, SingularReport report) {
            try {
                menu.addItem(newMenuItem(icon, title, report));
            } catch (Exception ex) {
                getLogger().error("Não foi possivel criar o menu, todos os construtores foram sobreescritos?", ex);
            }
            return this;
        }
    }

    public class ReportMenuGroupBuilder implements Loggable {
        private final MetronicMenuGroup group;

        ReportMenuGroupBuilder(MetronicMenuGroup group) {
            this.group = group;
        }

        public ReportMenuGroupBuilder addItem(Icon icon, String title, SingularReport report) {
            try {
                group.addItem(newMenuItem(icon, title, report));
            } catch (Exception ex) {
                getLogger().error("Não foi possivel criar o menu, todos os construtores foram sobreescritos?", ex);
            }
            return this;
        }

        public ReportMenuGroupBuilder addGroup(Icon icon, String title) {
            MetronicMenuGroup newGroup = new MetronicMenuGroup(icon, title);
            group.addItem(newGroup);
            return new ReportMenuGroupBuilder(newGroup);
        }
    }

    private ReportAjaxMenuItem newMenuItem(Icon icon, String title, SingularReport report) {
        ReportAjaxMenuItem newAjaxItem = new ReportAjaxMenuItem(icon, title, report);
        if (newAjaxItem.isActive()) {
            activeReport = report;
        }
        return newAjaxItem;
    }

    public static void setAsResponsePageWithMessage(Component c, String message, String identity) {
        if (StringUtils.isNotBlank(message)) {
            Session.get().setAttribute(successMessageAttribute(identity), message);
        }
        PageParameters params = new PageParameters();
        if (identity != null) {
            params.add(IDENTITY_PARAM, identity);
        }
        RequestCycle.get().setResponsePage(c.getPage().getClass(), params);
    }
}