/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package br.net.mirante.singular.exemplos.opas.gestaoobrasservicosaquisicoes.form;

import java.util.Arrays;
import java.util.List;

import br.net.mirante.singular.form.SIComposite;
import br.net.mirante.singular.form.SInfoType;
import br.net.mirante.singular.form.STypeComposite;
import br.net.mirante.singular.form.STypeList;
import br.net.mirante.singular.form.TypeBuilder;
import br.net.mirante.singular.form.type.core.SIString;
import br.net.mirante.singular.form.type.core.STypeString;
import br.net.mirante.singular.form.view.SViewListByTable;

@SInfoType(spackage = SPackageGestaoObrasServicosAquisicoes.class)
public class STypeChecklist extends STypeComposite<SIComposite> {

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);

        final STypeString acao = this.addFieldString("acao", true);
        acao.selectionOf("Edificações", "Saneamento")
            .asAtr().label("Ação").asAtrBootstrap().colPreference(12);
        
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
            .selectionOf("Sim", "Parcial", "Não", "Não se aplica")
            .withRadioView()
            .asAtr().label("Checklist")
            .asAtrBootstrap().colPreference(3);
        
        itens.withUpdateListener(itensInstance -> {
            SIString acaoInstance = itensInstance.findNearest(acao).get();
            itensInstance.removeChildren();
//            itensInstance.clearInstance();
            if(acaoInstance.getValue() != null){
                if(acaoInstance.getValue().equals("Edificações")){
                    getItensEdificacoes().stream().forEach(item_ -> itensInstance.addNew().setValue(descrItem, item_));
                } else if(acaoInstance.getValue().equals("Saneamento")){
                    getItensSaneamento().stream().forEach(item_ -> itensInstance.addNew().setValue(descrItem, item_));
                }
            }
        });
        
        this.addFieldBoolean("confirmacao", true)
            .addInstanceValidator(confirmacao -> {
                if(!confirmacao.getInstance().getValue()){
                    confirmacao.error("Selecione: Todos os dados informados estão corredos");
                }
            })
            .asAtrBootstrap().colPreference(12)
            .asAtr().label("Todos os dados informados estão corredos");
    }

    private List<String> getItensEdificacoes(){
        return Arrays.asList("Projeto", "Plano de Execução", "Planta Baixa");
    }

    private List<String> getItensSaneamento(){
        return Arrays.asList("Projeto de Sistema de Abastecimento de Água ", "Projeto de Estação de Tratamento de Água");
    }
}
