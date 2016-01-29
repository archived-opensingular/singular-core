package br.net.mirante.singular.showcase.view.page.form.examples.canabidiol;

import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.MInfoTipo;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.TipoBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.basic.ui.AtrBootstrap;
import br.net.mirante.singular.form.mform.basic.view.MSelecaoPorRadioView;
import br.net.mirante.singular.form.mform.core.AtrCore;
import br.net.mirante.singular.form.mform.core.STypeDataHora;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.mform.util.transformer.Value;

@MInfoTipo(nome = "MTipoImportacao", pacote = SPackagePeticaoCanabidiol.class)
public class STypeImportacao extends STypeComposite<SIComposite> {


    @Override
    protected void onLoadType(TipoBuilder tb) {
        super.onLoadType(tb);

        STypeString modalidade = this.addCampoString("modalidadeImportacao");

        modalidade
                .as(AtrCore::new)
                .obrigatorio()
                .as(AtrBasic::new)
                .label("Modalidade de Importação")
                .as(AtrBootstrap::new)
                .colPreference(12);

        modalidade
                .withSelection()
                        .add("58911761", "Aquisição Intermediada (entidade hospitalar; unidade governamental ligada à área de saúde; operadora de plano de saúde ou entidade civil representativa de pacientes, legalmente constituída)")
                        .add("58911758", "Bagagem acompanhada")
                        .add("58911759", "Formal - por meio de Licenciamento de Importação (LI) no Sistema Integrado de Comércio Exterior - SISCOMEX IMPORTAÇÃO")
                        .add("58911760", "Remessa Expressa");


        aquisicaoIntermediada(modalidade, "58911761");
        bagagemAcompanhada(modalidade, "58911758");
        licenciamentoImportacao(modalidade, "58911759");
        remessaExpressa(modalidade, "58911760");


    }

    private void aquisicaoIntermediada(STypeString modalidade, String aquisicaoIntermediada) {

        STypeString naturezaIntermediador = this.addCampoString("naturezaIntermediador");

        naturezaIntermediador
                .as(AtrCore::new)
                .obrigatorio()
                .as(AtrBasic::new)
                .label("Natureza do intermediador")
                // Isso é um bug não sei como descrever
                .visivel(instancia -> aquisicaoIntermediada.equals(Value.of(instancia, modalidade)))
                .dependsOn(modalidade);


        naturezaIntermediador
                .withSelection()
                        .add("57862460", "Entidade civil representativa de pacientes legalmente constituída")
                        .add("57862461", "Entidade hospitalar")
                        .add("57862462", "Operadora de plano de saúde")
                        .add("57862463", "Unidade governamental ligada à área de saúde");

        naturezaIntermediador
                .withView(new MSelecaoPorRadioView().layoutVertical());


        this.addCampoString("razaoSocialIntermediador")
                .as(AtrCore::new)
                .obrigatorio()
                .as(AtrBasic::new)
                .label("Razão Social")
                // Isso é um bug não sei como descrever
                .visivel(instancia -> aquisicaoIntermediada.equals(Value.of(instancia, modalidade)))
                .dependsOn(modalidade)
                .as(AtrBootstrap::new)
                .colPreference(4);


        this.addCampoCNPJ("cnpjIntermediador")
                .as(AtrCore::new)
                .obrigatorio()
                .as(AtrBasic::new)
                .label("CNPJ")
                // Isso é um bug não sei como descrever
                .visivel(instancia -> aquisicaoIntermediada.equals(Value.of(instancia, modalidade)))
                .dependsOn(modalidade)
                .as(AtrBootstrap::new)
                .colPreference(3);


        this.addCampo("enderecoIntermediador", STypeEndereco.class)
                .as(AtrBasic::new)
                .label("Endereço do Intermediador")
                // Isso é um bug não sei como descrever
                .visivel(instancia -> aquisicaoIntermediada.equals(Value.of(instancia, modalidade)))
                .dependsOn(modalidade);


        STypeContato tipoContato = this.addCampo("contato", STypeContato.class);
        tipoContato
                .as(AtrBasic::new)
                .label("Contato do Intermediador")
                // Isso é um bug não sei como descrever
                .visivel(instancia -> aquisicaoIntermediada.equals(Value.of(instancia, modalidade)))
                .dependsOn(modalidade);
        tipoContato
                .telefoneFixo
                .as(AtrCore::new)
                .obrigatorio();
    }

    private void bagagemAcompanhada(STypeString modalidade, String bagagemAcompanhada) {

        this.addCampoString("nomePassageiro")
                .as(AtrCore::new)
                .obrigatorio(instancia -> bagagemAcompanhada.equals(Value.of(instancia, modalidade)))
                .as(AtrBasic::new)
                .label("Nome do Passageiro")
                // Isso é um bug não sei como descrever
                .visivel(instancia -> bagagemAcompanhada.equals(Value.of(instancia, modalidade)))
                .dependsOn(modalidade)
                .as(AtrBootstrap::new)
                .colPreference(4);


        this.addCampoString("passaporte")
                .as(AtrCore::new)
                .obrigatorio(instancia -> bagagemAcompanhada.equals(Value.of(instancia, modalidade)))
                .as(AtrBasic::new)
                .label("Número do passaporte")
                // Isso é um bug não sei como descrever
                .visivel(instancia -> bagagemAcompanhada.equals(Value.of(instancia, modalidade)))
                .dependsOn(modalidade)
                .as(AtrBootstrap::new)
                .colPreference(4);

        this.addCampoString("companhiaAerea")
                .as(AtrBasic::new)
                .label("Nome da empresa aérea")
                // Isso é um bug não sei como descrever
                .visivel(instancia -> bagagemAcompanhada.equals(Value.of(instancia, modalidade)))
                .dependsOn(modalidade)
                .as(AtrBootstrap::new)
                .colPreference(4);

        this.addCampoString("numeroVoo")
                .as(AtrBasic::new)
                .label("Número do vôo")
                // Isso é um bug não sei como descrever
                .visivel(instancia -> bagagemAcompanhada.equals(Value.of(instancia, modalidade)))
                .dependsOn(modalidade)
                .as(AtrBootstrap::new)
                .colPreference(4);


        this.addCampoString("aeroportoChegada")
                .as(AtrBasic::new)
                .label("Nome do aeroporto de chegada ao Brasil")
                // Isso é um bug não sei como descrever
                .visivel(instancia -> bagagemAcompanhada.equals(Value.of(instancia, modalidade)))
                .dependsOn(modalidade)
                .as(AtrBootstrap::new)
                .colPreference(4);


        this.addCampo("dataChegada", STypeDataHora.class)
                .as(AtrBasic::new)
                .label("Data e Hora da chegada ao Brasil")
                // Isso é um bug não sei como descrever
                .visivel(instancia -> bagagemAcompanhada.equals(Value.of(instancia, modalidade)))
                .dependsOn(modalidade)
                .as(AtrBootstrap::new)
                .colPreference(6);
    }


    private void licenciamentoImportacao(STypeString modalidade, String licenciamentoImportacao) {
        this.addCampoString("numeroLI")
                .as(AtrBasic::new)
                .label("Número do LI")
                // Isso é um bug não sei como descrever
                .visivel(instancia -> licenciamentoImportacao.equals(Value.of(instancia, modalidade)))
                .dependsOn(modalidade)
                .as(AtrBootstrap::new)
                .colPreference(4);

    }

    private void remessaExpressa(STypeString modalidade, String remessaExpressa) {

        this.addCampoString("rastreadorCourier")
                .as(AtrBasic::new)
                .label("Número de objeto da empresa de Courrier:\n")
                // Isso é um bug não sei como descrever
                .visivel(instancia -> remessaExpressa.equals(Value.of(instancia, modalidade)))
                .dependsOn(modalidade)
                .as(AtrBootstrap::new)
                .colPreference(4);

        this.addCampoString("awb")
                .as(AtrBasic::new)
                .label("Número do AWB (Air Way Bill)")
                // Isso é um bug não sei como descrever
                .visivel(instancia -> remessaExpressa.equals(Value.of(instancia, modalidade)))
                .dependsOn(modalidade)
                .as(AtrBootstrap::new)
                .colPreference(4);
    }


}
