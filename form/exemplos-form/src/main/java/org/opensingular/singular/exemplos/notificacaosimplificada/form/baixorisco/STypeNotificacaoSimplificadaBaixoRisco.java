package org.opensingular.singular.exemplos.notificacaosimplificada.form.baixorisco;

import org.apache.commons.lang3.tuple.Triple;

import org.opensingular.singular.exemplos.notificacaosimplificada.common.STypeSubstanciaPopulator;
import org.opensingular.singular.exemplos.notificacaosimplificada.form.STypeAcondicionamento;
import org.opensingular.singular.exemplos.notificacaosimplificada.form.vocabulario.STypeFormaFarmaceutica;
import org.opensingular.singular.exemplos.notificacaosimplificada.form.vocabulario.STypeLinhaProducao;
import org.opensingular.singular.exemplos.notificacaosimplificada.service.DominioService;
import org.opensingular.singular.exemplos.util.TripleConverter;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SInfoType;
import org.opensingular.form.SInstance;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.STypeList;
import org.opensingular.form.STypeSimple;
import org.opensingular.form.TypeBuilder;
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
        super.onLoadType(tb);

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

        final STypeList<STypeComposite<SIComposite>, SIComposite> substancia = new STypeSubstanciaPopulator(this, configuracaoLinhaProducao, idConfiguracaoLinhaProducao,
                ins -> dominioService(ins).findSubstanciasByIdConfiguracaoLinhaProducao((Integer) Value.of(ins, idConfiguracaoLinhaProducao))
        ).populate();

        final STypeString nomeComercial = addFieldString("nomeComercial");
        {
            nomeComercial
                    .asAtr()
                    .required().label("Nome do medicamento")
                    .asAtrBootstrap()
                    .colPreference(8);
        }

        final STypeFormaFarmaceutica formaFarmaceutica = addField("formaFarmaceutica", STypeFormaFarmaceutica.class);


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
