/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.form.showcase.component;

import org.opensingular.form.SDictionary;
import org.opensingular.form.SPackage;
import org.opensingular.form.SType;
import org.opensingular.form.SingularFormException;
import org.opensingular.form.wicket.enums.AnnotationMode;

import java.util.Optional;


public class CaseBaseForm extends CaseBase {

    private transient SType<?> caseType;

    public CaseBaseForm(Class<?> caseClass, String componentName, String subCaseName, AnnotationMode annotation) {
        super(caseClass, ShowCaseType.FORM, componentName, subCaseName, annotation);
    }

    public CaseBaseForm(String componentName) {
        super(componentName);
    }

    public CaseBaseForm(String componentName, String subCaseName) {
        super(componentName, subCaseName);
    }

    @SuppressWarnings("unchecked")
    private Class<? extends SPackage> getPackage() {
        return (Class<? extends SPackage>) caseClass;
    }


    public String getTypeName() {
        return getPackage().getName() + ".testForm";
    }

    public SType<?> getCaseType() {
        if (caseType == null) {
            SDictionary dicionario = SDictionary.create();
            SPackage p = dicionario.loadPackage(getPackage());

            caseType = p.getLocalTypeOptional("testForm")
                    .orElseThrow(() -> new SingularFormException("O pacote " + p.getName() + " não define o tipo para exibição 'testForm'"));
        }
        return caseType;
    }


    @Override
    public Optional<ResourceRef> getMainSourceResourceName() {
        return ResourceRef.forSource(getPackage());
    }


    public boolean showValidateButton() {
        return getCaseType().hasAnyValidation();
    }


}
