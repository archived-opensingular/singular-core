package br.net.mirante.singular.showcase.view.page.form.examples.canabidiol;

import br.net.mirante.singular.form.mform.MIComposto;
import br.net.mirante.singular.form.mform.MInfoTipo;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.TipoBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.basic.view.MSelecaoPorRadioView;
import br.net.mirante.singular.form.mform.core.MTipoString;
import br.net.mirante.singular.form.wicket.AtrBootstrap;

@MInfoTipo(nome = "MTipoImportacao", pacote = MPacotePeticaoCanabidiol.class)
public class MTipoImportacao extends MTipoComposto<MIComposto> implements CanabidiolUtil {


    @Override
    protected void onCargaTipo(TipoBuilder tb) {
        super.onCargaTipo(tb);

        MTipoString modalidade = this.addCampoString("modalidadeImportacao");

        modalidade
                .as(AtrBasic::new)
                .label("Modalidade de Importação")
                .as(AtrBootstrap::new)
                .colPreference(12);

        modalidade
                .withSelectionOf(
                        modalidade.create("58911761", "Aquisição Intermediada (entidade hospitalar; unidade governamental ligada à área de saúde; operadora de plano de saúde ou entidade civil representativa de pacientes, legalmente constituída)"),
                        modalidade.create("58911758", "Bagagem acompanhada"),
                        modalidade.create("58911759", "Formal - por meio de Licenciamento de Importação (LI) no Sistema Integrado de Comércio Exterior - SISCOMEX IMPORTAÇÃO"),
                        modalidade.create("58911760", "Remessa Expressa"));


        aquisicaoIntermediada(modalidade, "58911761");
        bagagemAcompanhada(modalidade, "58911758");
        licenciamentoImportacao(modalidade, "58911759");
        remessaExpressa(modalidade, "58911760");


    }

    private void aquisicaoIntermediada(MTipoString modalidade, String aquisicaoIntermediada) {

        MTipoString naturezaIntermediador = this.addCampoString("naturezaIntermediador");

        naturezaIntermediador
                .as(AtrBasic::new)
                .label("Natureza do intermediador")
                .visivel(false)// Isso é um bug não sei como descrever
                .visivel(instancia -> aquisicaoIntermediada.equals(getValue(instancia, modalidade)))
                .dependsOn(modalidade);


        naturezaIntermediador
                .withSelectionOf(
                        naturezaIntermediador.create("57862460", "Entidade civil representativa de pacientes legalmente constituída"),
                        naturezaIntermediador.create("57862461", "Entidade hospitalar"),
                        naturezaIntermediador.create("57862462", "Operadora de plano de saúde"),
                        naturezaIntermediador.create("57862463", "Unidade governamental ligada à área de saúde"));

        naturezaIntermediador
                .withView(new MSelecaoPorRadioView().layoutVertical());


        this.addCampoString("razaoSocialIntermediador")
                .as(AtrBasic::new)
                .label("Razão Social")
                .visivel(false)// Isso é um bug não sei como descrever
                .visivel(instancia -> aquisicaoIntermediada.equals(getValue(instancia, modalidade)))
                .dependsOn(modalidade)
                .as(AtrBootstrap::new)
                .colPreference(4);


        this.addCampoCNPJ("cnpjIntermediador")
                .as(AtrBasic::new)
                .label("CNPJ")
                .visivel(false)// Isso é um bug não sei como descrever
                .visivel(instancia -> aquisicaoIntermediada.equals(getValue(instancia, modalidade)))
                .dependsOn(modalidade)
                .as(AtrBootstrap::new)
                .colPreference(3);


        this.addCampo("enderecoIntermediador", MTipoEndereco.class)
                .as(AtrBasic::new)
                .label("Endereço do Intermediador")
                .visivel(false)// Isso é um bug não sei como descrever
                .visivel(instancia -> aquisicaoIntermediada.equals(getValue(instancia, modalidade)))
                .dependsOn(modalidade);

        this.addCampo("contato", MTipoContato.class)
                .as(AtrBasic::new)
                .label("Contato do Intermediador")
                .visivel(false)// Isso é um bug não sei como descrever
                .visivel(instancia -> aquisicaoIntermediada.equals(getValue(instancia, modalidade)))
                .dependsOn(modalidade);
    }

    private void bagagemAcompanhada(MTipoString modalidade, String bagagemAcompanhada) {

        this.addCampoString("nomePassageiro")
                .as(AtrBasic::new)
                .label("Nome do Passageiro")
                .visivel(false)// Isso é um bug não sei como descrever
                .visivel(instancia -> bagagemAcompanhada.equals(getValue(instancia, modalidade)))
                .dependsOn(modalidade)
                .as(AtrBootstrap::new)
                .colPreference(4);


        this.addCampoString("passaporte")
                .as(AtrBasic::new)
                .label("Número do passaporte")
                .visivel(false)// Isso é um bug não sei como descrever
                .visivel(instancia -> bagagemAcompanhada.equals(getValue(instancia, modalidade)))
                .dependsOn(modalidade)
                .as(AtrBootstrap::new)
                .colPreference(4);

        this.addCampoString("companhiaAerea")
                .as(AtrBasic::new)
                .label("Nome da empresa aérea")
                .visivel(false)// Isso é um bug não sei como descrever
                .visivel(instancia -> bagagemAcompanhada.equals(getValue(instancia, modalidade)))
                .dependsOn(modalidade)
                .as(AtrBootstrap::new)
                .colPreference(4);

        this.addCampoString("numeroVoo")
                .as(AtrBasic::new)
                .label("Número do vôo")
                .visivel(false)// Isso é um bug não sei como descrever
                .visivel(instancia -> bagagemAcompanhada.equals(getValue(instancia, modalidade)))
                .dependsOn(modalidade)
                .as(AtrBootstrap::new)
                .colPreference(4);


        this.addCampoString("aeroportoChegada")
                .as(AtrBasic::new)
                .label("Nome do aeroporto de chegada ao Brasil")
                .visivel(false)// Isso é um bug não sei como descrever
                .visivel(instancia -> bagagemAcompanhada.equals(getValue(instancia, modalidade)))
                .dependsOn(modalidade)
                .as(AtrBootstrap::new)
                .colPreference(4);


        this.addCampoData("dataChegada")
                .as(AtrBasic::new)
                .label("Data e Hora da chegada ao Brasil")
                .visivel(false)// Isso é um bug não sei como descrever
                .visivel(instancia -> bagagemAcompanhada.equals(getValue(instancia, modalidade)))
                .dependsOn(modalidade)
                .as(AtrBootstrap::new)
                .colPreference(6);
    }


    private void licenciamentoImportacao(MTipoString modalidade, String licenciamentoImportacao) {
        this.addCampoString("numeroLI")
                .as(AtrBasic::new)
                .label("Número do LI")
                .visivel(false)// Isso é um bug não sei como descrever
                .visivel(instancia -> licenciamentoImportacao.equals(getValue(instancia, modalidade)))
                .dependsOn(modalidade)
                .as(AtrBootstrap::new)
                .colPreference(4);

    }

    private void remessaExpressa(MTipoString modalidade, String remessaExpressa) {

        this.addCampoString("rastreadorCourier")
                .as(AtrBasic::new)
                .label("Número de objeto da empresa de Courrier:\n")
                .visivel(false)// Isso é um bug não sei como descrever
                .visivel(instancia -> remessaExpressa.equals(getValue(instancia, modalidade)))
                .dependsOn(modalidade)
                .as(AtrBootstrap::new)
                .colPreference(4);

        this.addCampoString("awb")
                .as(AtrBasic::new)
                .label("Número do AWB (Air Way Bill)")
                .visivel(false)// Isso é um bug não sei como descrever
                .visivel(instancia -> remessaExpressa.equals(getValue(instancia, modalidade)))
                .dependsOn(modalidade)
                .as(AtrBootstrap::new)
                .colPreference(4);
    }


}
