/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.component.layout;

import br.net.mirante.singular.form.mform.PackageBuilder;
import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SPackage;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.STypeList;
import br.net.mirante.singular.form.mform.basic.view.SViewListByTable;
import br.net.mirante.singular.form.mform.core.STypeDate;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.mform.util.comuns.STypeYearMonth;

public class CaseListByTableMinimiumAndMaximumPackage extends SPackage {

    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {

        STypeComposite<?> testForm = pb.createCompositeType("testForm");

        final STypeList<STypeComposite<SIComposite>, SIComposite> certificacoes = testForm.addFieldListOfComposite("certificacoes", "certificacao");
        certificacoes.asAtrBasic().label("Certificações");
        final STypeComposite<?> certificacao = certificacoes.getElementsType();
        final STypeYearMonth dataCertificacao = certificacao.addField("data", STypeYearMonth.class, true);
        final STypeString entidadeCertificacao = certificacao.addFieldString("entidade", true);
        final STypeDate validadeCertificacao = certificacao.addFieldDate("validade");
        final STypeString nomeCertificacao = certificacao.addFieldString("nome", true);
        {
            certificacoes
                    //@destacar:bloco
                    .withMiniumSizeOf(2)
                    .withMaximumSizeOf(3)
                     //@destacar:fim
                    .withView(SViewListByTable::new)
                    .asAtrBasic().label("Certificações");
            certificacao
                    .asAtrBasic().label("Certificação");
            dataCertificacao
                    .asAtrBasic().label("Data")
                    .asAtrBootstrap().colPreference(2);
            entidadeCertificacao
                    .asAtrBasic().label("Entidade")
                    .asAtrBootstrap().colPreference(4);
            validadeCertificacao
                    .asAtrBasic().label("Validade")
                    .asAtrBootstrap().colPreference(2);
            nomeCertificacao
                    .asAtrBasic().label("Nome")
                    .asAtrBootstrap().colPreference(4);
        }
    }
}
