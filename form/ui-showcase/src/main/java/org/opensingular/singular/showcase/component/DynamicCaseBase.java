/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.showcase.component;

import java.util.Optional;

import org.opensingular.singular.form.PackageBuilder;
import org.opensingular.singular.form.SDictionary;
import org.opensingular.singular.form.SType;
import org.opensingular.singular.form.STypeComposite;
import org.opensingular.singular.form.type.core.STypeString;

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
