package org.opensingular.form.report;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.cycle.RequestCycle;
import org.opensingular.form.SInstance;
import org.opensingular.form.SType;
import org.opensingular.form.wicket.component.SingularSaveButton;
import org.opensingular.form.wicket.panel.SingularFormPanel;
import org.opensingular.lib.wicket.util.modal.BSModalBorder;
import org.opensingular.lib.wicket.views.SingularReportPanel;

import java.io.Serializable;

public class SingularFormReportPanel<E extends Serializable, T extends SType<I>, I extends SInstance>
        extends SingularReportPanel<SingularFormReportMetadata<I>, I> {
    private final SingularFormReport<E, T, I> singularFormReport;
    private final IModel<Boolean> showTable;

    private SingularFormPanel singularFormPanel;
    private SingularSaveButton filterButton;

    public SingularFormReportPanel(String id, SingularFormReport<E, T, I> singularFormReport) {
        super(id, () -> singularFormReport);
        this.singularFormReport = singularFormReport;
        this.showTable = new Model<>(singularFormReport.eagerLoading());
    }

    @Override
    protected void customizeModal(BSModalBorder searchModal) {
        addFilter(searchModal);
        addFilterButton(searchModal);
        addCloseButton(searchModal);
        searchModal.add(new Behavior() {
            @Override
            public void onConfigure(Component component) {
                super.onConfigure(component);
                if (RequestCycle.get().find(AjaxRequestTarget.class) == null && !singularFormReport.eagerLoading()) {
                    searchModal.show(null);
                }
            }
        });
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
                showTable.setObject(Boolean.TRUE);
                target.add(SingularFormReportPanel.this);
                bsModalBorder.hide(target);
            }
        };
        bsModalBorder.addButton(BSModalBorder.ButtonStyle.CONFIRM, Model.of("Aplicar"), filterButton);
    }

    private void addFilter(BSModalBorder bsModalBorder) {
        singularFormPanel = new SingularFormPanel("filter", singularFormReport.getFilterType());
        singularFormPanel.setNested(true);
        bsModalBorder.add(singularFormPanel);
    }

    @Override
    protected SingularFormReportMetadata<I> getReportMetadata() {
        return new SingularFormReportMetadata<>((I) singularFormPanel.getInstance());
    }

    @Override
    protected Boolean isShowReport() {
        return showTable.getObject();
    }
}