package br.net.mirante.singular.showcase.view.page.form.examples.canabidiol;

import br.net.mirante.singular.form.mform.MIComposto;
import br.net.mirante.singular.form.mform.MInfoTipo;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.TipoBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.basic.ui.AtrBootstrap;
import br.net.mirante.singular.form.mform.core.AtrCore;
import br.net.mirante.singular.form.mform.util.comuns.MTipoEMail;
import br.net.mirante.singular.form.mform.util.comuns.MTipoTelefoneNacional;

@MInfoTipo(nome = "MTipoContato", pacote = MPacotePeticaoCanabidiol.class)
public class MTipoContato extends MTipoComposto<MIComposto> {


    public MTipoEMail email;
    public MTipoTelefoneNacional telefoneFixo;
    public MTipoTelefoneNacional celular;

    @Override
    protected void onCargaTipo(TipoBuilder tb) {
        super.onCargaTipo(tb);

        this.as(AtrBasic::new)
                .label("Contato");

        telefoneFixo = this.addCampo("telefonefixo", MTipoTelefoneNacional.class);
        telefoneFixo
                .as(AtrBasic::new)
                .label("Telefone Fixo")
                .as(AtrBootstrap::new)
                .colPreference(2);

        celular = this.addCampo("celular", MTipoTelefoneNacional.class);
        celular
                .as(AtrBasic::new)
                .label("Celular")
                .as(AtrBootstrap::new)
                .colPreference(2);

        email = this.addCampoEmail("email");
        email
                .as(AtrCore::new)
                .obrigatorio()
                .as(AtrBasic::new)
                .label("E-mail")
                .as(AtrBootstrap::new)
                .colPreference(4);

    }

}
