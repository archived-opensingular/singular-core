/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.opensingular.form.exemplos.opas.gestaoobrasservicosaquisicoes.form;

import org.opensingular.form.SIComposite;
import org.opensingular.form.SInfoType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.STypeList;
import org.opensingular.form.TypeBuilder;
import org.opensingular.form.type.core.SIString;
import org.opensingular.form.type.core.STypeInteger;
import org.opensingular.form.type.core.STypeMonetary;
import org.opensingular.form.type.core.STypeString;

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
