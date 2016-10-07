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

package org.opensingular.form.exemplos.notificacaosimplificada.form;

import org.opensingular.form.exemplos.notificacaosimplificada.domain.EtapaFabricacao;
import org.opensingular.form.exemplos.notificacaosimplificada.domain.corporativo.PessoaJuridicaNS;
import org.opensingular.form.exemplos.notificacaosimplificada.service.DominioService;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SInfoType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.STypeList;
import org.opensingular.form.TypeBuilder;
import org.opensingular.form.provider.STextQueryProvider;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.view.SViewAutoComplete;
import org.opensingular.form.view.SViewListByTable;

import static org.opensingular.form.exemplos.notificacaosimplificada.form.STypeLocalFabricacao.dominioService;

@SInfoType(spackage = SPackageNotificacaoSimplificada.class)
public class STypeEmpresaTerceirizada extends STypeComposite<SIComposite> {

    @Override
    protected void onLoadType(TypeBuilder tb) {

        STypeComposite<SIComposite> empresa     = addFieldComposite("empresa");
        STypeString                 idEmpresa   = empresa.addFieldString("id");
        STypeString                 razaoSocial = empresa.addFieldString("razaoSocial");
        razaoSocial.asAtr().label("Razão Social");
        STypeString endereco = empresa.addFieldString("endereco");
        empresa
                .asAtr().label("Empresa")
                .displayString("${razaoSocial} - ${endereco}")

                .getTipo().withView(SViewAutoComplete::new);

        empresa.autocompleteOf(PessoaJuridicaNS.class)
                .id(PessoaJuridicaNS::getCod)
                .display(PessoaJuridicaNS::getRazaoSocial)
                .converter(new STypeLocalFabricacao.PessoaJuridicaConverter(idEmpresa, razaoSocial, endereco))
                .filteredProvider((i, f) -> dominioService(i).empresaTerceirizada(f));

        STypeList<STypeComposite<SIComposite>, SIComposite> etapasFabricacao         = addFieldListOfComposite("etapasFabricacao", "etapaFabricacaoWrapper");
        STypeComposite<SIComposite>                         etapaFabricacaoWrapper   = etapasFabricacao.getElementsType();
        STypeComposite<SIComposite>                         etapaFabricacao          = etapaFabricacaoWrapper.addFieldComposite("etapaFabricacao");
        STypeString                                         idEtapaFabricacao        = etapaFabricacao.addFieldString("id");
        STypeString                                         descricaoEtapaFabricacao = etapaFabricacao.addFieldString("descricao");

        etapaFabricacao
                .setView(SViewAutoComplete::new);

        etapaFabricacao.autocomplete()
                .id(idEtapaFabricacao)
                .display(descricaoEtapaFabricacao)
                .filteredProvider((STextQueryProvider) (builder, query) -> {
                    builder
                            .getCurrentInstance()
                            .getDocument()
                            .lookupService(DominioService.class)
                            .buscarVocabulario(EtapaFabricacao.class, query)
                            .forEach(vc -> builder.add().set(idEtapaFabricacao, vc.getId()).set(descricaoEtapaFabricacao, vc.getDescricao()));
                });

        etapasFabricacao
                .withView(SViewListByTable::new);
        etapasFabricacao
                .asAtr()
                .label("Etapa de fabricação")
                .displayString("<#list _inst as c>${c.etapaFabricacao.descricao}<#sep>, </#sep></#list>");
    }

    public STypeList<STypeComposite<SIComposite>, SIComposite> etapasFabricacao() {
        return (STypeList<STypeComposite<SIComposite>, SIComposite>) getField("etapasFabricacao");
    }


}
