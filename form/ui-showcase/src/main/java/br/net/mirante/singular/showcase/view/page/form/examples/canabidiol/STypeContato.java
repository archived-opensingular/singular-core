package br.net.mirante.singular.showcase.view.page.form.examples.canabidiol;

import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.MInfoTipo;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.TipoBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.basic.ui.AtrBootstrap;
import br.net.mirante.singular.form.mform.core.AtrCore;
import br.net.mirante.singular.form.mform.util.comuns.STypeEMail;
import br.net.mirante.singular.form.mform.util.comuns.STypeTelefoneNacional;

@MInfoTipo(nome = "MTipoContato", pacote = SPackagePeticaoCanabidiol.class)
public class STypeContato extends STypeComposite<SIComposite> {


    public STypeEMail email;
    public STypeTelefoneNacional telefoneFixo;
    public STypeTelefoneNacional celular;

    @Override
    protected void onLoadType(TipoBuilder tb) {
        super.onLoadType(tb);

        this.as(AtrBasic::new)
                .label("Contato");

        telefoneFixo = this.addCampo("telefonefixo", STypeTelefoneNacional.class);
        telefoneFixo
                .as(AtrBasic::new)
                .label("Telefone Fixo")
                .as(AtrBootstrap::new)
                .colPreference(2);

        celular = this.addCampo("celular", STypeTelefoneNacional.class);
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
