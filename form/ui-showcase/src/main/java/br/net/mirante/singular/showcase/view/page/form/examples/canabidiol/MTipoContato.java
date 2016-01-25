package br.net.mirante.singular.showcase.view.page.form.examples.canabidiol;

import br.net.mirante.singular.form.mform.MIComposto;
import br.net.mirante.singular.form.mform.MILista;
import br.net.mirante.singular.form.mform.MInfoTipo;
import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.TipoBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.options.MSelectionableInstance;
import br.net.mirante.singular.form.mform.util.comuns.MTipoTelefoneNacional;
import br.net.mirante.singular.form.wicket.AtrBootstrap;
import br.net.mirante.singular.showcase.view.page.form.examples.SelectBuilder;

@MInfoTipo(nome = "MTipoContato", pacote = MPacotePeticaoCanabidiol.class)
public class MTipoContato extends MTipoComposto<MIComposto> {



    @Override
    protected void onCargaTipo(TipoBuilder tb) {
        super.onCargaTipo(tb);

        this.as(AtrBasic::new)
                .label("Contato");

        this.addCampo("telefonefixo", MTipoTelefoneNacional.class)
                .as(AtrBasic::new)
                .label("Telefone Fixo")
                .as(AtrBootstrap::new)
                .colPreference(2);

        this.addCampo("celular", MTipoTelefoneNacional.class)
                .as(AtrBasic::new)
                .label("Celular")
                .as(AtrBootstrap::new)
                .colPreference(2);

        this.addCampoEmail("email")
                .as(AtrBasic::new)
                .label("E-mail")
                .as(AtrBootstrap::new)
                .colPreference(4);

    }
}
