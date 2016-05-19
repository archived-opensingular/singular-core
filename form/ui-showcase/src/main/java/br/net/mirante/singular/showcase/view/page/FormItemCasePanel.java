/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.view.page;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.$b;
import static br.net.mirante.singular.util.wicket.util.WicketUtils.$m;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.lang.Bytes;

import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.context.SFormConfig;
import br.net.mirante.singular.form.document.RefType;
import br.net.mirante.singular.form.internal.xml.MElement;
import br.net.mirante.singular.form.wicket.component.BFModalBorder;
import br.net.mirante.singular.form.wicket.component.SingularForm;
import br.net.mirante.singular.form.wicket.component.SingularSaveButton;
import br.net.mirante.singular.form.wicket.component.SingularValidationButton;
import br.net.mirante.singular.form.wicket.enums.AnnotationMode;
import br.net.mirante.singular.form.wicket.enums.ViewMode;
import br.net.mirante.singular.form.wicket.panel.SingularFormPanel;
import br.net.mirante.singular.showcase.component.CaseBase;
import br.net.mirante.singular.showcase.view.SingularWicketContainer;
import br.net.mirante.singular.util.wicket.output.BOutputPanel;
import br.net.mirante.singular.util.wicket.tab.BSTabPanel;

public class FormItemCasePanel extends ItemCasePanel implements SingularWicketContainer<FormItemCasePanel, Void> {

    private static final long serialVersionUID = 1L;

    private final BFModalBorder viewXmlModal = new BFModalBorder("viewXmlModal");

    private SingularFormPanel<String> singularFormPanel = null;
    private ViewMode viewMode = ViewMode.EDITION;

    @Inject
    @Named("formConfigWithoutDatabase")
    private SFormConfig<String> singularFormConfig;

    public FormItemCasePanel(String id, IModel<CaseBase> caseBase) {
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
                String typeName = getCaseBase().getObject().getCaseType().getName();
                RefType refType = singularFormConfig.getTypeLoader().loadRefTypeOrException(typeName);
                return singularFormConfig.getDocumentFactory().createInstance(refType);
            }

            @Override
            public ViewMode getViewMode() {
                return viewMode;
            }

            @Override
            public AnnotationMode annotation(){ return getCaseBase().getObject().annotation(); }
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

    private void viewXml(AjaxRequestTarget target, MElement xml) {
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
            final SingularValidationButton bsb = new SingularValidationButton(id) {
                @Override
                public boolean isVisible() {
                    return getCaseBase().getObject().showValidateButton();
                }

                @Override
                protected void onValidationSuccess(AjaxRequestTarget target, Form<?> form,
                                                   IModel<? extends SInstance> instanceModel) {
                }

                @Override
                public IModel<? extends SInstance> getCurrentInstance() {
                    return ci;
                }
            };

            bsb.add($b.attr("value", getString("label.button.validate")));
            bsb.add($b.classAppender("red"));

            return bsb;
        };
    }

    private ItemCaseButton buildVisualizationButton() {
        return (id, ci) -> {
            final AjaxButton ab = new AjaxButton(id) {
                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                    viewMode = ViewMode.VISUALIZATION;
                    singularFormPanel.updateContainer();
                    target.add(form);
                }

                @Override
                public boolean isVisible() {
                    return viewMode.isEdition();
                }
            };

            ab.add($b.attr("value", getString("label.button.view.mode")));
            ab.add($b.classAppender("yellow"));

            return ab;
        };
    }

    private ItemCaseButton buildSaveButton() {
        return (id, ci) -> {
            final SingularSaveButton bsb = new SingularSaveButton(id) {

                @Override
                public IModel<? extends SInstance> getCurrentInstance() {
                    return ci;
                }

                @Override
                protected void handleSaveXML(AjaxRequestTarget target, MElement xml) {
                    viewXml(target, xml);
                }
            };

            bsb.add($b.attr("value", getString("label.button.save")));
            bsb.add($b.classAppender("blue"));

            return bsb;
        };
    }

    private ItemCaseButton buildEditionButton() {
        return (id, ci) -> {
            final AjaxButton ab = new AjaxButton(id) {
                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                    viewMode = ViewMode.EDITION;
                    singularFormPanel.updateContainer();
                    target.add(form);
                }

                @Override
                public boolean isVisible() {
                    return viewMode.isVisualization();
                }
            };

            ab.add($b.attr("value", getString("label.button.edit.mode")));
            ab.add($b.classAppender("yellow"));

            return ab;
        };
    }

}
