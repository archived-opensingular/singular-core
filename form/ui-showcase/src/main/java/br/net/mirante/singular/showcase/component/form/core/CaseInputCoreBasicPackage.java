/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.component.form.core;

import org.opensingular.singular.form.PackageBuilder;
import org.opensingular.singular.form.SPackage;
import org.opensingular.singular.form.STypeComposite;
import org.opensingular.singular.form.type.country.brazil.STypeCEP;
import org.opensingular.singular.form.type.country.brazil.STypeCNPJ;
import org.opensingular.singular.form.type.country.brazil.STypeCPF;
import org.opensingular.singular.form.type.country.brazil.STypeTelefoneNacional;
import br.net.mirante.singular.showcase.component.CaseItem;
import br.net.mirante.singular.showcase.component.Group;

/**
 * Campos básicos para uso nos formulários do singular
 */

@CaseItem(componentName = "Basic", group = Group.INPUT)
public class CaseInputCoreBasicPackage extends SPackage {

    @Override
    protected void onLoadPackage(PackageBuilder pb) {
        STypeComposite<?> tipoMyForm = pb.createCompositeType("testForm");
        tipoMyForm.addField("cnpj", STypeCNPJ.class);
        tipoMyForm.addField("cpf", STypeCPF.class);
        tipoMyForm.addField("cep", STypeCEP.class);
        tipoMyForm.addFieldEmail("email");
        tipoMyForm.addFieldString("descricao")
                .asAtr().label("Descrição");
        tipoMyForm.addField("telefone", STypeTelefoneNacional.class);
        super.onLoadPackage(pb);
    }
}
