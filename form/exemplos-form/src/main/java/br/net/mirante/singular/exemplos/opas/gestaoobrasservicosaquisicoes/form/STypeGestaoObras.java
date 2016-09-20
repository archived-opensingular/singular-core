/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package br.net.mirante.singular.exemplos.opas.gestaoobrasservicosaquisicoes.form;

import br.net.mirante.singular.form.SIComposite;
import br.net.mirante.singular.form.SInfoType;
import br.net.mirante.singular.form.STypeComposite;
import br.net.mirante.singular.form.TypeBuilder;
import br.net.mirante.singular.form.view.SViewTab;

@SInfoType(spackage = SPackageGestaoObrasServicosAquisicoes.class)
public class STypeGestaoObras extends STypeComposite<SIComposite>{

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);
        
        this.asAtr().label("Gestão de Obras");
        
        
        SViewTab tabbed = this.setView(SViewTab::new);
        tabbed.addTab(addField("checklist", STypeChecklist.class), "Checklist");
        tabbed.addTab(addField("processo", STypeProcesso.class), "Processo");
        
        // configuração do tamanho da coluna de navegação das abas
        tabbed.navColPreference(1).navColMd(2).navColSm(2).navColXs(3);
        
    }
}
