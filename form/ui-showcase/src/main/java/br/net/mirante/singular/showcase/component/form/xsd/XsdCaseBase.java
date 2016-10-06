/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.component.form.xsd;

import java.util.Optional;

import org.opensingular.singular.form.PackageBuilder;
import org.opensingular.singular.form.SDictionary;
import org.opensingular.singular.form.SType;
import org.opensingular.singular.form.io.FormXsdUtil;
import br.net.mirante.singular.showcase.component.CaseBaseForm;
import br.net.mirante.singular.showcase.component.ResourceRef;

public abstract class XsdCaseBase extends CaseBaseForm {

    private final String xsdFileName;

    private String packageName = "xsd";
    private String typeName;

    public XsdCaseBase(String xsdFileName, String componentName) {
        super(componentName, null);
        this.xsdFileName = xsdFileName;
    }

    public XsdCaseBase(String xsdFileName, String componentName, String subCaseName) {
        super(componentName, subCaseName);
        this.xsdFileName = xsdFileName;
    }

    @Override
    public String getTypeName() {
        if (typeName == null) {
            typeName = getCaseType().getName();
        }
        return typeName;
    }

    @Override
    public SType<?> getCaseType() {

        SDictionary dicionario = SDictionary.create();
        PackageBuilder pb = dicionario.createNewPackage(packageName);
        return FormXsdUtil.xsdToSType(pb, getMainSourceResourceName().get().getContent());

    }

    @Override
    public Optional<ResourceRef> getMainSourceResourceName() {
        return ResourceRef.forClassWithExtension(getClass(), "xsd");

    }

    @Override
    public boolean isDynamic() {
        return true;
    }

    @Override
    public Optional<String> getDescriptionHtml() {
        return Optional.of("Este form foi gerado a partir de um XSD.");
    }
}
