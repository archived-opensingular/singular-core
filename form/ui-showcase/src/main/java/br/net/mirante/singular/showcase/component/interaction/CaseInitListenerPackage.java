/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.component.interaction;

import java.util.Arrays;
import java.util.Optional;

import br.net.mirante.singular.form.mform.PackageBuilder;
import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SIList;
import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.SPackage;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.STypeList;
import br.net.mirante.singular.form.mform.basic.view.SViewListByForm;
import br.net.mirante.singular.form.mform.core.STypeInteger;
import br.net.mirante.singular.form.mform.core.STypeString;

public class CaseInitListenerPackage extends SPackage {

    private STypeList<STypeComposite<SIComposite>, SIComposite> itens;
    private STypeString nome;

    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {
        super.carregarDefinicoes(pb);

        final STypeComposite<SIComposite> testForm = pb.createCompositeType("testForm");
        itens = testForm.addFieldListOfComposite("itens", "itenm");
        itens.asAtrBasic().label("Itens");
        itens.withView(new SViewListByForm().disableDelete().disableNew());

        final STypeComposite<SIComposite> item = itens.getElementsType();
        nome = item.addFieldString("nome");
        nome
                .asAtrBasic().label("Nome").enabled(false)
                .asAtrBootstrap().colPreference(3);

        final STypeInteger quantidade = item.addFieldInteger("quantidade");
        quantidade
                .asAtrBasic().label("Quantidade")
                .asAtrBootstrap().colPreference(2);

        //@destacar
        testForm.withInitListener(this::initForm);

    }

    private void initForm(SInstance instance) {
        for (String n : Arrays.asList("Mauro", "Laura")) {
            final Optional<SIList<SIComposite>> itensList = instance.findNearest(itens);
            itensList.ifPresent(il -> initItem(il, n));
        }
    }

    private void initItem(SIList<SIComposite> list, String nomeItem) {
        final SIComposite item = list.addNew();
        item.findNearest(nome)
                .ifPresent(n -> n.setValue(nomeItem));
    }
}
