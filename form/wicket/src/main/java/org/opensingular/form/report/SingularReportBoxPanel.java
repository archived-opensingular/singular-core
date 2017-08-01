package org.opensingular.form.report;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.opensingular.form.SInstance;
import org.opensingular.form.SType;
import org.opensingular.lib.wicket.util.menu.AjaxMenuItem;
import org.opensingular.lib.wicket.util.menu.MetronicMenu;
import org.opensingular.lib.wicket.util.resource.Icon;

import java.io.Serializable;

/**
 * A Box panel to show reports grouped by menus
 */
public abstract class SingularReportBoxPanel extends Panel {

    private MetronicMenu menu;
    private Component body;

    public SingularReportBoxPanel(String id) {
        super(id);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        addMenu();
        addBody();
    }

    private void addBody() {
        body = new WebMarkupContainer("body");
        add(body);
    }

    private void addMenu() {
        menu = new MetronicMenu("menu");
        configureMenu(menu);
        add(menu);
    }

    protected abstract void configureMenu(MetronicMenu menu);

    protected class ReportAjaxMenuItem<E extends Serializable, T extends SType<I>, I extends SInstance> extends AjaxMenuItem {
        private final SingularFormReport<E, T, I> singularFormReport;

        public ReportAjaxMenuItem(Icon icon, String title, SingularFormReport<E, T, I> singularFormReport) {
            super(icon, title, menu);
            this.singularFormReport = singularFormReport;
        }

        @Override
        protected void onAjax(AjaxRequestTarget target) {
            body = new SingularFormReportPanel<>("body", singularFormReport);
            SingularReportBoxPanel.this.replace(body);
            target.add(SingularReportBoxPanel.this);
        }
    }

}