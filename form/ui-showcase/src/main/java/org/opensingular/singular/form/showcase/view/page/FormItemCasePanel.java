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

package org.opensingular.singular.form.showcase.view.page;

import static org.opensingular.lib.wicket.util.util.WicketUtils.$b;
import static org.opensingular.lib.wicket.util.util.WicketUtils.$m;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.opensingular.singular.form.showcase.component.CaseBaseForm;
import org.opensingular.singular.form.showcase.component.ShowCaseType;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.lang.Bytes;

import org.opensingular.form.SInstance;
import org.opensingular.form.context.SFormConfig;
import org.opensingular.form.document.RefType;
import org.opensingular.form.internal.xml.MElement;
import org.opensingular.form.io.MformPersistenciaXML;
import org.opensingular.form.wicket.component.BFModalBorder;
import org.opensingular.form.wicket.component.SingularForm;
import org.opensingular.form.wicket.component.SingularSaveButton;
import org.opensingular.form.wicket.component.SingularValidationButton;
import org.opensingular.form.wicket.enums.AnnotationMode;
import org.opensingular.form.wicket.enums.ViewMode;
import org.opensingular.form.wicket.panel.SingularFormPanel;
import org.opensingular.singular.form.showcase.dao.form.ShowcaseTypeLoader;
import org.opensingular.singular.form.showcase.view.SingularWicketContainer;
import org.opensingular.lib.wicket.util.output.BOutputPanel;
import org.opensingular.lib.wicket.util.tab.BSTabPanel;

public class FormItemCasePanel extends ItemCasePanel<CaseBaseForm> implements SingularWicketContainer<FormItemCasePanel, Void> {

    private static final long serialVersionUID = 1L;

    private final BFModalBorder viewXmlModal = new BFModalBorder("viewXmlModal");

    private SingularFormPanel<String> singularFormPanel = null;
    private ViewMode viewMode = ViewMode.EDIT;

    @Inject
    @Named("formConfigWithoutDatabase")
    private SFormConfig<String> singularFormConfig;

    public FormItemCasePanel(String id, IModel<CaseBaseForm> caseBase) {
        super(id, caseBase);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        SingularForm<Void> form = new SingularForm<>("form");
        form.setMultiPart(true);
        form.setFileMaxSize(Bytes.MAX);
        form.setMaxSize(Bytes.MAX);
        form.add(buildSingularBasePanel());
        form.add(buildButtons());
        form.add(viewXmlModal);

        add(form);
    }

    private SingularFormPanel<String> buildSingularBasePanel() {
        singularFormPanel = new SingularFormPanel<String>("singularFormPanel", singularFormConfig) {
            @Override
            protected SInstance createInstance(SFormConfig<String> singularFormConfig) {
                final CaseBaseForm caseBase = getCaseBase().getObject();
                String typeName = caseBase.getCaseType().getName();
                if (caseBase.isDynamic()) {
                    registerDynamicType(singularFormConfig, caseBase);
                }
                RefType refType = singularFormConfig.getTypeLoader().loadRefTypeOrException(typeName);
                return singularFormConfig.getDocumentFactory().createInstance(refType);
            }

            private void registerDynamicType(SFormConfig<String> singularFormConfig, CaseBaseForm caseBase) {
                final ShowcaseTypeLoader typeLoader = (ShowcaseTypeLoader) singularFormConfig.getTypeLoader();
                typeLoader.add(caseBase.getComponentName(), caseBase, ShowCaseType.FORM);
            }

            @Override
            public ViewMode getViewMode() {
                return viewMode;
            }

            @Override
            public AnnotationMode getAnnotationMode(){ return getCaseBase().getObject().annotation(); }
        };

        return singularFormPanel;
    }

    private MarkupContainer buildButtons() {
        final List<ItemCaseButton> botoes = buildDefaultButtons();
        botoes.addAll(getCaseBase().getObject().getBotoes());
        return new ListView<ItemCaseButton>("buttons", botoes) {
            @Override
            protected void populateItem(ListItem<ItemCaseButton> item) {
                item.add(item.getModelObject().buildButton("button", singularFormPanel.getRootInstance()));
            }
        };
    }

    private void viewXml(AjaxRequestTarget target, SInstance instance) {
        MElement xml = MformPersistenciaXML.toXML(instance);
        final BSTabPanel xmlCodes = new BSTabPanel("xmlCodes");
        xmlCodes.addTab(getString("label.xml.tabulado"), new BOutputPanel(BSTabPanel.TAB_PANEL_ID, $m.ofValue(getXmlOutput(xml, true))));
        xmlCodes.addTab(getString("label.xml.persistencia"), new BOutputPanel(BSTabPanel.TAB_PANEL_ID, $m.ofValue(getXmlOutput(xml, false))));
        viewXmlModal.addOrReplace(xmlCodes);
        viewXmlModal.show(target);
    }

    private static String getXmlOutput(MElement xml, boolean tabulado) {
        if (xml == null) {
            return StringUtils.EMPTY;
        }
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw);
        if (tabulado) {
            xml.printTabulado(pw);
        } else {
            xml.print(pw);
        }
        return sw.toString();
    }

    private List<ItemCaseButton> buildDefaultButtons() {
        final List<ItemCaseButton> botoes = new ArrayList<>();
        botoes.add(buildSaveButton());
        botoes.add(buildValidateButton());
        botoes.add(buildVisualizationButton());
        botoes.add(buildEditionButton());
        return botoes;
    }

    private ItemCaseButton buildValidateButton() {
        return (id, ci) -> {
            final SingularValidationButton bsb = new SingularValidationButton(id, ci) {
                @Override
                public boolean isVisible() {
                    return getCaseBase().getObject().showValidateButton();
                }

                @Override
                protected void onValidationSuccess(AjaxRequestTarget target, Form<?> form,
                                                   IModel<? extends SInstance> instanceModel) {
                }
            };

            bsb.add($b.attr("value", getString("label.button.validate")));

            return bsb;
        };
    }

    private ItemCaseButton buildVisualizationButton() {
        return (id, ci) -> {
            final AjaxButton ab = new AjaxButton(id) {
                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                    viewMode = ViewMode.READ_ONLY;
                    singularFormPanel.updateContainer();
                    target.add(form);
                }

                @Override
                public boolean isVisible() {
                    return viewMode.isEdition();
                }
            };

            ab.add($b.attr("value", getString("label.button.view.mode")));

            return ab;
        };
    }

    private ItemCaseButton buildSaveButton() {
        return (id, ci) -> {
            final SingularSaveButton bsb = new SingularSaveButton(id, ci) {
                @Override
                protected void onValidationSuccess(AjaxRequestTarget target, Form<?> form, IModel<? extends SInstance> instanceModel) {
                    viewXml(target, instanceModel.getObject());
                }
            };

            bsb.add($b.attr("value", getString("label.button.save")));
            bsb.add($b.classAppender("send-btn"));

            return bsb;
        };
    }

    private ItemCaseButton buildEditionButton() {
        return (id, ci) -> {
            final AjaxButton ab = new AjaxButton(id) {
                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                    viewMode = ViewMode.EDIT;
                    singularFormPanel.updateContainer();
                    target.add(form);
                }

                @Override
                public boolean isVisible() {
                    return viewMode.isVisualization();
                }
            };

            ab.add($b.attr("value", getString("label.button.edit.mode")));

            return ab;
        };
    }

}
