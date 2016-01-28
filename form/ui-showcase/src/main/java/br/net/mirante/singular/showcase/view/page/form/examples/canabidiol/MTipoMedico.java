package br.net.mirante.singular.showcase.view.page.form.examples.canabidiol;

import br.net.mirante.singular.form.mform.MIComposto;
import br.net.mirante.singular.form.mform.MInfoTipo;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.TipoBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.basic.ui.AtrBootstrap;
import br.net.mirante.singular.form.mform.core.AtrCore;
import br.net.mirante.singular.form.mform.core.MTipoString;
import br.net.mirante.singular.form.mform.options.MOptionsProvider;
import br.net.mirante.singular.showcase.view.page.form.examples.SelectBuilder;

@MInfoTipo(nome = "MTipoMedico", pacote = MPacotePeticaoCanabidiol.class)
public class MTipoMedico extends MTipoComposto<MIComposto> {

    @Override
    protected void onCargaTipo(TipoBuilder tb) {
        super.onCargaTipo(tb);

        this
                .addCampoString("nome")
                .as(AtrCore::new)
                .obrigatorio()
                .as(AtrBasic::new)
                .label("Nome do Médico")
                .as(AtrBootstrap::new)
                .colPreference(6);

        this
                .addCampoString("CRM")
                .as(AtrCore::new)
                .obrigatorio()
                .as(AtrBasic::new)
                .label("Número do CRM")
                .as(AtrBootstrap::new)
                .colPreference(3);

        MTipoComposto<?> estado = this.addCampoComposto("UFCRM");
        estado
                .as(AtrCore::new)
                .obrigatorio()
                .as(AtrBasic::new)
                .label("UF do CRM")
                .as(AtrBootstrap::new)
                .colPreference(3);
        estado.addCampoString("sigla");
        MTipoString nomeUF = estado.addCampoString("nome");
        estado
                .withSelectionFromProvider(nomeUF, (MOptionsProvider) inst -> SelectBuilder.buildEstados(estado));

        this.addCampoCPF("cpf")
                .as(AtrBasic::new)
                .label("CPF")
                .as(AtrBootstrap::new)
                .colPreference(3);


        this.addCampo("endereco", MTipoEndereco.class)
                .as(AtrBasic::new)
                .label("Endereço");

        MTipoContato tipoTelefone = this.addCampo("contato", MTipoContato.class);
        tipoTelefone
                .telefoneFixo
                .as(AtrCore::new)
                .obrigatorio();

    }
}
