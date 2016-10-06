/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.opensingular.singular.exemplos.opas.gestaoobrasservicosaquisicoes.form;

import org.opensingular.singular.form.SIComposite;
import org.opensingular.singular.form.SInfoType;
import org.opensingular.singular.form.STypeComposite;
import org.opensingular.singular.form.TypeBuilder;
import org.opensingular.singular.form.view.SViewTab;

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
