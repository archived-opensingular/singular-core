/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package br.net.mirante.singular.exemplos.emec.credenciamentoescolagoverno.form;

import br.net.mirante.singular.form.SIComposite;
import br.net.mirante.singular.form.SInfoType;
import br.net.mirante.singular.form.STypeComposite;
import br.net.mirante.singular.form.TypeBuilder;
import br.net.mirante.singular.form.view.SViewTab;

@SInfoType(spackage = SPackageCredenciamentoEscolaGoverno.class)
public class STypeCredenciamentoEscolaGoverno extends STypeComposite<SIComposite>{

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);
        
        this.asAtr().label("Credenciamento de Escola de Governo");
        
        STypeInformacoesPDI informacoesPDI = this.addField("informacoesPDI", STypeInformacoesPDI.class);
        informacoesPDI
            .asAtr().required()
            .label("Informações do PDI");
        
        SViewTab tabbed = this.setView(SViewTab::new);
        tabbed.addTab("mantenedora", "Mantenedora");
        tabbed.addTab("mantida", "Mantida");
        tabbed.addTab("corpoDirigente", "Corpo Dirigente");
        tabbed.addTab("membrosCPA", "Membros da CPA");
        tabbed.addTab(informacoesPDI);
        
        // configuração do tamanho da coluna de navegação das abas
        this.asAtrBootstrap().colPreference(2);
    }
}
