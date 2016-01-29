package br.net.mirante.singular.showcase.view.page.form.examples.canabidiol;

import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.MInfoTipo;
import br.net.mirante.singular.form.mform.STypeComposto;
import br.net.mirante.singular.form.mform.TipoBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.basic.ui.AtrBootstrap;
import br.net.mirante.singular.form.mform.core.AtrCore;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.mform.options.MOptionsProvider;
import br.net.mirante.singular.showcase.view.page.form.examples.SelectBuilder;

@MInfoTipo(nome = "MTipoMedico", pacote = SPackagePeticaoCanabidiol.class)
public class STypeMedico extends STypeComposto<SIComposite> {

    @Override
    protected void onLoadType(TipoBuilder tb) {
        super.onLoadType(tb);

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

        STypeComposto<?> estado = this.addCampoComposto("UFCRM");
        estado
                .as(AtrCore::new)
                .obrigatorio()
                .as(AtrBasic::new)
                .label("UF do CRM")
                .as(AtrBootstrap::new)
                .colPreference(3);
        estado.addCampoString("sigla");
        STypeString nomeUF = estado.addCampoString("nome");
        estado
                .withSelectionFromProvider(nomeUF, (MOptionsProvider) inst -> SelectBuilder.buildEstados(estado));

        this.addCampoCPF("cpf")
                .as(AtrBasic::new)
                .label("CPF")
                .as(AtrBootstrap::new)
                .colPreference(3);


        this.addCampo("endereco", STypeEndereco.class)
                .as(AtrBasic::new)
                .label("Endereço");

        STypeContato tipoTelefone = this.addCampo("contato", STypeContato.class);
        tipoTelefone
                .telefoneFixo
                .as(AtrCore::new)
                .obrigatorio();

    }
}
