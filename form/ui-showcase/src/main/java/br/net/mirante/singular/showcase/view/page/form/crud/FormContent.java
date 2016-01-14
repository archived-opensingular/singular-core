package br.net.mirante.singular.showcase.view.page.form.crud;

import br.net.mirante.singular.form.mform.basic.view.MAnnotationView;
import br.net.mirante.singular.showcase.dao.form.ExampleDataDAO;
import br.net.mirante.singular.showcase.dao.form.ExampleDataDTO;
import br.net.mirante.singular.showcase.dao.form.TemplateRepository;
import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.MTipo;
import br.net.mirante.singular.form.mform.io.MformPersistenciaXML;
import br.net.mirante.singular.form.util.xml.MElement;
import br.net.mirante.singular.form.util.xml.MParser;
import br.net.mirante.singular.form.wicket.component.BelverSaveButton;
import br.net.mirante.singular.form.wicket.component.BelverValidationButton;
import br.net.mirante.singular.form.wicket.enums.ViewMode;
import br.net.mirante.singular.form.wicket.model.MInstanceRootModel;
import br.net.mirante.singular.form.wicket.panel.BelverPanel;
import br.net.mirante.singular.showcase.view.SingularWicketContainer;
import br.net.mirante.singular.showcase.view.page.form.crud.services.SpringServiceRegistry;
import br.net.mirante.singular.showcase.view.template.Content;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import javax.inject.Inject;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

public class FormContent extends Content implements SingularWicketContainer<CrudContent, Void> {

    /**
     *
     */
    private static final long serialVersionUID = 327099871613673185L;

    private static final Logger logger = LoggerFactory.getLogger(FormContent.class);

    private final String key;
    private final String typeName;
    private ViewMode viewMode = ViewMode.EDITION;

    private ExampleDataDTO currentModel;
    private BelverPanel belverPanel;

    @Inject
    private ExampleDataDAO dao;

    @Inject
    private SpringServiceRegistry serviceRegistry;

    public FormContent(String id, StringValue type, StringValue key, StringValue viewMode) {
        super(id, false, true);
        if (!viewMode.isNull()) {
            this.viewMode = ViewMode.valueOf(viewMode.toString());
        }
        this.typeName = type.toString();
        this.key = key.toString();
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(buildForm());
    }

    @Override
    protected IModel<?> getContentTitlelModel() {
        return new ResourceModel("label.content.title");
    }

    @Override
    protected IModel<?> getContentSubtitlelModel() {
        return new ResourceModel("label.content.title");
    }

    private Form<?> buildForm() {
        Form<?> form = new Form<>("save-form");
        form.setMultiPart(true);
        form.add(buildBelverBasePanel());
        form.add(buildSaveButton());
        form.add(buildSaveWithoutValidateButton());
        form.add(buildValidateButton());
        form.add(buildCancelButton());
        return form;
    }

    private BelverPanel buildBelverBasePanel() {
        belverPanel = new BelverPanel("belver-panel", serviceRegistry) {

            @Override
            protected MTipo<?> getTipo() {
                return TemplateRepository.get().loadType(typeName);
            }

            @Override
            protected MInstanceRootModel<MInstancia> populateInstance(MTipo<?> tipo) {
                try {
                    loadOrbuildModel();
                    final String xml = currentModel.getXml();
                    if (xml == null || xml.isEmpty()) {
                        return super.populateInstance(tipo);
                    } else {
                        MElement xmlElement = MParser.parse(xml);
                        MInstancia instance = MformPersistenciaXML.fromXML(tipo, xmlElement);
                        return new MInstanceRootModel<>(instance);
                    }
                } catch (SAXException | IOException e) {
                    logger.error(e.getMessage(), e);
                }
                return null;
            }

            @Override
            public ViewMode getViewMode() {
                return viewMode;
            }
        };

        return belverPanel;
    }

    private void loadOrbuildModel() {
        if (key == null || key.isEmpty()) {
            currentModel = new ExampleDataDTO(UUID.randomUUID().toString());
            currentModel.setType(typeName);
        } else {
            currentModel = dao.find(key, typeName);
        }
    }

    private void backToCrudPage(Component componentContext) {
        PageParameters params = new PageParameters()
                .add(CrudPage.TYPE_NAME, currentModel.getType());
        componentContext.setResponsePage(CrudPage.class, params);
    }

    private Component buildSaveButton() {
        final Component button = new BelverSaveButton("save-btn") {
            @Override
            public IModel<? extends MInstancia> getCurrentInstance() {
                return belverPanel.getRootInstance();
            }

            @Override
            protected void handleSaveXML(AjaxRequestTarget target, MElement xml) {
                currentModel.setXml(xml.toStringExato());
                dao.save(currentModel);
                backToCrudPage(this);
            }
        };
        return button.add(visibleOnlyInEditionBehaviour());
    }

    private Component buildSaveWithoutValidateButton() {
        final Component button = new AjaxButton("save-whitout-validate-btn") {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                final MInstancia instancia = belverPanel.getRootInstance().getObject();

                instancia.getDocument().persistFiles();
                Optional<String> rootXml = MformPersistenciaXML.toStringXML(instancia);

                currentModel.setXml(rootXml.orElse(""));
                dao.save(currentModel);
                backToCrudPage(this);
            }

        };
        return button.add(visibleOnlyInEditionBehaviour());
    }

    @SuppressWarnings("rawtypes")
    private AjaxLink<?> buildCancelButton() {
        return new AjaxLink("cancel-btn") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                backToCrudPage(this);
            }
        };
    }

    private Component buildValidateButton() {
        final BelverValidationButton button = new BelverValidationButton("validate-btn") {

            @Override
            protected void onValidationSuccess(AjaxRequestTarget target, Form<?> form,
                                               IModel<? extends MInstancia> instanceModel) {
            }

            @Override
            public IModel<? extends MInstancia> getCurrentInstance() {
                return belverPanel.getRootInstance();
            }
        };

        return button.add(visibleOnlyInEditionBehaviour());
    }

    private Behavior visibleOnlyInEditionBehaviour() {
        return new Behavior() {
            @Override
            public void onConfigure(Component component) {
                super.onConfigure(component);

                component.setVisible(viewMode.isEdition() || isInAnnotationMode());
            }
        };
    }

    private boolean isInAnnotationMode() {
        MTipo<?> mTipo = TemplateRepository.get().loadType(typeName);
        boolean isAnnotated = mTipo.getView() instanceof MAnnotationView;
        return viewMode.isVisualization() && isAnnotated;
    }

}