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
import br.net.mirante.singular.form.type.core.SIString;
import br.net.mirante.singular.form.type.core.STypeInteger;
import br.net.mirante.singular.form.type.core.STypeMonetary;
import br.net.mirante.singular.form.type.core.STypeString;

@SInfoType(spackage = SPackageGestaoObrasServicosAquisicoes.class)
public class STypeEncaminhamento extends STypeComposite<SIComposite>{

    public static final String FIELD_VALOR_EMPENHADO = "valorEmpenhado";
    public static final String FIELD_EXERCICIO = "exercicio";

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);
        
        addFieldString("responsavel", true).asAtr().label("Responsável");
        addFieldDateTime("dataHora", true).asAtr().label("Data");
        addFieldString("detalhamento", true).withTextAreaView().asAtr().label("Detalhamento");
        
        final STypeList<STypeString, SIString> pendencias = addFieldListOf("pendencias", STypeString.class);
        pendencias.getElementsType().asAtr().label("Descrição");
        pendencias.asAtr().itemLabel("Pendência");
    }

    public STypeInteger getFieldExercicio(){
        return (STypeInteger) getField(FIELD_EXERCICIO);
    }

    public STypeMonetary getFieldValorEmpenhado(){
        return (STypeMonetary) getField(FIELD_VALOR_EMPENHADO);
    }
}
