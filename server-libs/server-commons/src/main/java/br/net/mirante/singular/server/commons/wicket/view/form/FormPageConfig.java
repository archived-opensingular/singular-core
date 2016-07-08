package br.net.mirante.singular.server.commons.wicket.view.form;

import java.io.Serializable;
import java.util.HashMap;

import br.net.mirante.singular.flow.core.ProcessDefinition;
import br.net.mirante.singular.form.wicket.enums.AnnotationMode;
import br.net.mirante.singular.form.wicket.enums.ViewMode;
import br.net.mirante.singular.server.commons.flow.LazyFlowDefinitionResolver;

public class FormPageConfig implements Serializable {

    private ViewMode                           viewMode;
    private AnnotationMode                     annotationMode;
    private String                             formId;
    private String                             formType;
    private HashMap<String, Object>            contextParams;
    private LazyFlowDefinitionResolver         lazyFlowDefinitionResolver;
    private Class<? extends ProcessDefinition> processDefinition;

    private FormPageConfig() {
        viewMode = ViewMode.VISUALIZATION;
        annotationMode = AnnotationMode.NONE;
        contextParams = new HashMap<>();
    }

    private static FormPageConfig newConfig(String formType,
                                            String formId,
                                            AnnotationMode annotationMode,
                                            ViewMode viewMode) {
        final FormPageConfig cfg = new FormPageConfig();
        cfg.formType = formType;
        cfg.formId = formId;
        cfg.annotationMode = annotationMode;
        cfg.viewMode = viewMode;
        return cfg;
    }

    public static FormPageConfig newConfig(String formId,
                                           String formType,
                                           AnnotationMode annotationMode,
                                           ViewMode viewMode,
                                           Class<? extends ProcessDefinition> processDefinition) {
        final FormPageConfig cfg = newConfig(formType, formId, annotationMode, viewMode);
        cfg.processDefinition = processDefinition;
        return cfg;
    }


    public static FormPageConfig newConfig(String formType,
                                           String formId,
                                           AnnotationMode annotationMode,
                                           ViewMode viewMode,
                                           LazyFlowDefinitionResolver lazyFlowDefinitionResolver) {
        final FormPageConfig cfg = newConfig(formType, formId, annotationMode, viewMode);
        cfg.lazyFlowDefinitionResolver = lazyFlowDefinitionResolver;
        return cfg;
    }

    public ViewMode getViewMode() {
        return viewMode;
    }

    public void setViewMode(ViewMode viewMode) {
        this.viewMode = viewMode;
    }

    public AnnotationMode getAnnotationMode() {
        return annotationMode;
    }

    public void setAnnotationMode(AnnotationMode annotationMode) {
        this.annotationMode = annotationMode;
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

}