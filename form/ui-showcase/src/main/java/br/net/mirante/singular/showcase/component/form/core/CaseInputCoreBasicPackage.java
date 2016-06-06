/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.component.form.core;

import br.net.mirante.singular.form.PackageBuilder;
import br.net.mirante.singular.form.SPackage;
import br.net.mirante.singular.form.STypeComposite;
import br.net.mirante.singular.form.type.basic.AtrBasic;
import br.net.mirante.singular.form.type.country.brazil.STypeCEP;
import br.net.mirante.singular.form.type.country.brazil.STypeCNPJ;
import br.net.mirante.singular.form.type.country.brazil.STypeCPF;
import br.net.mirante.singular.form.type.country.brazil.STypeTelefoneNacional;
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
