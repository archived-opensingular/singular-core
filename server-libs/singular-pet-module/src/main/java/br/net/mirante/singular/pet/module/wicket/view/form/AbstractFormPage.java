package br.net.mirante.singular.pet.module.wicket.view.form;

import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.util.xml.MElement;
import br.net.mirante.singular.form.wicket.enums.AnnotationMode;
import br.net.mirante.singular.form.wicket.enums.ViewMode;
import br.net.mirante.singular.pet.module.util.ServerProperties;
import br.net.mirante.singular.pet.module.wicket.view.template.Content;
import br.net.mirante.singular.pet.module.wicket.view.template.Template;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.flow.RedirectToUrlException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractFormPage extends Template {

    private static final Logger logger = LoggerFactory.getLogger(AbstractFormPage.class);


    @Override
    protected boolean withMenu() {
        return false;
    }

    protected abstract FormPageConfig parseParameters(Request request);


    @Override
    protected Content getContent(String id) {
        FormPageConfig config = parseParameters(getRequest());

        if (config.type == null
                && config.formId == null) {
            String urlServidorSingular = ServerProperties.getProperty(ServerProperties.SINGULAR_SERVIDOR_ENDERECO);
            throw new RedirectToUrlException(urlServidorSingular);
        }

        return new AbstractFormContent(id, config.type, config.formId, config.viewMode, config.annotationMode) {

            @Override
            protected IModel<?> getContentTitlelModel() {
                return AbstractFormPage.this.getContentTitlelModel();
            }

            @Override
            protected IModel<?> getContentSubtitlelModel() {
                return AbstractFormPage.this.getContentSubtitlelModel();
            }

            @Override
            protected String getFormXML(IModel<?> model) {
                return AbstractFormPage.this.getFormXML(model);
            }

            @Override
            protected void setFormXML(IModel<?> model, String xml) {
                AbstractFormPage.this.setFormXML(model, xml);
            }

            @Override
            protected void saveForm(IModel<?> currentInstance) {
                AbstractFormPage.this.saveForm(currentInstance);
            }

            @Override
            protected void send(IModel<? extends SInstance> currentInstance, MElement xml) {
                AbstractFormPage.this.send(currentInstance, xml);
            }

            @Override
            protected void loadOrCreateFormModel(String formId, String type, ViewMode viewMode, AnnotationMode annotationMode) {
                AbstractFormPage.this.loadOrCreateFormModel(formId, type, viewMode, annotationMode);
            }

            @Override
            protected IModel<?> getFormModel() {
                return AbstractFormPage.this.getFormModel();
            }

            @Override
            protected String getAnnotationsXML(IModel<?> model) {
                return AbstractFormPage.this.getAnnotationsXML(model);
            }

            @Override
            protected void setAnnotationsXML(IModel<?> model, String xml) {
                AbstractFormPage.this.setAnnotationsXML(model, xml);
            }
        };
    }

    protected abstract IModel<?> getContentTitlelModel();

    protected abstract IModel<?> getContentSubtitlelModel();

    protected abstract String getFormXML(IModel<?> model);

    protected abstract void setFormXML(IModel<?> model, String xml);

    protected abstract void saveForm(IModel<?> currentInstance);

    protected abstract void send(IModel<? extends SInstance> currentInstance, MElement xml);

    protected abstract void loadOrCreateFormModel(String formId, String type, ViewMode viewMode, AnnotationMode annotationMode);

    protected abstract IModel<?> getFormModel();

    protected abstract String getAnnotationsXML(IModel<?> model);

    protected abstract void setAnnotationsXML(IModel<?> model, String xml);

    public static class FormPageConfig {
        public ViewMode viewMode = ViewMode.VISUALIZATION;
        public AnnotationMode annotationMode = AnnotationMode.NONE;
        public String formId;
        public String type;
    }

}
