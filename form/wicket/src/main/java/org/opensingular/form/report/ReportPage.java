package org.opensingular.form.report;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.jetbrains.annotations.NotNull;
import org.opensingular.form.report.extension.ReportMenuExtension;
import org.opensingular.lib.commons.extension.SingularExtensionUtil;
import org.opensingular.lib.commons.lambda.ISupplier;
import org.opensingular.lib.commons.report.SingularReport;
import org.opensingular.lib.wicket.util.menu.AjaxMenuItem;
import org.opensingular.lib.wicket.util.menu.MetronicMenu;
import org.opensingular.lib.wicket.util.menu.MetronicMenuGroup;
import org.opensingular.lib.wicket.util.resource.Icon;
import org.opensingular.lib.wicket.util.template.admin.SingularAdminTemplate;
import org.opensingular.lib.wicket.views.SingularReportPanel;

import java.util.List;

/**
 * A Box panel to show reports grouped by menus
 */
public abstract class ReportPage extends SingularAdminTemplate {
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
        return menu = new MetronicMenu("menu");
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        rebuildMenu(null);
    }

    public void rebuildMenu(AjaxRequestTarget ajaxRequestTarget) {
        menu = (MetronicMenu) menu.replaceWith(new MetronicMenu("menu"));
        ReportMenuBuilder reportMenuBuilder = new ReportMenuBuilder();
        configureMenu(reportMenuBuilder);
        configureExtensionButton(reportMenuBuilder);
        if (ajaxRequestTarget != null) {
            ajaxRequestTarget.add(menu);
        }
    }

    public void hideBody(AjaxRequestTarget ajaxRequestTarget){
        this.body = body.replaceWith(new WebMarkupContainer("body"));
        ajaxRequestTarget.add(body);
    }

    private void configureExtensionButton(ReportMenuBuilder reportMenuBuilder) {
        List<ReportMenuExtension> menuExtensions = SingularExtensionUtil.get().findExtensionByClass(ReportMenuExtension.class);
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

    protected class ReportAjaxMenuItem extends AjaxMenuItem {
        private final ISupplier<SingularReport> reportSupplier;

        public ReportAjaxMenuItem(Icon icon, String title, ISupplier<SingularReport> reportSupplier) {
            super(icon, title);
            this.reportSupplier = reportSupplier;
        }

        @Override
        protected void onAjax(AjaxRequestTarget target) {
            body = new SingularReportPanel("body", reportSupplier);
            ReportPage.this.replace(body);
            target.add(body, menu);
        }
    }


    public class ReportMenuBuilder {
        public ReportMenuGroupBuilder addGroup(Icon icon, String title, boolean openByDefault) {
            MetronicMenuGroup group = new MetronicMenuGroup(icon, title);
            menu.addItem(group);
            if (openByDefault) {
                group.setOpen();
            }
            return new ReportMenuGroupBuilder(group);
        }

        public ReportMenuBuilder addItem(Icon icon, String title, ISupplier<SingularReport> report) {
            menu.addItem(new ReportAjaxMenuItem(icon, title, report));
            return this;
        }
    }

    public class ReportMenuGroupBuilder {
        private final MetronicMenuGroup group;

        public ReportMenuGroupBuilder(MetronicMenuGroup group) {
            this.group = group;
        }

        public ReportMenuGroupBuilder addItem(Icon icon, String title, ISupplier<SingularReport> report) {
            group.addItem(new ReportAjaxMenuItem(icon, title, report));
            return this;
        }

        public ReportMenuGroupBuilder addGroup(Icon icon, String title, boolean openByDefault) {
            MetronicMenuGroup newGroup = new MetronicMenuGroup(icon, title);
            group.addItem(newGroup);
            if (openByDefault) {
                newGroup.setOpen();
            }
            return new ReportMenuGroupBuilder(newGroup);
        }
    }

}