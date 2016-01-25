package br.net.mirante.singular.showcase.view.page.form.examples.canabidiol;

import br.net.mirante.singular.form.mform.MIComposto;
import br.net.mirante.singular.form.mform.MISimples;
import br.net.mirante.singular.form.mform.MInfoTipo;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.MTipoSimples;
import br.net.mirante.singular.form.mform.TipoBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.core.AtrCore;
import br.net.mirante.singular.form.mform.options.MOptionsProvider;
import br.net.mirante.singular.form.mform.util.transformer.Val;
import br.net.mirante.singular.form.wicket.AtrBootstrap;
import br.net.mirante.singular.showcase.view.page.form.examples.SelectBuilder;

@MInfoTipo(nome = "MTipoEndereco", pacote = MPacotePeticaoCanabidiol.class)
public class MTipoEndereco extends MTipoComposto<MIComposto>  {


    @Override
    protected void onCargaTipo(TipoBuilder tb) {
        super.onCargaTipo(tb);


        this.addCampoString("logradouro")
                .as(AtrCore::new)
                .obrigatorio()
                .as(AtrBasic::new)
                .label("Logradouro")
                .as(AtrBootstrap::new)
                .colPreference(5);

        this.addCampoString("complemento")
                .as(AtrBasic::new)
                .label("Complemento")
                .as(AtrBootstrap::new)
                .colPreference(5);

        this.addCampoString("numero")
                .as(AtrBasic::new)
                .label("Número")
                .as(AtrBootstrap::new)
                .colPreference(2);

        this.addCampoString("bairro")
                .as(AtrBasic::new)
                .label("Bairro")
                .as(AtrBootstrap::new)
                .colPreference(4);

        this.addCampoCEP("CEP")
                .as(AtrBasic::new)
                .label("CEP")
                .as(AtrBootstrap::new)
                .colPreference(2);

        MTipoComposto<?> estado = this.addCampoComposto("estado");
        estado
                .as(AtrCore::new)
                .obrigatorio()
                .as(AtrBasic::new)
                .label("Estado")
                .as(AtrBootstrap::new)
                .colPreference(3);
        estado
                .withSelectionFromProvider("nome", (MOptionsProvider) isnt -> SelectBuilder.buildEstados(estado));

        MTipoComposto<?> cidade = this.addCampoComposto("cidade");
        cidade
                .as(AtrCore::new)
                .obrigatorio(inst -> Val.notNull(inst, (MTipoSimples) estado.getCampo("id")))
                .as(AtrBasic::new)
                .label("Cidade")
                .visivel(inst -> Val.notNull(inst, (MTipoSimples)estado.getCampo("id")))
                .dependsOn(estado)
                .as(AtrBootstrap::new)
                .colPreference(3);
        cidade.
                withSelectionFromProvider("nome", (MOptionsProvider) inst ->
                        SelectBuilder
                                .buildMunicipiosFiltrado(
                                        cidade,
                                        (String)Val.of(inst, (MTipoSimples)estado.getCampo("id")),
                                        inst.getMTipo().novaLista()));
    }
}
