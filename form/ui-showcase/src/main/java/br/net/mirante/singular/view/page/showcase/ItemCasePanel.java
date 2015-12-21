package br.net.mirante.singular.view.page.showcase;

import br.net.mirante.singular.dao.form.FileDao;
import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.MTipo;
import br.net.mirante.singular.form.mform.ServiceRef;
import br.net.mirante.singular.form.mform.core.attachment.IAttachmentPersistenceHandler;
import br.net.mirante.singular.form.mform.core.attachment.handlers.InMemoryAttachmentPersitenceHandler;
import br.net.mirante.singular.form.mform.document.SDocument;
import br.net.mirante.singular.form.util.xml.MElement;
import br.net.mirante.singular.form.validation.InstanceValidationContext;
import br.net.mirante.singular.form.validation.ValidationErrorLevel;
import br.net.mirante.singular.form.wicket.SingularFormContextWicket;
import br.net.mirante.singular.form.wicket.WicketBuildContext;
import br.net.mirante.singular.form.wicket.component.BelverSaveButton;
import br.net.mirante.singular.form.wicket.component.BelverValidationButton;
import br.net.mirante.singular.form.wicket.enums.ViewMode;
import br.net.mirante.singular.form.wicket.model.MInstanceRootModel;
import br.net.mirante.singular.showcase.CaseBase;
import br.net.mirante.singular.showcase.ResourceRef;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSGrid;
import br.net.mirante.singular.util.wicket.modal.BSModalBorder;
import br.net.mirante.singular.util.wicket.output.BOutputPanel;
import br.net.mirante.singular.util.wicket.tab.BSTabPanel;
import br.net.mirante.singular.view.SingularWicketContainer;
import br.net.mirante.singular.view.page.form.crud.services.SpringServiceRegistry;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.feedback.FencedFeedbackPanel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import javax.inject.Inject;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.$b;
import static br.net.mirante.singular.util.wicket.util.WicketUtils.$m;


public class ItemCasePanel extends Panel implements SingularWicketContainer<ItemCasePanel, Void> {

    /**
     *
     */
    private static final long serialVersionUID = 3200319871613673285L;

    @Inject
    private SpringServiceRegistry serviceRegistry;

    @Inject
    private FileDao filePersistence;

    @Inject
    private SingularFormContextWicket singularFormContext;

    private final BSModalBorder viewXmlModal = new BSModalBorder("viewXmlModal");

    private Form<?> inputForm = new Form<>("save-form");

    private BSGrid container = new BSGrid("generated");

    private IModel<MInstancia> currentInstance;

    private IModel<CaseBase> caseBase;

    private ViewMode viewMode = ViewMode.EDITION;

    private ServiceRef<IAttachmentPersistenceHandler> temporaryRef = ServiceRef.of(new InMemoryAttachmentPersitenceHandler());

    private ServiceRef<IAttachmentPersistenceHandler> persistanceRef = ServiceRef.of(filePersistence);

    public ItemCasePanel(String id, IModel<CaseBase> caseBase) {
        super(id);
        this.caseBase = caseBase;
        createInstance();
        updateContainer();
    }

    private WebMarkupContainer buildHeaderText() {

        WebMarkupContainer headerContainer = new WebMarkupContainer("header");
        String description = caseBase.getObject().getDescriptionHtml().orElse("");

        headerContainer.add(new Label("description", $m.ofValue(description)));
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
            bsTabPanel.addTab(rr.getDisplayName(), new ItemCodePanel(BSTabPanel.getTabPanelId(), $m.ofValue(rr.getContent())));
        }

        return bsTabPanel;
    }

    private void createInstance() {
        MTipo<?> tipo = caseBase.getObject().getCaseType();
        currentInstance = new MInstanceRootModel<>(tipo.novaInstancia());
        bindDefaultServices(currentInstance.getObject().getDocument());
    }

    private void updateContainer() {
        container = new BSGrid("generated");
        inputForm.addOrReplace(container);
        inputForm.addOrReplace(viewXmlModal);
        buildContainer();
    }

    private void buildContainer() {
        WicketBuildContext ctx = new WicketBuildContext(container.newColInRow(), buildBodyContainer());
        singularFormContext.getUIBuilder().build(ctx, currentInstance, viewMode);
    }

    private BSContainer<?> buildBodyContainer() {
        BSContainer<?> bodyContainer = new BSContainer<>("body-container");
        inputForm.addOrReplace(bodyContainer);
        return bodyContainer;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(buildHeaderText());
        add(inputForm
                        .add(buildFeedbackPanel())
                        .add(buildButtons())
        );
        add(buildCodeTabs());

    }

    private MarkupContainer buildButtons() {
        final List<ItemCaseButton> botoes = buildDefaultButtons();
        botoes.addAll(caseBase.getObject().getBotoes());
        return new ListView<ItemCaseButton>("buttons", botoes) {
            @Override
            protected void populateItem(ListItem<ItemCaseButton> item) {
                item.add(item.getModelObject().buildButton("button", currentInstance));
            }
        };
    }

    @SuppressWarnings("serial")
    private Component buildFeedbackPanel() {
        return new FencedFeedbackPanel("feedback").add(new Behavior() {
            @Override
            public void onConfigure(Component component) {
                component.setVisible(((FencedFeedbackPanel) component).anyMessage());
            }
        });
    }

    private void viewXml(AjaxRequestTarget target, MElement xml) {
        final BSTabPanel xmlCodes = new BSTabPanel("xmlCodes");
        xmlCodes.addTab(getString("label.xml.persistencia"), new BOutputPanel(BSTabPanel.getTabPanelId(), $m.ofValue(getXmlOutput(xml, false))));
        xmlCodes.addTab(getString("label.xml.tabulado"), new BOutputPanel(BSTabPanel.getTabPanelId(), $m.ofValue(getXmlOutput(xml, true))));
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

    private void bindDefaultServices(SDocument document) {
        document.setAttachmentPersistenceHandler(temporaryRef);
        document.bindLocalService(SDocument.FILE_PERSISTENCE_SERVICE,
                IAttachmentPersistenceHandler.class, persistanceRef);
        document.addServiceRegistry(serviceRegistry);
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
            final BelverValidationButton bsb = new BelverValidationButton(id, ci) {
                @Override
                public boolean isVisible() {
                    return caseBase.getObject().showValidateButton();
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
                    updateContainer();
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
            final BelverSaveButton bsb = new BelverSaveButton(id, ci) {
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
                    updateContainer();
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
        AjaxButton buildButton(String id, IModel<MInstancia> currentInstance);
    }
}
