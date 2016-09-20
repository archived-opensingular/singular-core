/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package br.net.mirante.singular.exemplos.opas.gestaoobrasservicosaquisicoes.form;

import br.net.mirante.singular.form.SIComposite;
import br.net.mirante.singular.form.SInfoType;
import br.net.mirante.singular.form.STypeComposite;
import br.net.mirante.singular.form.STypeList;
import br.net.mirante.singular.form.TypeBuilder;
import br.net.mirante.singular.form.type.core.STypeString;
import br.net.mirante.singular.form.view.SViewByBlock;
import br.net.mirante.singular.form.view.SViewListByTable;

@SInfoType(spackage = SPackageGestaoObrasServicosAquisicoes.class)
public class STypeObra extends STypeComposite<SIComposite>{

    private static final String FIELD_NUM_CONTRATO = "numContrato";
    private static final String FIELD_VALORES_EMPENHADOS = "valoresEmpenhados";

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);
        
        addFieldString(FIELD_NUM_CONTRATO, true)
            .asAtr().label("Nº Contrato").asAtrBootstrap().colPreference(3);
        addFieldInteger("descricaoObra", true)
            .asAtr().label("Descrição da Obra").asAtrBootstrap().colPreference(5);
        addFieldDate("dataInicio", true)
            .asAtr().label("Início").asAtrBootstrap().colPreference(2);
        addFieldDate("dataFim", true)
            .asAtr().label("Fim").asAtrBootstrap().colPreference(2);
        
        addValoresEmpenhados();
        
        setView(SViewByBlock::new)
            .newBlock().add("numContrato", "descricaoObra", "dataInicio", "dataFim")
            .newBlock("Valor Empenhado").add(FIELD_VALORES_EMPENHADOS);
    }

    private void addValoresEmpenhados() {
        final STypeList<STypeValorEmpenhadoObra, SIComposite> valoresEmpenhados = addFieldListOf(FIELD_VALORES_EMPENHADOS, STypeValorEmpenhadoObra.class);
        valoresEmpenhados.withMiniumSizeOf(1).withView(SViewListByTable::new);
    }
    
    @SuppressWarnings("unchecked")
    public STypeList<STypeValorEmpenhadoObra, SIComposite> getFieldValoresEmpenhados(){
        return (STypeList<STypeValorEmpenhadoObra, SIComposite>) getField(FIELD_VALORES_EMPENHADOS);
    }
    
    public STypeString getFieldNumContrato(){
        return (STypeString) getField(FIELD_NUM_CONTRATO);
    }
}
