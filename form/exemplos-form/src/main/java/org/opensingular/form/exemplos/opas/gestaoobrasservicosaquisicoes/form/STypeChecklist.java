/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.opensingular.form.exemplos.opas.gestaoobrasservicosaquisicoes.form;

import org.opensingular.form.exemplos.opas.gestaoobrasservicosaquisicoes.enums.AcaoGestaoObras;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SInfoType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.STypeList;
import org.opensingular.form.TypeBuilder;
import org.opensingular.form.type.core.SIString;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.view.SViewListByTable;

@SInfoType(spackage = SPackageGestaoObrasServicosAquisicoes.class)
public class STypeChecklist extends STypeComposite<SIComposite> {

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);

        final STypeString acao = this.addFieldString("acao", true);
        acao.selectionOfEnum(AcaoGestaoObras.class)
            .asAtr().label("Ação").asAtrBootstrap().colPreference(3);
        
        final STypeList<STypeComposite<SIComposite>, SIComposite> itens = this.addFieldListOfComposite("itens", "item");
        itens.asAtr()
            .itemLabel("Item")
            .required()
            .dependsOn(acao)
            .visible(itensInstance -> {
                SIString acaoInstance = itensInstance.findNearest(acao).get();
                return acaoInstance.getValue() != null;
            });
        itens.setView(SViewListByTable::new).setDeleteEnabled(false).setNewEnabled(false);

        final STypeComposite<SIComposite> item = itens.getElementsType();
        final STypeString descrItem = item.addFieldString("descricao");
        descrItem.asAtr().label("Descrição").enabled(false);
        item.addFieldString("avaliacao", true)
            .selectionOf("Sim", "Não", "Parcial", "Não se aplica")
            .withRadioView()
            .asAtr().label("Checklist")
            .asAtrBootstrap().colPreference(3);
        
        itens.withUpdateListener(itensInstance -> {
            SIString acaoInstance = itensInstance.findNearest(acao).get();
            itensInstance.removeChildren();
//            itensInstance.clearInstance();
            AcaoGestaoObras acaoSelecionada = acaoInstance.getValue(AcaoGestaoObras.class);
            if(acaoSelecionada != null){
                acaoSelecionada.getChecklistItens().stream().forEach(item_ -> itensInstance.addNew().setValue(descrItem, item_));
            }
        });
        
        this.addFieldBoolean("confirmacao", true)
            .addInstanceValidator(confirmacao -> {
                if(!confirmacao.getInstance().getValue()){
                    confirmacao.error("Selecione: Todos os dados informados estão corretos");
                }
            })
            .asAtrBootstrap().colPreference(12)
            .asAtr().label("Todos os dados informados estão corretos");
    }
}
