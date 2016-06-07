/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.component;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import br.net.mirante.singular.form.SDictionary;
import br.net.mirante.singular.form.SPackage;
import br.net.mirante.singular.form.SType;
import br.net.mirante.singular.form.SingularFormException;
import br.net.mirante.singular.form.wicket.enums.AnnotationMode;
import br.net.mirante.singular.showcase.view.page.ItemCasePanel;

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
