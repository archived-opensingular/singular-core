package org.opensingular.form.report;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.opensingular.form.SInstance;
import org.opensingular.form.wicket.component.SingularSaveButton;
import org.opensingular.form.wicket.panel.SingularFormPanel;
import org.opensingular.lib.commons.extension.SingularExtension;
import org.opensingular.lib.commons.report.ReportMetadata;
import org.opensingular.lib.commons.report.SingularReport;
import org.opensingular.lib.commons.views.ViewGenerator;
import org.opensingular.lib.wicket.util.bootstrap.layout.TemplatePanel;
import org.opensingular.lib.wicket.util.modal.BSModalBorder;
import org.opensingular.lib.wicket.util.resource.DefaultIcons;
import org.opensingular.lib.wicket.util.resource.Icon;
import org.opensingular.lib.wicket.util.util.WicketUtils;
import org.opensingular.lib.wicket.views.SingularReportPanel;
import org.opensingular.lib.wicket.views.plugin.ButtonReportPlugin;

import static org.opensingular.lib.wicket.util.util.WicketUtils.$b;

@SingularExtension
public class SearchReportPlugin implements ButtonReportPlugin {

    private SingularFormPanel singularFormPanel;
    private SingularSaveButton filterButton;
    private BSModalBorder searchModal;
    private boolean init = true;
    private SingularFormReport singularFormReport;

    @Override
    public void init(SingularReport singularReport) {
        if (singularReport instanceof SingularFormReport) {
            this.singularFormReport = (SingularFormReport) singularReport;
        }
    }

    @Override
    public void updateReportMetatada(ReportMetadata reportMetadata) {
        if (reportMetadata instanceof SingularFormReportMetadata) {
            ((SingularFormReportMetadata) (reportMetadata)).setFilter(singularFormPanel.getInstance());
        }
    }

    @Override
    public Icon getIcon() {
        return DefaultIcons.SEARCH;
    }

    @Override
    public String getName() {
        return "Pesquisar";
    }

    @Override
    public void onAction(AjaxRequestTarget ajaxRequestTarget, ViewGenerator viewGenerator) {
        searchModal.show(ajaxRequestTarget);
    }

    @Override
    public void onBuild(SingularReportPanel reportPanel) {
        TemplatePanel templatePanel = new TemplatePanel(reportPanel.getPluginContainerView().newChildId(),
                "<div wicket:id='modal-border'><div wicket:id='singular-form-panel'></div></div>");
        searchModal = new BSModalBorder("modal-border");
        addFilter(searchModal);
        addFilterButton(searchModal);
        addCloseButton(searchModal);
        reportPanel.getTable().add($b.notVisibleIf(this::isFirstRequestAndIsNotEagerLoading));
        searchModal.add(new Behavior() {
            @Override
            public void onConfigure(Component component) {
                super.onConfigure(component);
                if (isFirstRequestAndIsNotEagerLoading()) {
                    searchModal.show(null);
                    init = false;
                }
            }
        });
        templatePanel.add(searchModal);
        reportPanel.getPluginContainerView().add(templatePanel);
    }

    private void addCloseButton(BSModalBorder bsModalBorder) {
        bsModalBorder.addButton(BSModalBorder.ButtonStyle.CANCEL, Model.of("Fechar"), new AjaxButton("close") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                super.onSubmit(target, form);
                bsModalBorder.hide(target);
            }
        });
    }

    private void addFilterButton(BSModalBorder bsModalBorder) {
        filterButton = new SingularSaveButton("filter-btn", singularFormPanel.getInstanceModel(), false) {
            @Override
            protected void onValidationSuccess(AjaxRequestTarget target, Form<?> form, IModel<? extends SInstance> instanceModel) {
                target.add(form);
                bsModalBorder.hide(target);
            }
        };
        bsModalBorder.addButton(BSModalBorder.ButtonStyle.CONFIRM, Model.of("Aplicar"), filterButton);
    }

    private void addFilter(BSModalBorder bsModalBorder) {
        singularFormPanel = new SingularFormPanel("singular-form-panel", singularFormReport.getFilterType());
        singularFormPanel.setNested(true);
        bsModalBorder.add(singularFormPanel);
    }

    private boolean isFirstRequestAndIsNotEagerLoading() {
        return init && !singularFormReport.eagerLoading();
    }

}