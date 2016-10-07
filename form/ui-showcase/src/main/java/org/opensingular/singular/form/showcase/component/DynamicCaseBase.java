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

import java.util.Optional;

import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SDictionary;
import org.opensingular.form.SType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.type.core.STypeString;

public class DynamicCaseBase extends CaseBaseForm {

    private String packageName = "teste";
    private String typeName = "endereco";

    public DynamicCaseBase(String componentName) {
        super(componentName);
    }

    public DynamicCaseBase(String componentName, String subCaseName) {
        super(componentName, subCaseName);
    }

    @Override
    public String getTypeName() {
        return packageName + "." + typeName;
    }

    @Override
    public SType<?> getCaseType() {
        SDictionary dicionario = SDictionary.create();
        PackageBuilder pb = dicionario.createNewPackage(packageName);
        STypeComposite<?> tipoEndereco = pb.createCompositeType(typeName);
        tipoEndereco.addField("rua", STypeString.class).asAtr().label("Rua");
        tipoEndereco.addFieldString("bairro", true).asAtr().label("Bairro");
        tipoEndereco.addFieldInteger("cep", true).asAtr().label("CEP");

        STypeComposite<?> tipoClassificacao = tipoEndereco.addFieldComposite("classificacao");
        tipoClassificacao.asAtr().label("Classificação");
        tipoClassificacao.addFieldInteger("prioridade").asAtr().label("Prioridade");
        tipoClassificacao.addFieldString("descricao").asAtr().label("Descrição");

        return tipoEndereco;
    }

    @Override
    public Optional<ResourceRef> getMainSourceResourceName() {
        return Optional.empty();
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
