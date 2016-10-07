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
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.view.SViewByBlock;
import org.opensingular.form.view.SViewListByTable;

@SInfoType(spackage = SPackageGestaoObrasServicosAquisicoes.class)
public class STypeObra extends STypeComposite<SIComposite>{

    public static final String FIELD_NUM_CONTRATO = "numContrato";
    public static final String FIELD_VALORES_EMPENHADOS = "valoresEmpenhados";

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);
        
        addFieldString(FIELD_NUM_CONTRATO, true)
            .asAtr().label("Nº Contrato").asAtrBootstrap().colPreference(4);
        addFieldString("descricaoObra", true)
            .asAtr().label("Descrição da Obra").asAtrBootstrap().colPreference(8);
        addFieldDate("dataInicio", true)
            .asAtr().label("Início").asAtrBootstrap().colPreference(3);
        addFieldDate("dataFim", true)
            .asAtr().label("Fim").asAtrBootstrap().colPreference(3);
        
        addValoresEmpenhados();
        
        setView(SViewByBlock::new)
            .newBlock("Dados da Obra").add("numContrato", "descricaoObra", "dataInicio", "dataFim")
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
