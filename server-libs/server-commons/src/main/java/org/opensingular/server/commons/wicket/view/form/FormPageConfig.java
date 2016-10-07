/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.server.commons.wicket.view.form;

import org.opensingular.flow.core.ProcessDefinition;
import org.opensingular.form.wicket.enums.AnnotationMode;
import org.opensingular.form.wicket.enums.ViewMode;
import org.opensingular.server.commons.flow.LazyFlowDefinitionResolver;
import org.opensingular.server.commons.form.FormActions;

import java.io.Serializable;
import java.util.HashMap;

public class FormPageConfig implements Serializable {

    private FormActions                        formAction;
    private String                             petitionId;
    private String                             formType;
    private HashMap<String, Object>            contextParams;
    private LazyFlowDefinitionResolver         lazyFlowDefinitionResolver;
    private Class<? extends ProcessDefinition> processDefinition;
    private Long                               formVersionPK;

    private FormPageConfig() {
        formAction = FormActions.FORM_VIEW;
        contextParams = new HashMap<>();
    }

    private static FormPageConfig newConfig(String formType,
                                            String petitionId,
                                            FormActions formAction,
                                            Long formVersionPK) {
        final FormPageConfig cfg = new FormPageConfig();
        cfg.formType = formType;
        cfg.petitionId = petitionId;
        cfg.formAction = formAction;
        cfg.formVersionPK = formVersionPK;
        return cfg;
    }

    public static FormPageConfig newConfig(String petitionId,
                                           String formType,
                                           FormActions formAction,
                                           Long formVersionPK,
                                           Class<? extends ProcessDefinition> processDefinition) {
        final FormPageConfig cfg = newConfig(formType, petitionId, formAction, formVersionPK);
        cfg.processDefinition = processDefinition;
        return cfg;
    }


    public static FormPageConfig newConfig(String formType,
                                           String petitionId,
                                           FormActions formAction,
                                           Long formVersionPK,
                                           LazyFlowDefinitionResolver lazyFlowDefinitionResolver) {
        final FormPageConfig cfg = newConfig(formType, petitionId, formAction, formVersionPK);
        cfg.lazyFlowDefinitionResolver = lazyFlowDefinitionResolver;
        return cfg;
    }

    public ViewMode getViewMode() {
        return formAction.getViewMode();
    }

    public AnnotationMode getAnnotationMode() {
        return formAction.getAnnotationMode();
    }

    public String getPetitionId() {
        return petitionId;
    }

    public void setPetitionId(String petitionId) {
        this.petitionId = petitionId;
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

    public Long getFormVersionPK() {
        return formVersionPK;
    }
}