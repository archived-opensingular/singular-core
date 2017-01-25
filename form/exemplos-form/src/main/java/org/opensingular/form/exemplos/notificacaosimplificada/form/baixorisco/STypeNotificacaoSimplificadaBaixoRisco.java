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

package org.opensingular.form.exemplos.notificacaosimplificada.form.baixorisco;

import org.apache.commons.lang3.tuple.Triple;
import org.opensingular.form.*;
import org.opensingular.form.exemplos.notificacaosimplificada.common.STypeSubstanciaPopulator;
import org.opensingular.form.exemplos.notificacaosimplificada.form.STypeAcondicionamento;
import org.opensingular.form.exemplos.notificacaosimplificada.form.vocabulario.STypeFormaFarmaceutica;
import org.opensingular.form.exemplos.notificacaosimplificada.form.vocabulario.STypeLinhaProducao;
import org.opensingular.form.exemplos.notificacaosimplificada.service.DominioService;
import org.opensingular.form.exemplos.util.TripleConverter;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.util.transformer.Value;
import org.opensingular.form.view.SViewListByMasterDetail;

@SInfoType(name = "STypeNotificacaoSimplificadaBaixoRisco", spackage = SPackageNotificacaoSimplificadaBaixoRisco.class)
public class STypeNotificacaoSimplificadaBaixoRisco extends STypeComposite<SIComposite> {


    static DominioService dominioService(SInstance ins) {
        return ins.getDocument().lookupService(DominioService.class);
    }


    @Override
    protected void onLoadType(TypeBuilder tb) {

        {
            asAtr().displayString("${nomeComercial} - ${configuracaoLinhaProducao.descricao} (<#list substancias as c>${c.substancia.descricao} ${c.concentracao.descricao}<#sep>, </#sep></#list>) ");
            asAtr().label("Medicamento de Baixo Risco");
        }

        final STypeLinhaProducao linhaProducao = addField("linhaProducao", STypeLinhaProducao.class);

        final STypeComposite<SIComposite> configuracaoLinhaProducao     = addFieldComposite("configuracaoLinhaProducao");
        final STypeSimple                 idConfiguracaoLinhaProducao   = configuracaoLinhaProducao.addFieldInteger("id");
        final STypeSimple                 idLinhaProducaoConfiguracao   = configuracaoLinhaProducao.addFieldInteger("idLinhaProducao");
        final STypeSimple                 descConfiguracaoLinhaProducao = configuracaoLinhaProducao.addFieldString("descricao");

        {
            configuracaoLinhaProducao
                    .asAtr()
                    .label("Descrição").required().dependsOn(linhaProducao).exists(i -> Value.notNull(i, linhaProducao.id))
                    .asAtrBootstrap()
                    .colPreference(8);
            configuracaoLinhaProducao
                    .autocompleteOf(Triple.class)
                    .id("${left}")
                    .display("${right}")
                    .converter(new TripleConverter(idConfiguracaoLinhaProducao, idLinhaProducaoConfiguracao, descConfiguracaoLinhaProducao))
                    .simpleProvider(ins -> dominioService(ins).configuracoesLinhaProducao(Value.of(ins, linhaProducao.id)));
        }

        new STypeSubstanciaPopulator(this, configuracaoLinhaProducao, idConfiguracaoLinhaProducao,
                ins -> dominioService(ins).findSubstanciasByIdConfiguracaoLinhaProducao(
                        (Integer) Value.of(ins, idConfiguracaoLinhaProducao))).populate();

        final STypeString nomeComercial = addFieldString("nomeComercial");
        {
            nomeComercial
                    .asAtr()
                    .required().label("Nome do medicamento")
                    .asAtrBootstrap()
                    .colPreference(8);
        }

        addField("formaFarmaceutica", STypeFormaFarmaceutica.class);


        final STypeList<STypeAcondicionamento, SIComposite> acondicionamentos = addFieldListOf("acondicionamentos", STypeAcondicionamento.class);
        {
            acondicionamentos.withMiniumSizeOf(1);
            acondicionamentos
                    .withView(new SViewListByMasterDetail()
                            .col(acondicionamentos.getElementsType().embalagemPrimaria, "Embalagem primária")
                            .col(acondicionamentos.getElementsType().embalagemSecundaria.descricao, "Embalagem secundária")
                            .col(acondicionamentos.getElementsType().quantidade)
                            .col(acondicionamentos.getElementsType().unidadeMedida.sigla, "Unidade de medida")
                            .col(acondicionamentos.getElementsType().estudosEstabilidade, "Estudo de estabilidade")
                            .col(acondicionamentos.getElementsType().prazoValidade))
                    .asAtr().label("Acondicionamento");
        }


    }
}
