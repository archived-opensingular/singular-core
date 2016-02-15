package br.net.mirante.singular.showcase.view.page;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.$b;
import static br.net.mirante.singular.util.wicket.util.WicketUtils.$m;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.util.xml.MElement;
import br.net.mirante.singular.form.wicket.component.BFModalBorder;
import br.net.mirante.singular.form.wicket.component.SingularSaveButton;
import br.net.mirante.singular.form.wicket.component.SingularValidationButton;
import br.net.mirante.singular.form.wicket.enums.ViewMode;
import br.net.mirante.singular.form.wicket.panel.SingularFormPanel;
import br.net.mirante.singular.showcase.component.CaseBase;
import br.net.mirante.singular.showcase.component.ResourceRef;
import br.net.mirante.singular.showcase.dao.form.TemplateRepository;
import br.net.mirante.singular.showcase.view.SingularWicketContainer;
import br.net.mirante.singular.showcase.view.page.form.crud.services.SpringServiceRegistry;
import br.net.mirante.singular.util.wicket.output.BOutputPanel;
import br.net.mirante.singular.util.wicket.tab.BSTabPanel;


public class ItemCasePanel extends Panel implements SingularWicketContainer<ItemCasePanel, Void> {

    /**
     *
     */
    private static final long serialVersionUID = 3200319871613673285L;

    private final BFModalBorder viewXmlModal = new BFModalBorder("viewXmlModal");
    private final IModel<CaseBase> caseBase;

    private SingularFormPanel singularFormPanel = null;
    private ViewMode viewMode = ViewMode.EDITION;

    @Inject
    private SpringServiceRegistry springServiceRegistry;

    public ItemCasePanel(String id, IModel<CaseBase> caseBase) {
        super(id);
        this.caseBase = caseBase;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(buildHeaderText());

        Form<Void> form = new Form<>("form");
        form.add(buildSingularBasePanel());
        form.add(buildButtons());
        form.add(viewXmlModal);

        add(buildCodeTabs());
        add(form);
    }

    private WebMarkupContainer buildHeaderText() {

        WebMarkupContainer headerContainer = new WebMarkupContainer("header");
        String description = caseBase.getObject().getDescriptionHtml().orElse("");

        headerContainer.add(new Label("description", $m.ofValue(description)).setEscapeModelStrings(false));
        headerContainer.setVisible(!description.isEmpty());

        return headerContainer;
    }

    private BSTabPanel buildCodeTabs() {

        final BSTabPanel bsTabPanel = new BSTabPanel("codes");
        final List<ResourceRef> sources = new ArrayList<>();
        final Optional<ResourceRef> mainSource = caseBase.getObject().getMainSourceResourceName();

        if (mainSource.isPresent()) {
            sources.add(mainSource.get());
        }

        sources.addAll(caseBase.getObject().getAditionalSources());

        for (ResourceRef rr : sources) {
            bsTabPanel.addTab(rr.getDisplayName(), new ItemCodePanel(
                    BSTabPanel.getTabPanelId(), $m.ofValue(rr.getContent()), $m.ofValue(rr.getExtension())));
        }

        return bsTabPanel;
    }


    private SingularFormPanel buildSingularBasePanel() {
        singularFormPanel = new SingularFormPanel("singularFormPanel", springServiceRegistry) {
            @Override
            protected SType<?> getTipo() {
                String typeName = caseBase.getObject().getCaseType().getName();
                return TemplateRepository.create().loadType(typeName, typeName);
            }

            @Override
            public ViewMode getViewMode() {
                return viewMode;
            }

            @Override
            public boolean annotationEnabled(){ return caseBase.getObject().annotationEnabled(); }
        };

        return singularFormPanel;
    }

    private MarkupContainer buildButtons() {
        final List<ItemCaseButton> botoes = buildDefaultButtons();
        botoes.addAll(caseBase.getObject().getBotoes());
        return new ListView<ItemCaseButton>("buttons", botoes) {
            @Override
            protected void populateItem(ListItem<ItemCaseButton> item) {
                item.add(item.getModelObject().buildButton("button", singularFormPanel.getRootInstance()));
            }
        };
    }

    private void viewXml(AjaxRequestTarget target, MElement xml) {
        final BSTabPanel xmlCodes = new BSTabPanel("xmlCodes");
        xmlCodes.addTab(getString("label.xml.tabulado"), new BOutputPanel(BSTabPanel.getTabPanelId(), $m.ofValue(getXmlOutput(xml, true))));
        xmlCodes.addTab(getString("label.xml.persistencia"), new BOutputPanel(BSTabPanel.getTabPanelId(), $m.ofValue(getXmlOutput(xml, false))));
        viewXmlModal.addOrReplace(xmlCodes);
        viewXmlModal.show(target);
    }

    private String getXmlOutput(MElement xml, boolean tabulado) {
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
                    return caseBase.getObject().showValidateButton();
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

    public interface ItemCaseButton extends Serializable {
        AjaxButton buildButton(String id, IModel<? extends SInstance> currentInstance);
    }
}
