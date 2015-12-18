package br.net.mirante.singular.view.page.peticao;

import java.io.PrintWriter;
import java.io.StringWriter;

import br.net.mirante.singular.form.mform.context.SingularFormContext;
import br.net.mirante.singular.form.wicket.IWicketComponentMapper;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.feedback.FencedFeedbackPanel;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

import br.net.mirante.singular.form.mform.MDicionario;
import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.MTipo;
import br.net.mirante.singular.form.mform.io.MformPersistenciaXML;
import br.net.mirante.singular.form.util.xml.MElement;
import br.net.mirante.singular.form.validation.InstanceValidationContext;
import br.net.mirante.singular.form.validation.ValidationErrorLevel;
import br.net.mirante.singular.form.wicket.UIBuilderWicket;
import br.net.mirante.singular.form.wicket.WicketBuildContext;
import br.net.mirante.singular.form.wicket.model.MInstanceRootModel;
import br.net.mirante.singular.form.wicket.util.WicketFormUtils;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSGrid;
import br.net.mirante.singular.view.SingularWicketContainer;
import br.net.mirante.singular.view.template.Content;

import javax.inject.Inject;

public class PeticaoContent extends Content
        implements SingularWicketContainer<PeticaoContent, Void> {

    @Inject
    private SingularFormContext<UIBuilderWicket, IWicketComponentMapper> singularFormContext;

    private Form<?> inputForm = new Form<>("save-form");
    private BSGrid container = new BSGrid("generated");
    private IModel<MInstancia> currentInstance;

    public PeticaoContent(String id) {
        super(id);

        createInstance();
        updateContainer();
    }

    private void createInstance() {
        //TODO precisa de tudo isso??
        MDicionario d = MDicionario.create();
        d.carregarPacote(MPacotePeticaoGGTOX.class);
        MTipo<?> tipo = d.getTipo(MPacotePeticaoGGTOX.NOME_COMPLETO);
        currentInstance = new MInstanceRootModel<>(tipo.novaInstancia());

    }


    private void updateContainer() {
        inputForm.remove(container);
        container = new BSGrid("generated");
        inputForm.queue(container);
        buildContainer();
    }

    private void buildContainer() {
        WicketBuildContext ctx = new WicketBuildContext(container.newColInRow(), buildBodyContainer());
        singularFormContext.getUIBuilder().buildForEdit(ctx, currentInstance);
    }

    private BSContainer<?> buildBodyContainer(){
        BSContainer<?> bodyContainer = new BSContainer<>("body-container");
        add(bodyContainer);
        return bodyContainer;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        queue(inputForm
                .add(createFeedbackPanel())
                .add(new SaveButton("save-btn"))
                .add(createCancelButton())
        );
    }


    private Component createFeedbackPanel() {
        return new FencedFeedbackPanel("feedback", inputForm).add(new Behavior() {
            @Override
            public void onConfigure(Component component) {
                component.setVisible(((FencedFeedbackPanel) component).anyMessage());
            }
        });
    }

    private final class SaveButton extends AjaxButton {
        private SaveButton(String id) {
            super(id);
        }

        @Override
        protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
            MInstancia trueInstance = currentInstance.getObject();
            MElement rootXml = MformPersistenciaXML.toXML(trueInstance);

            try {
                addValidationErrors(target, form, trueInstance, rootXml);
            } catch (Exception e) {
                target.add(form);
                return;
            }
            System.out.println(printXml(rootXml));
        }

        private void addValidationErrors(AjaxRequestTarget target, Form<?> form, MInstancia trueInstance,
                                         MElement rootXml) throws Exception {
            runDefaultValidators(form, trueInstance);
            validateEmptyForm(form, rootXml);
        }

        private void validateEmptyForm(Form<?> form, MElement rootXml) {
            if (rootXml == null) {
                form.error(getMessage("form.message.empty").getString());
                throw new RuntimeException("Has empty form");
            }
        }

        private void runDefaultValidators(Form<?> form, MInstancia trueInstance) {
            InstanceValidationContext validationContext = new InstanceValidationContext(trueInstance);
            validationContext.validateAll();
            WicketFormUtils.associateErrorsToComponents(validationContext, form);

            if (validationContext.hasErrorsAboveLevel(ValidationErrorLevel.WARNING)) {
                throw new RuntimeException("Has form errors");
            }
        }

        private String printXml(MElement rootXml) {
            StringWriter buffer = new StringWriter();
            rootXml.printTabulado(new PrintWriter(buffer));
            return buffer.toString();
        }
    }

    private AjaxButton createCancelButton() {
        return new AjaxButton("cancel-btn") {
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                setResponsePage(ListPeticaoPage.class);
            }
        };
    }

    @Override
    protected IModel<?> getContentTitlelModel() {
        return new ResourceModel("label.content.title");
    }

    @Override
    protected IModel<?> getContentSubtitlelModel() {
        return new ResourceModel("label.content.subtitle");
    }
}
