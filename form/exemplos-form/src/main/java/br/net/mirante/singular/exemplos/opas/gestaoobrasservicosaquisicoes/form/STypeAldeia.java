/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package br.net.mirante.singular.exemplos.opas.gestaoobrasservicosaquisicoes.form;

import br.net.mirante.singular.form.SIComposite;
import br.net.mirante.singular.form.SInfoType;
import br.net.mirante.singular.form.STypeComposite;
import br.net.mirante.singular.form.TypeBuilder;
import br.net.mirante.singular.form.type.core.STypeInteger;
import br.net.mirante.singular.form.type.core.STypeString;

@SInfoType(spackage = SPackageGestaoObrasServicosAquisicoes.class)
public class STypeAldeia extends STypeComposite<SIComposite>{

    public static final String FIELD_POPULACAO = "populacao";
    public static final String FIELD_QTD_FAMILIAS = "qtdFamilias";
    public static final String FIELD_NOME = "nome";

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);
        
        addFieldString(FIELD_NOME, true).asAtr().label("Aldeia");
        addFieldInteger(FIELD_QTD_FAMILIAS, true).asAtr().label("Nº Famílias");
        addFieldInteger(FIELD_POPULACAO, true).asAtr().label("População");
    }
    
    public STypeAldeia mockSelection(){
        selection()
            .id(getFieldNome()).display(getFieldNome())
            .simpleProvider(instance -> {
                instance.add().set(getFieldNome(), "Baniuas").set(getFieldQtdFamilias(), 1035).set(getFieldPopulacao(), 5141 );
                instance.add().set(getFieldNome(), "Guaranis").set(getFieldQtdFamilias(), 13789).set(getFieldPopulacao(), 34350);
                instance.add().set(getFieldNome(), "Uapixanas").set(getFieldQtdFamilias(), 1273).set(getFieldPopulacao(), 6589);
                instance.add().set(getFieldNome(), "Caiapós").set(getFieldQtdFamilias(), 2357).set(getFieldPopulacao(), 7096);
            });
        return this;
    }

    public STypeInteger getFieldPopulacao() {
        return (STypeInteger) getField(FIELD_POPULACAO);
    }

    public STypeInteger getFieldQtdFamilias() {
        return (STypeInteger) getField(FIELD_QTD_FAMILIAS);
    }

    public STypeString getFieldNome() {
        return (STypeString) getField(FIELD_NOME);
    }
}
