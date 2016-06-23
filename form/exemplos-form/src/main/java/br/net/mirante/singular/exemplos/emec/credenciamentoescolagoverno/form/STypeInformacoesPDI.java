/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package br.net.mirante.singular.exemplos.emec.credenciamentoescolagoverno.form;

import br.net.mirante.singular.form.SIComposite;
import br.net.mirante.singular.form.SInfoType;
import br.net.mirante.singular.form.STypeComposite;
import br.net.mirante.singular.form.TypeBuilder;

@SInfoType(spackage = SPackageCredenciamentoEscolaGoverno.class)
public class STypeInformacoesPDI extends STypeComposite<SIComposite>{

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);
        
        addField("PDI", STypePDI.class);
        addField("projetoPedagogico", STypePDIProjetoPedagogico.class);
        addField("documentos", STypePDIDocumentos.class);
        addRegimentoEstatuto();
    }

    private void addRegimentoEstatuto() {
        final STypeComposite<SIComposite> regimentoEstatuto = this.addFieldComposite("regimentoEstatuto");
        regimentoEstatuto.asAtr().label("Regimento/Estatuto");
        //TODO - richtext
        regimentoEstatuto.addFieldString("textoRegimento", true)
            .withTextAreaView().asAtr().label("Texto do Regimento")
            .asAtrBootstrap().colPreference(12);
    }
}
