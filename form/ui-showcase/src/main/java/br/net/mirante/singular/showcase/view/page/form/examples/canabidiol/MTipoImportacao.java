package br.net.mirante.singular.showcase.view.page.form.examples.canabidiol;

import br.net.mirante.singular.form.mform.MIComposto;
import br.net.mirante.singular.form.mform.MInfoTipo;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.TipoBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.basic.view.MBooleanRadioView;
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
                .as(AtrBootstrap::new).colPreference(12);

        modalidade
                .withSelectionOf(
                        modalidade.create("58911761", "Aquisição Intermediada (entidade hospitalar; unidade governamental ligada à área de saúde; operadora de plano de saúde ou entidade civil representativa de pacientes, legalmente constituída)"),
                        modalidade.create("58911758", "Bagagem acompanhada"),
                        modalidade.create("58911759", "Formal - por meio de Licenciamento de Importação (LI) no Sistema Integrado de Comércio Exterior - SISCOMEX IMPORTAÇÃO"),
                        modalidade.create("58911760", "Remessa Expressa"));

        MTipoString naturezaIntermediador = this.addCampoString("naturezaIntermediador");

        naturezaIntermediador
                .as(AtrBasic::new)
                .label("Natureza do intermediador")
                .visivel(false)// Isso é um bug não sei como descrever
                .visivel(instancia -> "58911761".equals(getValue(instancia, modalidade)))
                .dependsOn(modalidade);


        naturezaIntermediador
                .withSelectionOf(
                        naturezaIntermediador.create("57862460", "Entidade civil representativa de pacientes legalmente constituída"),
                        naturezaIntermediador.create("57862461", "Entidade hospitalar"),
                        naturezaIntermediador.create("57862462", "Operadora de plano de saúde"),
                        naturezaIntermediador.create("57862463", "Unidade governamental ligada à área de saúde"));

        naturezaIntermediador
                .withView(new MSelecaoPorRadioView().layoutVertical());




    }


}
