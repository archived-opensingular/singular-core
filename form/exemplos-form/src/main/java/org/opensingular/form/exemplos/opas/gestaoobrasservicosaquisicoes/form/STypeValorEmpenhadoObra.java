/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.opensingular.form.exemplos.opas.gestaoobrasservicosaquisicoes.form;

import org.opensingular.form.SIComposite;
import org.opensingular.form.SInfoType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.TypeBuilder;
import org.opensingular.form.type.core.STypeInteger;
import org.opensingular.form.type.core.STypeMonetary;

@SInfoType(spackage = SPackageGestaoObrasServicosAquisicoes.class)
public class STypeValorEmpenhadoObra extends STypeComposite<SIComposite>{

    public static final String FIELD_VALOR_EMPENHADO = "valorEmpenhado";
    public static final String FIELD_EXERCICIO = "exercicio";

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);
        
        final STypeInteger exercicio = addFieldInteger(FIELD_EXERCICIO, true);
        exercicio.asAtr().label("Exercício");
        exercicio.selection()
            .selfIdAndDisplay()
            .simpleProviderOf(2016, 2017, 2018, 2019);
        addFieldMonetary(FIELD_VALOR_EMPENHADO)
            .withRequired(true).asAtr().label("Valor Empenhado");
    }

    public STypeInteger getFieldExercicio(){
        return (STypeInteger) getField(FIELD_EXERCICIO);
    }

    public STypeMonetary getFieldValorEmpenhado(){
        return (STypeMonetary) getField(FIELD_VALOR_EMPENHADO);
    }
}
