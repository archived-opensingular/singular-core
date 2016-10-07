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

package org.opensingular.singular.form.showcase.component.form.xsd;

import java.util.Optional;

import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SDictionary;
import org.opensingular.form.SType;
import org.opensingular.form.io.FormXsdUtil;
import org.opensingular.singular.form.showcase.component.CaseBaseForm;
import org.opensingular.singular.form.showcase.component.ResourceRef;

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
