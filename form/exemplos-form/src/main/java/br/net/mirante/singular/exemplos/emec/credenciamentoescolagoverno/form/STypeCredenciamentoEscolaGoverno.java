/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package br.net.mirante.singular.exemplos.emec.credenciamentoescolagoverno.form;

import br.net.mirante.singular.form.SIComposite;
import br.net.mirante.singular.form.SInfoType;
import br.net.mirante.singular.form.STypeComposite;
import br.net.mirante.singular.form.TypeBuilder;
import br.net.mirante.singular.form.view.SViewByBlock;
import br.net.mirante.singular.form.view.SViewTab;

@SInfoType(spackage = SPackageCredenciamentoEscolaGoverno.class)
public class STypeCredenciamentoEscolaGoverno extends STypeComposite<SIComposite>{

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);
        
        this.asAtr().label("Credenciamento de Escola de Governo");
        
        
        SViewTab tabbed = this.setView(SViewTab::new);
        tabbed.addTab(addField("mantenedora", STypeMantenedora.class), "Mantenedora");
//        tabbed.addTab("mantida", "Mantida");
        tabbed.addTab(addField("corpoDirigenteMembrosCPA", STypeCorpoDirigente.class), "Corpo Dirigente/CPA");
        tabbed.addTab(addField("PDI", STypePDI.class), "Informações do PDI");
        tabbed.addTab(addField("projetoPedagogico", STypePDIProjetoPedagogico.class), "Projeto Pedagógico");
        tabbed.addTab(addField("documentos", STypePDIDocumentos.class), "Documentos");
        tabbed.addTab(addRegimentoEstatuto(), "Regimento/Estatuto");
        // configuração do tamanho da coluna de navegação das abas
        tabbed.navColPreference(2);
        
    }
    
    private STypeComposite<SIComposite> addRegimentoEstatuto() {
        final STypeComposite<SIComposite> regimentoEstatuto = this.addFieldComposite("regimentoEstatuto");
        //TODO - richtext
        regimentoEstatuto.addFieldString("textoRegimento", true)
            .withTextAreaView()
            .asAtrBootstrap().colPreference(12);
        
        regimentoEstatuto.setView(SViewByBlock::new).newBlock("25 Texto do Regimento").add("textoRegimento");
        return regimentoEstatuto;
    }
}
