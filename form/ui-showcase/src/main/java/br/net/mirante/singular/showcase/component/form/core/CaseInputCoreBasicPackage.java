/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.component.form.core;

import br.net.mirante.singular.form.PackageBuilder;
import br.net.mirante.singular.form.SPackage;
import br.net.mirante.singular.form.STypeComposite;
import br.net.mirante.singular.form.type.basic.AtrBasic;
import br.net.mirante.singular.form.type.country.brazil.STypeTelefoneNacional;


public class CaseInputCoreBasicPackage extends SPackage {

    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {
        STypeComposite<?> tipoMyForm = pb.createCompositeType("testForm");
        tipoMyForm.addFieldCNPJ("cnpj")
                .as(AtrBasic.class).label("CNPJ");
        tipoMyForm.addFieldCPF("cpf")
                .as(AtrBasic.class).label("CPF");
        tipoMyForm.addFieldCEP("cep")
                .as(AtrBasic.class).label("CEP");
        tipoMyForm.addFieldEmail("email")
                .as(AtrBasic.class).label("E-mail");
        tipoMyForm.addFieldString("descricao")
                .as(AtrBasic.class).label("Descrição");
        tipoMyForm.addField("telefone", STypeTelefoneNacional.class)
                .as(AtrBasic.class).label("Telefone");
        super.carregarDefinicoes(pb);
    }
}
