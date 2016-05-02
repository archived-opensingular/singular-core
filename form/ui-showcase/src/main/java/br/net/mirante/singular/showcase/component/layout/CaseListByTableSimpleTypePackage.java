/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.component.layout;

import br.net.mirante.singular.form.mform.PackageBuilder;
import br.net.mirante.singular.form.mform.SPackage;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.STypeList;
import br.net.mirante.singular.form.mform.basic.view.SViewListByTable;
import br.net.mirante.singular.form.mform.core.SIString;
import br.net.mirante.singular.form.mform.core.STypeString;

public class CaseListByTableSimpleTypePackage extends SPackage {

    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {

        STypeComposite<?>                testForm = pb.createCompositeType("testForm");
        STypeList<STypeString, SIString> nomes    = testForm.addFieldListOf("nomes", STypeString.class);

        nomes.withView(SViewListByTable::new);
        nomes.withMiniumSizeOf(2);
        nomes.asAtrBootstrap().colPreference(6);
        nomes.asAtr().label("Nomes");

        nomes.getElementsType().asAtr().required();
    }

}
