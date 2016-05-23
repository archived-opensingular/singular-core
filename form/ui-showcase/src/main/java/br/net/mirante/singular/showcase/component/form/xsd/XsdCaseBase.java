/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.component.form.xsd;

import java.util.Optional;

import br.net.mirante.singular.form.PackageBuilder;
import br.net.mirante.singular.form.SDictionary;
import br.net.mirante.singular.form.SType;
import br.net.mirante.singular.form.io.FormXsdUtil;
import br.net.mirante.singular.showcase.component.CaseBase;
import br.net.mirante.singular.showcase.component.ResourceRef;

public abstract class XsdCaseBase extends CaseBase {

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
