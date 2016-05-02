/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.exemplos.notificacaosimplificada.form.baixorisco;

import br.net.mirante.singular.exemplos.notificacaosimplificada.common.STypeSubstanciaPopulator;
import br.net.mirante.singular.exemplos.notificacaosimplificada.form.SPackageNotificacaoSimplificada;
import br.net.mirante.singular.exemplos.notificacaosimplificada.form.STypeAcondicionamento;
import br.net.mirante.singular.exemplos.notificacaosimplificada.form.vocabulario.STypeFormaFarmaceutica;
import br.net.mirante.singular.exemplos.notificacaosimplificada.form.vocabulario.STypeLinhaProducao;
import br.net.mirante.singular.exemplos.notificacaosimplificada.service.DominioService;
import br.net.mirante.singular.exemplos.util.TripleConverter;
import br.net.mirante.singular.form.mform.*;
import br.net.mirante.singular.form.mform.basic.view.SViewListByMasterDetail;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.mform.util.transformer.Value;
import org.apache.commons.lang3.tuple.Triple;

public class SPackageNotificacaoSimplificadaBaixoRisco extends SPackage {

    public static final String PACOTE        = "mform.peticao.notificacaosimplificada.baixorisco";
    public static final String TIPO          = "MedicamentoBaixoRisco";
    public static final String NOME_COMPLETO = PACOTE + "." + TIPO;

    public static DominioService dominioService(SInstance ins) {
        return ins.getDocument().lookupService(DominioService.class);
    }

    public SPackageNotificacaoSimplificadaBaixoRisco() {
        super(PACOTE);
    }


    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {
        pb.getDictionary().loadPackage(SPackageNotificacaoSimplificada.class);

        final STypeComposite<?> baixoRisco = pb.createCompositeType(TIPO);
        {
            baixoRisco.asAtr().displayString("${nomeComercial} - ${configuracaoLinhaProducao.descricao} (<#list substancias as c>${c.substancia.descricao} ${c.concentracao.descricao}<#sep>, </#sep></#list>) ");
            baixoRisco.asAtr().label("Notificação Simplificada - Medicamento de Baixo Risco");
        }

        final STypeLinhaProducao linhaProducao = baixoRisco.addField("linhaProducao", STypeLinhaProducao.class);

        final STypeComposite<SIComposite> configuracaoLinhaProducao     = baixoRisco.addFieldComposite("configuracaoLinhaProducao");
        final STypeSimple       idConfiguracaoLinhaProducao   = configuracaoLinhaProducao.addFieldInteger("id");
        final STypeSimple       idLinhaProducaoConfiguracao   = configuracaoLinhaProducao.addFieldInteger("idLinhaProducao");
        final STypeSimple       descConfiguracaoLinhaProducao = configuracaoLinhaProducao.addFieldString("descricao");

        {
            configuracaoLinhaProducao
                    .asAtr()
                    .label("Descrição").required().dependsOn(linhaProducao).visible(i -> Value.notNull(i, linhaProducao.id))
                    .asAtrBootstrap()
                    .colPreference(8);
            configuracaoLinhaProducao
                    .autocompleteOf(Triple.class)
                    .id("${left}")
                    .display("${right}")
                    .converter(new TripleConverter(idConfiguracaoLinhaProducao, idLinhaProducaoConfiguracao, descConfiguracaoLinhaProducao))
                    .simpleProvider(ins -> dominioService(ins).configuracoesLinhaProducao(Value.of(ins, linhaProducao.id)));
        }

        final STypeList<STypeComposite<SIComposite>, SIComposite> substancia = new STypeSubstanciaPopulator(baixoRisco, configuracaoLinhaProducao, idConfiguracaoLinhaProducao,
                ins -> dominioService(ins).findSubstanciasByIdConfiguracaoLinhaProducao((Integer) Value.of(ins, idConfiguracaoLinhaProducao))
        ).populate();

        final STypeString nomeComercial = baixoRisco.addFieldString("nomeComercial");
        {
            nomeComercial
                    .asAtr()
                    .required().label("Nome do medicamento")
                    .asAtrBootstrap()
                    .colPreference(8);
        }

        final STypeFormaFarmaceutica formaFarmaceutica = baixoRisco.addField("formaFarmaceutica", STypeFormaFarmaceutica.class);


        final STypeList<STypeAcondicionamento, SIComposite> acondicionamentos = baixoRisco.addFieldListOf("acondicionamentos", STypeAcondicionamento.class);
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

