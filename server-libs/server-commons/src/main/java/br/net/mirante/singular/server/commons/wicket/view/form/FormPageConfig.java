package br.net.mirante.singular.server.commons.wicket.view.form;

import java.io.Serializable;
import java.util.HashMap;

import br.net.mirante.singular.flow.core.ProcessDefinition;
import br.net.mirante.singular.form.wicket.enums.AnnotationMode;
import br.net.mirante.singular.form.wicket.enums.ViewMode;
import br.net.mirante.singular.server.commons.flow.LazyFlowDefinitionResolver;
import br.net.mirante.singular.server.commons.form.FormActions;

public class FormPageConfig implements Serializable {

    private FormActions                        formAction;
    private String                             formId;
    private String                             formType;
    private HashMap<String, Object>            contextParams;
    private LazyFlowDefinitionResolver         lazyFlowDefinitionResolver;
    private Class<? extends ProcessDefinition> processDefinition;

    private FormPageConfig() {
        formAction = FormActions.FORM_VIEW;
        contextParams = new HashMap<>();
    }

    private static FormPageConfig newConfig(String formType,
                                            String formId,
                                            FormActions formAction) {
        final FormPageConfig cfg = new FormPageConfig();
        cfg.formType = formType;
        cfg.formId = formId;
        cfg.formAction = formAction;
        return cfg;
    }

    public static FormPageConfig newConfig(String formId,
                                           String formType,
                                           FormActions formAction,
                                           Class<? extends ProcessDefinition> processDefinition) {
        final FormPageConfig cfg = newConfig(formType, formId, formAction);
        cfg.processDefinition = processDefinition;
        return cfg;
    }


    public static FormPageConfig newConfig(String formType,
                                           String formId,
                                           FormActions formAction,
                                           LazyFlowDefinitionResolver lazyFlowDefinitionResolver) {
        final FormPageConfig cfg = newConfig(formType, formId, formAction);
        cfg.lazyFlowDefinitionResolver = lazyFlowDefinitionResolver;
        return cfg;
    }

    public ViewMode getViewMode() {
        return formAction.getViewMode();
    }

    public AnnotationMode getAnnotationMode() {
        return formAction.getAnnotationMode();
    }

    public String getFormId() {
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

    public String getFormType() {
        return formType;
    }

    public void setFormType(String formType) {
        this.formType = formType;
    }

    public HashMap<String, Object> getContextParams() {
        return contextParams;
    }

    public Class<? extends ProcessDefinition> getProcessDefinition() {
        return processDefinition;
    }

    public void setProcessDefinition(Class<? extends ProcessDefinition> processDefinition) {
        this.processDefinition = processDefinition;
    }

    public LazyFlowDefinitionResolver getLazyFlowDefinitionResolver() {
        return lazyFlowDefinitionResolver;
    }

    public void setLazyFlowDefinitionResolver(LazyFlowDefinitionResolver lazyFlowDefinitionResolver) {
        this.lazyFlowDefinitionResolver = lazyFlowDefinitionResolver;
    }

    public boolean isWithLazyProcessResolver() {
        return lazyFlowDefinitionResolver != null;
    }

    public boolean containsProcessDefinition() {
        return processDefinition != null;
    }

    public FormActions getFormAction() {
        return formAction;
    }

    public void setFormAction(FormActions formAction) {
        this.formAction = formAction;
    }
}