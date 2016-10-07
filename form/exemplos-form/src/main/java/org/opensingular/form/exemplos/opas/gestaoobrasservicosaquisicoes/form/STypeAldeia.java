/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.opensingular.form.exemplos.opas.gestaoobrasservicosaquisicoes.form;

import org.opensingular.form.SIComposite;
import org.opensingular.form.SInfoType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.TypeBuilder;
import org.opensingular.form.type.core.STypeInteger;
import org.opensingular.form.type.core.STypeString;

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
