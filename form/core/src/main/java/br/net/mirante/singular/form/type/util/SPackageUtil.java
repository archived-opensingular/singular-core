/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.type.util;

import br.net.mirante.singular.form.PackageBuilder;
import br.net.mirante.singular.form.SDictionary;
import br.net.mirante.singular.form.SInfoPackage;
import br.net.mirante.singular.form.SPackage;
import br.net.mirante.singular.form.STypeComposite;
import br.net.mirante.singular.form.type.basic.AtrBasic;
import br.net.mirante.singular.form.type.basic.SPackageBasic;
import br.net.mirante.singular.form.type.country.brazil.STypeCEP;
import br.net.mirante.singular.form.type.country.brazil.STypeCNPJ;
import br.net.mirante.singular.form.type.country.brazil.STypeCPF;
import br.net.mirante.singular.form.type.country.brazil.STypeTelefoneNacional;

@SInfoPackage(name = SDictionary.SINGULAR_PACKAGES_PREFIX + "util")
public class SPackageUtil extends SPackage {

    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {
        pb.createType(STypeCPF.class);
        pb.createType(STypeCNPJ.class);
        pb.createType(STypeCEP.class);
        pb.createType(STypeEMail.class);
        pb.createType(STypeYearMonth.class);
        pb.createType(STypePersonName.class);
        pb.createType(STypeTelefoneNacional.class);

        pb.addAttribute(STypeYearMonth.class, SPackageBasic.ATR_TAMANHO_EDICAO, 7);

        STypeComposite<?> endereco = pb.createCompositeType("Endereco");
        endereco.addFieldString("rua").as(AtrBasic.class).tamanhoMaximo(50);
        endereco.addFieldString("bairro");
        endereco.addField("cep", STypeCEP.class);
    }
}
