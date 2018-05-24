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
import org.opensingular.lib.commons.report.SingularReport;
import org.opensingular.lib.commons.ui.Icon;
import org.opensingular.lib.commons.views.ViewGenerator;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSContainer;
import org.opensingular.lib.wicket.util.bootstrap.layout.TemplatePanel;
import org.opensingular.lib.wicket.util.modal.BSModalBorder;
import org.opensingular.lib.wicket.util.resource.DefaultIcons;
import org.opensingular.lib.wicket.util.util.Shortcuts;
import org.opensingular.lib.wicket.views.SingularReportPanel;
import org.opensingular.lib.wicket.views.plugin.ReportButtonExtension;

import static org.opensingular.form.wicket.AjaxUpdateListenersFactory.SINGULAR_PROCESS_EVENT;
import static org.opensingular.lib.wicket.util.util.WicketUtils.*;

public class SearchReportButtonExtension implements ReportButtonExtension {

    private SingularFormPanel  singularFormPanel;
    private SingularSaveButton filterButton;
    private BSModalBorder      searchModal;
    private boolean init = true;
    private SingularReport singularFormReport;
    private boolean hasFilter;

    @Override
    public void init(SingularReport singularReport) {
        this.singularFormReport = singularReport;
        hasFilter = singularFormReport.getFilterValue() != null;
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
        if (hasFilter) {
            searchModal.show(ajaxRequestTarget);
        }
    }

    @Override
    public boolean isButtonVisible() {
        return hasFilter;
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
        BSContainer<?> modalContainer = new BSContainer<>(reportPanel.getPluginContainerView().newChildId());
        singularFormPanel.setModalContainer(modalContainer);

        templatePanel.add(searchModal);
        reportPanel
                .getPluginContainerView()
                .add(templatePanel)
                .add(modalContainer);
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
                singularFormReport.setFilterValue(singularFormPanel.getInstance());
                bsModalBorder.hide(target);
            }
        };
        bsModalBorder.addButton(BSModalBorder.ButtonStyle.CONFIRM, Model.of("Aplicar"), filterButton);
        bsModalBorder.add(Shortcuts.$b.onEnterDelegate(filterButton, SINGULAR_PROCESS_EVENT));
    }

    private void addFilter(BSModalBorder bsModalBorder) {
        SingularFormReport sr = (SingularFormReport) singularFormReport;
        singularFormPanel = new SingularFormPanel("singular-form-panel", (SInstance) sr.getFilterValue());
        singularFormPanel.setNested(true);
        bsModalBorder.add(singularFormPanel);
    }

    private boolean isFirstRequestAndIsNotEagerLoading() {
        return init && !singularFormReport.eagerLoading();
    }

}