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

package org.opensingular.singular.form.showcase.component;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.opensingular.form.wicket.enums.AnnotationMode;
import org.opensingular.singular.form.showcase.view.page.ItemCasePanel;

/**
 * Representa um exemplo de um componente ou solução junto com os respectivo
 * códigos e explicações.
 */
public abstract class CaseBase implements Serializable {

    private final String componentName;
    private final String subCaseName;
    private String descriptionHtml;
    private final List<ItemCasePanel.ItemCaseButton> botoes = new ArrayList<>();
    private final List<ResourceRef> aditionalSources = new ArrayList<>();
    protected Class<?> caseClass;
    private AnnotationMode annotationMode = AnnotationMode.NONE;

    private ShowCaseType showCaseType;

    public CaseBase(String componentName) {
        this(componentName, null);
    }

    public CaseBase(String componentName, String subCaseName) {
        this.componentName = componentName;
        this.subCaseName = subCaseName;
    }

    public CaseBase(Class<?> caseClass, ShowCaseType type, String componentName, String subCaseName, AnnotationMode annotation) {
        this.caseClass = caseClass;
        this.componentName = componentName;
        this.subCaseName = subCaseName;
        this.showCaseType = type;
        this.annotationMode = annotation;
    }


    public String getComponentName() {
        return componentName;
    }

    public String getSubCaseName() {
        return subCaseName;
    }

    public void setDescriptionHtml(String descriptionHtml) {
        this.descriptionHtml = descriptionHtml;
    }

    public Optional<String> getDescriptionHtml() {
        if (descriptionHtml != null) {
            return Optional.of(descriptionHtml);
        }
        return getDescriptionResourceName().map(ResourceRef::getContent);
    }

    public Optional<ResourceRef> getDescriptionResourceName() {
        return ResourceRef.forClassWithExtension(getClass(), "html");
    }

    public List<ResourceRef> getAditionalSources() {
        return aditionalSources;
    }

    public List<ItemCasePanel.ItemCaseButton> getBotoes() {
        return botoes;
    }


    public AnnotationMode annotation() { return annotationMode;}

    public boolean isDynamic() {
        return false;
    }

    public abstract Optional<ResourceRef> getMainSourceResourceName();

    public ShowCaseType getShowCaseType() {
        return showCaseType;
    }
}
