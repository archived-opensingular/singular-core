package br.net.mirante.singular.view.page.form.crud;

import br.net.mirante.singular.dao.form.ExampleDataDAO;
import br.net.mirante.singular.dao.form.ExampleDataDTO;
import br.net.mirante.singular.dao.form.FileDao;
import br.net.mirante.singular.dao.form.TemplateRepository;
import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.MTipo;
import br.net.mirante.singular.form.mform.ServiceRef;
import br.net.mirante.singular.form.mform.core.attachment.IAttachmentPersistenceHandler;
import br.net.mirante.singular.form.mform.core.attachment.handlers.InMemoryAttachmentPersitenceHandler;
import br.net.mirante.singular.form.mform.document.SDocument;
import br.net.mirante.singular.form.mform.io.MformPersistenciaXML;
import br.net.mirante.singular.form.util.xml.MElement;
import br.net.mirante.singular.form.util.xml.MParser;
import br.net.mirante.singular.form.wicket.UIBuilderWicket;
import br.net.mirante.singular.form.wicket.WicketBuildContext;
import br.net.mirante.singular.form.wicket.enums.ViewMode;
import br.net.mirante.singular.form.wicket.model.MInstanceRootModel;
import br.net.mirante.singular.form.wicket.util.WicketFormProcessing;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSGrid;
import br.net.mirante.singular.view.SingularWicketContainer;
import br.net.mirante.singular.view.page.form.crud.services.SpringServiceRegistry;
import br.net.mirante.singular.view.template.Content;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.feedback.FencedFeedbackPanel;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;

import javax.inject.Inject;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings("serial")
public class FormContent extends Content
    implements SingularWicketContainer<CrudContent, Void> {

    @Inject
    ExampleDataDAO                         dao;
    @Inject
    FileDao                                filePersistence;
    @Inject
    SpringServiceRegistry                  serviceRegistry;
    private BSGrid                         container = new BSGrid("generated");
    private Form<?>                        inputForm = new Form<>("save-form");
    private MInstanceRootModel<MInstancia> currentInstance;
    private ExampleDataDTO currentModel;
    private ViewMode viewMode;
    
    
//    @Inject FileDao filePersistence;
    
    private ServiceRef<IAttachmentPersistenceHandler> temporaryRef = new ServiceRef<IAttachmentPersistenceHandler>() {
        public IAttachmentPersistenceHandler get() {
            return new InMemoryAttachmentPersitenceHandler();
        }
    };

    private ServiceRef<IAttachmentPersistenceHandler> persistanceRef = new ServiceRef<IAttachmentPersistenceHandler>() {
        public IAttachmentPersistenceHandler get() {
            return filePersistence;
        }
    };
    
//    private ServiceRef<MOptionsProvider> choiceRef = new ServiceRef<MOptionsProvider>() {
//        public MOptionsProvider get() {
//            return new MFileIdsOptionsProvider(filePersistence);
//        }
//    };
    
    public FormContent(String id, StringValue type, StringValue key, StringValue viewMode) {
        super(id, false, true);
        if(viewMode.isNull()) {
            this.viewMode = ViewMode.EDITION;
        } else {
            this.viewMode = ViewMode.valueOf(viewMode.toString());
        }
        String typeName = type.toString();
        loadOrCreateModel(key, typeName);
        currentModel.setType(typeName);
    }

    private void loadOrCreateModel(StringValue key, String typeName) {
        if (key.isEmpty()) {
            currentModel = new ExampleDataDTO(UUID.randomUUID().toString());
        } else {
            currentModel = dao.find(key.toString(), typeName);
        }
        currentModel.setType(typeName);
        createInstance(typeName);
        updateContainer();
    }

    private void createInstance(String nomeDoTipo) {
        MTipo<?> tipo = TemplateRepository.get().loadType(nomeDoTipo);
        currentInstance = new MInstanceRootModel<MInstancia>(tipo.novaInstancia());
        bindDefaultServices(currentInstance.getObject().getDocument());
        populateInstance(tipo);
    }

    private void bindDefaultServices(SDocument document) {
        document.setAttachmentPersistenceHandler(temporaryRef);
        document.bindLocalService(SDocument.FILE_PERSISTENCE_SERVICE,
            IAttachmentPersistenceHandler.class, persistanceRef);
        //        document.bindLocalService("filesChoiceProvider", choiceRef);
        //        document.setServiceRegistry(createSpringRegistry());
        document.addServiceRegistry(serviceRegistry);
    }

    private void populateInstance(final MTipo<?> tipo) {
        if (currentModel.getXml() == null)
            return;
        try {
            MElement xml = MParser.parse(currentModel.getXml());
            MInstancia instance = MformPersistenciaXML.fromXML(tipo, xml);
            currentInstance = new MInstanceRootModel<MInstancia>(instance);
            bindDefaultServices(currentInstance.getObject().getDocument());
        } catch (Exception e) {
            Logger.getGlobal().log(Level.WARNING, "Captured during insertion", e);
            throw new RuntimeException(e);
        }
    }

    private void updateContainer() {
        inputForm.remove(container);
        container = new BSGrid("generated");
        inputForm.queue(container);
        buildContainer();
    }

    private void buildContainer() {
        WicketBuildContext ctx = new WicketBuildContext(container.newColInRow(), buildBodyContainer());
        UIBuilderWicket.build(ctx, currentInstance, viewMode);
    }

    @SuppressWarnings("rawtypes")
    private BSContainer buildBodyContainer() {
        BSContainer bodyContainer = new BSContainer("body-container");
        add(bodyContainer);
        return bodyContainer;
    }

    @Override
    protected IModel<?> getContentTitlelModel() {
        return new ResourceModel("label.content.title");
    }

    @Override
    protected IModel<?> getContentSubtitlelModel() {
        return new ResourceModel("label.content.title");
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        queue(inputForm
            .add(createFeedbackPanel())
            .add(createSaveButton("save-btn", true))
            .add(createSaveButton("save-whitout-validate-btn", false))
            .add(createValidateButton())
            .add(createCancelButton()));
    }

    private Component createFeedbackPanel() {
        return new FencedFeedbackPanel("feedback", inputForm).add(new Behavior() {
            @Override
            public void onConfigure(Component component) {
                component.setVisible(((FencedFeedbackPanel) component).anyMessage());
            }
        });
    }

    private AjaxButton createSaveButton(String wicketId, boolean validate) {
        return new AjaxButton(wicketId) {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {

                MInstancia trueInstance = currentInstance.getObject();
                trueInstance.getDocument().persistFiles(); //TODO: review this order
                MElement rootXml = MformPersistenciaXML.toXML(trueInstance);

                if (validate) {
                    if (!addValidationErrors(target, form, trueInstance, rootXml)) {
                        target.add(form);
                        return;
                    }
//                    try {
//                    } catch (Exception e) {
                    target.add(form);
//                        Logger.getGlobal().log(Level.WARNING, "Captured during insertion", e);
//                        return;
//                    }
                }

                currentModel.setXml(printXml(rootXml));
                dao.save(currentModel);
                backToCrudPage(this);
            }

            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisible(viewMode.isEdition());
            }
        };
    }

    private boolean addValidationErrors(AjaxRequestTarget target, Form<?> form, MInstancia trueInstance,
        MElement rootXml) {
        boolean proceed = runDefaultValidators(target, form, trueInstance);
        if (!proceed)
            return false;
        return validateEmptyForm(form, rootXml);
    }

    private boolean validateEmptyForm(Form<?> form, MElement rootXml) {
        if (rootXml == null) {
            form.error(getMessage("form.message.empty").getString());
            return false;
        }
        return true;
    }

    private boolean runDefaultValidators(AjaxRequestTarget target, Form<?> form, MInstancia trueInstance) {
        return WicketFormProcessing.onFormSubmit(form, Optional.of(target), trueInstance);
        //        InstanceValidationContext validationContext = new InstanceValidationContext(trueInstance);
        //        validationContext.validateAll();
        //        WicketFormUtils.associateErrorsToComponents(validationContext, form);
        //
        //        if (validationContext.hasErrorsAboveLevel(ValidationErrorLevel.WARNING)) {
        //            throw new RuntimeException("Has form errors");
        //        }
    }

    private String printXml(MElement rootXml) {
        if (rootXml != null) {
            StringWriter buffer = new StringWriter();
            rootXml.printTabulado(new PrintWriter(buffer));
            return buffer.toString();
        }
        return null;
    }

    @SuppressWarnings("rawtypes")
    private AjaxLink<?> createCancelButton() {
        return new AjaxLink("cancel-btn") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                backToCrudPage(this);
            }
        };
    }

    private void backToCrudPage(Component componentContext) {
        PageParameters params = new PageParameters()
            .add(CrudPage.TYPE_NAME, currentModel.getType());
        componentContext.setResponsePage(CrudPage.class, params);
    }

    private AjaxButton createValidateButton() {
        return new AjaxButton("validate-btn") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                MInstancia trueInstance = currentInstance.getObject();
                trueInstance.getDocument().persistFiles(); //TODO: review this order
                MElement rootXml = MformPersistenciaXML.toXML(trueInstance);
                try {
                    addValidationErrors(target, form, trueInstance, rootXml);
                } catch (Exception e) {
                    target.add(form);
                    Logger.getGlobal().log(Level.WARNING, "Captured during insertion", e);
                }
            }

            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisible(viewMode.isEdition());
            }
        };
    }

    //    public ServiceRegistry createSpringRegistry() throws BeansException {
    //        ShowcaseApplication app = (ShowcaseApplication) getApplication();
    //        return new SpringServiceRegistry(app.getApplicationContext());
    //    }
}
