package org.opensingular.form.report;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.jetbrains.annotations.NotNull;
import org.opensingular.form.SInstance;
import org.opensingular.form.SType;
import org.opensingular.lib.commons.lambda.ISupplier;
import org.opensingular.lib.wicket.util.menu.AjaxMenuItem;
import org.opensingular.lib.wicket.util.menu.MetronicMenu;
import org.opensingular.lib.wicket.util.resource.Icon;
import org.opensingular.lib.wicket.util.template.admin.SingularAdminTemplate;

import java.io.Serializable;

/**
 * A Box panel to show reports grouped by menus
 */
public abstract class SingularReportPage extends SingularAdminTemplate {
    private MetronicMenu menu;
    private Component body;

    @Override
    protected void onInitialize() {
        super.onInitialize();
        addBody();
    }

    private void addBody() {
        body = new WebMarkupContainer("body");
        add(body);
    }

    @NotNull
    @Override
    protected WebMarkupContainer buildPageMenu(String id) {
        menu = new MetronicMenu("menu");
        configureMenu(menu);
        return menu;
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

    protected abstract void configureMenu(MetronicMenu menu);

    protected class ReportAjaxMenuItem<E extends Serializable, T extends SType<I>, I extends SInstance> extends AjaxMenuItem {
        private final ISupplier<SingularFormReport<E, T, I>> singularFormReport;

        public ReportAjaxMenuItem(Icon icon, String title, ISupplier<SingularFormReport<E, T, I>> singularFormReport) {
            super(icon, title);
            this.singularFormReport = singularFormReport;
        }

        @Override
        protected void onAjax(AjaxRequestTarget target) {
            body = new SingularFormReportPanel<>("body", singularFormReport.get());
            SingularReportPage.this.replace(body);
            target.add(body, menu);
        }
    }
}