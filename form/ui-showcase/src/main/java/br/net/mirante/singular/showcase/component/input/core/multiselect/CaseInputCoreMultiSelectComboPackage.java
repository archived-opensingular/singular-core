/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.component.input.core.multiselect;

import br.net.mirante.singular.form.mform.PackageBuilder;
import br.net.mirante.singular.form.mform.SPackage;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.STypeList;
import br.net.mirante.singular.form.mform.basic.view.SMultiSelectionBySelectView;
import br.net.mirante.singular.form.mform.core.SIString;
import br.net.mirante.singular.form.mform.core.STypeString;

public class CaseInputCoreMultiSelectComboPackage extends SPackage {

    //@formatter:off
    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {

        STypeComposite<?> tipoMyForm = pb.createCompositeType("testForm");

        STypeString tipoContato = pb.createType("tipoContato", STypeString.class);
        tipoContato.withSelectionOf("Endereço", "Email", "Telefone", "Celular", "Fax");

        STypeString tipoArquivo = pb.createType("tipoArquivo", STypeString.class);
            tipoArquivo.withSelectionFromProvider("filesChoiceProvider");

        STypeList<STypeString, SIString> infoPub1 = tipoMyForm
                .addFieldListOf("infoPub1", tipoContato);

        infoPub1
            .withView(SMultiSelectionBySelectView::new)
            .asAtrBasic().label("Informações Públicas");

        STypeList<STypeString, SIString> infoArq = tipoMyForm
                .addFieldListOf("infoArq", tipoArquivo);
        infoArq
            .withView(SMultiSelectionBySelectView::new)
            .asAtrBasic().label("Arquivos Persistidos");
    }
}