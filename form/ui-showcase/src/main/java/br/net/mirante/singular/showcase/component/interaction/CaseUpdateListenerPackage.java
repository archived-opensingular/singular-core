/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.component.interaction;

import java.util.Optional;

import br.net.mirante.singular.form.mform.PackageBuilder;
import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SPackage;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.core.SIString;
import br.net.mirante.singular.form.mform.core.STypeString;

public class CaseUpdateListenerPackage extends SPackage {

    private STypeString cep;

    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {
        super.carregarDefinicoes(pb);

        final STypeComposite<SIComposite> testForm = pb.createCompositeType("testForm");
        testForm.asAtrBasic().label("Endereço");
        cep = testForm.addFieldString("cep");
        cep.asAtrBasic().tamanhoMaximo(8).label("CEP (Use os valores 70863520 ou 70070120)");
        final STypeString logradouro = testForm.addFieldString("logradouro");
        logradouro
                .asAtrBasic().enabled(false)
                .label("Logradouro")
                .dependsOn(cep);
        //@destacar
        logradouro.withUpdateListener(this::pesquisarLogradouro);

    }

    private void pesquisarLogradouro(SIString instance) {
        final Optional<SIString> cepField = instance.findNearest(cep);
        cepField.ifPresent(c -> {
            if (c.getValue().equalsIgnoreCase("70863520")) {
                instance.setValue("CLN 211 Bloco 'B' Subsolo");
            } else if (c.getValue().equalsIgnoreCase("70070120")) {
                instance.setValue("SBS - Qd. 02 - Bl. Q - Centro Empresarial João Carlos Saad 12° andar");
            } else {
                instance.setValue("Não encontrado");
            }
        });
    }

}
