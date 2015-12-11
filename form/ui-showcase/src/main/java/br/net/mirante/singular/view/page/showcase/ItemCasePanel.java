package br.net.mirante.singular.view.page.showcase;

import br.net.mirante.singular.dao.form.FileDao;
import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.MTipo;
import br.net.mirante.singular.form.mform.ServiceRef;
import br.net.mirante.singular.form.mform.core.attachment.IAttachmentPersistenceHandler;
import br.net.mirante.singular.form.mform.core.attachment.handlers.InMemoryAttachmentPersitenceHandler;
import br.net.mirante.singular.form.mform.document.SDocument;
import br.net.mirante.singular.form.mform.io.MformPersistenciaXML;
import br.net.mirante.singular.form.util.xml.MElement;
import br.net.mirante.singular.form.validation.InstanceValidationContext;
import br.net.mirante.singular.form.validation.ValidationErrorLevel;
import br.net.mirante.singular.form.wicket.UIBuilderWicket;
import br.net.mirante.singular.form.wicket.WicketBuildContext;
import br.net.mirante.singular.form.wicket.model.MInstanceRootModel;
import br.net.mirante.singular.form.wicket.util.WicketFormUtils;
import br.net.mirante.singular.showcase.CaseBase;
import br.net.mirante.singular.showcase.ResourceRef;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSGrid;
import br.net.mirante.singular.util.wicket.modal.BSModalBorder;
import br.net.mirante.singular.util.wicket.tab.BSTabPanel;
import br.net.mirante.singular.view.SingularWicketContainer;
import br.net.mirante.singular.view.page.form.crud.services.SpringServiceRegistry;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.feedback.FencedFeedbackPanel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import javax.inject.Inject;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Optional;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.$m;


public class ItemCasePanel extends Panel implements SingularWicketContainer<ItemCasePanel, Void> {

    /**
     *
     */
    private static final long serialVersionUID = 3200319871613673285L;

    private final BSModalBorder viewXmlModal = new BSModalBorder("viewXmlModal");

    @Inject
    private SpringServiceRegistry serviceRegistry;

    @Inject
    private FileDao filePersistence;

    private Form<?> inputForm = new Form<>("save-form");

    private BSGrid container = new BSGrid("generated");

    private IModel<MInstancia> currentInstance;

    private IModel<CaseBase> caseBase;

    private ServiceRef<IAttachmentPersistenceHandler> temporaryRef = InMemoryAttachmentPersitenceHandler::new;

    private ServiceRef<IAttachmentPersistenceHandler> persistanceRef = new ServiceRef<IAttachmentPersistenceHandler>() {
        public IAttachmentPersistenceHandler get() {
            return filePersistence;
        }
    };

    public ItemCasePanel(String id, IModel<CaseBase> caseBase) {
        super(id);
        this.caseBase = caseBase;
        add(buildBlockquote());
        createInstance();
        updateContainer();
        add(buildCodeTabs());
    }

    private WebMarkupContainer buildBlockquote(){

        WebMarkupContainer blockquote = new WebMarkupContainer("blockquote");
        String description = caseBase.getObject().getDescriptionHtml().orElse("");

        blockquote.add(new Label("description", $m.ofValue(description)));
        blockquote.setVisible(!description.isEmpty());

        return blockquote;
    }

    private BSTabPanel buildCodeTabs() {
        BSTabPanel bsTabPanel = new BSTabPanel("codes");
        Optional<ResourceRef> sources = caseBase.getObject().getMainSourceResourceName();
        if(sources.isPresent()) {
            for (ResourceRef rr : Collections.singletonList(sources.get())) {
                bsTabPanel.addTab(rr.getDisplayName(), new ItemCodePanel(BSTabPanel.getTabPanelId(), $m.ofValue(rr.getContent())));
            }
        }
        return bsTabPanel;
    }

    private void createInstance() {
        MTipo<?> tipo = caseBase.getObject().getCaseType();
        currentInstance = new MInstanceRootModel<>(tipo.novaInstancia());
        bindDefaultServices(currentInstance.getObject().getDocument());
    }

    private void updateContainer() {
        inputForm.remove(container);
        container = new BSGrid("generated");
        inputForm.add(container);
        inputForm.add(viewXmlModal);
        buildContainer();
    }

    private void buildContainer() {
        WicketBuildContext ctx = new WicketBuildContext(container.newColInRow(), buildBodyContainer());
        UIBuilderWicket.buildForEdit(ctx, currentInstance);
    }

    private BSContainer<?> buildBodyContainer() {
        BSContainer<?> bodyContainer = new BSContainer<>("body-container");
        inputForm.add(bodyContainer);
        return bodyContainer;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(inputForm
                        .add(createFeedbackPanel())
                        .add(createSaveButton())
                        .add(createValidateButton())
        );
    }

    @SuppressWarnings("serial")
    private Component createSaveButton() {
        return new AjaxButton("save-btn") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                if (addValidationErrors(form, currentInstance.getObject())) {
                    MElement rootXml = MformPersistenciaXML.toXML(currentInstance.getObject());
                    viewXml(target, printXml(rootXml));
                }
                target.add(form);
            }
        };
    }

    private String printXml(MElement rootXml) {
        if (rootXml != null) {
            StringWriter buffer = new StringWriter();
            rootXml.printTabulado(new PrintWriter(buffer));
            return buffer.toString();
        }
        return null;
    }

    @SuppressWarnings("serial")
    private Component createFeedbackPanel() {
        return new FencedFeedbackPanel("feedback").add(new Behavior() {
            @Override
            public void onConfigure(Component component) {
                component.setVisible(((FencedFeedbackPanel) component).anyMessage());
            }
        });
    }

    private boolean addValidationErrors(Form<?> form, MInstancia trueInstance) {
        InstanceValidationContext validationContext = new InstanceValidationContext(trueInstance);
        validationContext.validateAll();
        WicketFormUtils.associateErrorsToComponents(validationContext, form);
        return !validationContext.hasErrorsAboveLevel(ValidationErrorLevel.WARNING);
    }

    private AjaxButton createValidateButton() {
        return new AjaxButton("validate-btn") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                addValidationErrors(form, currentInstance.getObject());
                target.add(form);
            }

            @Override
            public boolean isVisible() {
                return caseBase.getObject().getCaseType().hasAnyValidation();
            }
        };
    }

    private void viewXml(AjaxRequestTarget target, String xml) {
        viewXmlModal.addOrReplace(new Label("xmlCode", $m.ofValue(xml)));
        viewXmlModal.show(target);
    }

    private void bindDefaultServices(SDocument document) {
        document.setAttachmentPersistenceHandler(temporaryRef);
        document.bindLocalService(SDocument.FILE_PERSISTENCE_SERVICE,
                IAttachmentPersistenceHandler.class, persistanceRef);
        document.addServiceRegistry(serviceRegistry);
    }
}
