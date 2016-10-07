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
