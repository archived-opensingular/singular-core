/*
 *
 *  * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.opensingular.form.sample;

import org.opensingular.form.SIComposite;
import org.opensingular.form.SInfoType;
import org.opensingular.form.SInstance;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.STypeList;
import org.opensingular.form.TypeBuilder;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.view.list.SViewListByMasterDetail;

import java.util.Optional;
import java.util.UUID;

@SInfoType(spackage = FormTestPackage.class,  name = "STypeFirstListElement")
public class STypeFirstListElement extends STypeComposite<SIComposite> {

    public STypeString                                         identificador;
    public STypeString                                         nome;
    public STypeString                                         inscricao;
    //    public STypeDate                                       validade;
    public STypeString                                         tipoEmbarcacao;
    public STypeList<STypeTroublesomeListElement, SIComposite> theNestedTroublesomeList;
    public STypeString                                         naturezaTransporte;

    @Override
    protected void onLoadType(TypeBuilder tb) {

        identificador = this.addFieldString("identificador");
        identificador
                .asAtr()
                .visible(false);
        identificador.withInitListener(si -> si.setValue(UUID.randomUUID().toString()));

        nome = this.addFieldString("nome");
        nome.asAtr()
                .required(STypeFormTest.OBRIGATORIO)
                .label("Nome da Embarcação")
                .asAtrBootstrap()
                .colPreference(3);

        inscricao = this.addFieldString("inscricao");
        inscricao
                .asAtr()
                .required(STypeFormTest.OBRIGATORIO)
                .label("Número de Inscrição").asAtrBootstrap()
                .colPreference(3);

//        validade = this.addFieldDate("validade");
//        validade
//                .asAtr()
//                .label("Validade (se houver)")
//                .asAtrBootstrap()
//                .colPreference(3);
//        validade.addInstanceValidator(this::isDataValidadeVencida);

        tipoEmbarcacao = this.addFieldString("tipoEmbarcacao");
        tipoEmbarcacao
                .asAtr()
                .required(STypeFormTest.OBRIGATORIO)
                .label("Embarcação")
                .asAtrBootstrap()
                .colPreference(3);

        tipoEmbarcacao.selectionOfEnum(ComboDomainEnum.class);


        naturezaTransporte = this.addFieldString("naturezaTransporte");
        naturezaTransporte.selectionOf("Misto", "Passageiros");
        naturezaTransporte.asAtr().label("Natureza do transporte");
        naturezaTransporte.asAtrBootstrap().colPreference(3);


        theNestedTroublesomeList = this.addFieldListOf("theNestedTroublesomeList", STypeTroublesomeListElement.class);
        theNestedTroublesomeList.asAtr().label("Habilitação técnica da embarcação");
        theNestedTroublesomeList.withInitListener(list -> list.addNew());
        theNestedTroublesomeList.withMinimumSizeOf(1);
        theNestedTroublesomeList.withMaximumSizeOf(1);
        theNestedTroublesomeList.withView(() -> new SViewListByMasterDetail()
                .fullSize()
                .disableEdit()
                .disableDelete()
                .disableAdd()
                .col("Habilitação Técnica", "Editar anexos de habilitação técnica"));

        theNestedTroublesomeList.getElementsType().cascoNuComp
                .asAtr()
                .exists(this::isCascoNu);
        theNestedTroublesomeList.getElementsType().contrucaoReformaComp
                .asAtr()
                .exists(this::isConstrucao);

        this.asAtr()
                .displayString("Embarcação ${nome!}")
                .dependsOn(nome);

    }

//    private void isDataValidadeVencida(InstanceValidatable<SIDate> validatable) {
//        Date dataValidade = validatable.getInstance().getValue();
//        if (dataValidade != null && dataValidade.before(new Date())){
//            validatable.error("A data de validade não pode estar vencida");
//        }
//    }

    private boolean isCascoNu(SInstance habilitacaoTecnica) {
        return isTipoEmbarcacao(habilitacaoTecnica, ComboDomainEnum.AFREATAMENTO_CASCO_NU);
    }

    private boolean isConstrucao(SInstance habilitacaoTecnica) {
        return isTipoEmbarcacao(habilitacaoTecnica, ComboDomainEnum.EM_CONSTRUCAO);
    }

    private boolean isTipoEmbarcacao(SInstance habilitacaoTecnica, ComboDomainEnum tipo) {
        Optional<String> value = habilitacaoTecnica.findNearestValue(tipoEmbarcacao);
        if (value.isPresent()) {
            return tipo.equals(ComboDomainEnum.valueOf(value.get()));
        }
        return false;
    }

}
