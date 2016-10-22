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

import org.opensingular.form.*;
import org.opensingular.form.type.core.STypeMonetary;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.view.SViewByBlock;
import org.opensingular.form.view.SViewListByTable;

@SInfoType(spackage = SPackageGestaoObrasServicosAquisicoes.class)
public class STypeObra extends STypeComposite<SIComposite>{

    public static final String FIELD_VALOR_CONTRATADO = "valorContratado";
    public static final String FIELD_VALOR_SOLICITADO = "valorSolicitado";
    public static final String FIELD_DATA_FIM = "dataFim";
    public static final String FIELD_DATA_INICIO = "dataInicio";
    public static final String FIELD_DESCRICAO_OBRA = "descricaoObra";
    public static final String FIELD_NUM_CONTRATO = "numContrato";
    public static final String FIELD_VALORES_EMPENHADOS = "valoresEmpenhados";

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);
        
        addFieldString(FIELD_NUM_CONTRATO, true)
            .asAtr().label("Nº Contrato").asAtrBootstrap().colPreference(4);
        addFieldString(FIELD_DESCRICAO_OBRA, true)
            .asAtr().label("Descrição da Obra").asAtrBootstrap().colPreference(8);
        addFieldDate(FIELD_DATA_INICIO, true)
            .asAtr().label("Início").asAtrBootstrap().colPreference(3);
        addFieldDate(FIELD_DATA_FIM, true)
            .asAtr().label("Fim").asAtrBootstrap().colPreference(3);

        addFieldMonetary(FIELD_VALOR_SOLICITADO)
            .asAtr().label("Valor Solicitado").asAtrBootstrap().colPreference(3);
        addFieldMonetary(FIELD_VALOR_CONTRATADO)
            .asAtr().label("Valor Contratado").asAtrBootstrap().colPreference(3);
        
        addValoresEmpenhados();
        
        setView(SViewByBlock::new)
            .newBlock("Dados da Obra").add(FIELD_NUM_CONTRATO, FIELD_DESCRICAO_OBRA, FIELD_DATA_INICIO, FIELD_DATA_FIM)
            .newBlock("Orçamento").add(FIELD_VALOR_SOLICITADO, FIELD_VALOR_CONTRATADO, FIELD_VALORES_EMPENHADOS);
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

    public STypeMonetary getFieldValorSolicitado(){
        return (STypeMonetary) getField(FIELD_VALOR_SOLICITADO);
    }
    
    public STypeMonetary getFieldValorContratado(){
        return (STypeMonetary) getField(FIELD_VALOR_CONTRATADO);
    }
}
