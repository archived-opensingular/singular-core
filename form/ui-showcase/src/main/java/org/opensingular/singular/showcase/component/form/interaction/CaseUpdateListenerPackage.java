/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.showcase.component.form.interaction;

import java.util.Optional;

import org.opensingular.singular.form.PackageBuilder;
import org.opensingular.singular.form.SIComposite;
import org.opensingular.singular.form.SPackage;
import org.opensingular.singular.form.STypeComposite;
import org.opensingular.singular.form.type.core.SIString;
import org.opensingular.singular.form.type.core.STypeString;
import org.opensingular.singular.showcase.component.CaseItem;
import org.opensingular.singular.showcase.component.Group;

/**
 * Listener que é executado quando um dependsOn é executado
 */
@CaseItem(componentName = "Listeners", subCaseName = "Update listener", group = Group.INTERACTION)
public class CaseUpdateListenerPackage extends SPackage {

    private STypeString cep;

    @Override
    protected void onLoadPackage(PackageBuilder pb) {
        super.onLoadPackage(pb);

        final STypeComposite<SIComposite> testForm = pb.createCompositeType("testForm");
        testForm.asAtr().label("Endereço");
        cep = testForm.addFieldString("cep");
        cep.asAtr().maxLength(8).label("CEP (Use os valores 70863520 ou 70070120)");
        final STypeString logradouro = testForm.addFieldString("logradouro");
        logradouro
                .asAtr().enabled(false)
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
