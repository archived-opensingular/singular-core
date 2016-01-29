package br.net.mirante.singular.showcase.view.page.form.examples.canabidiol;

import br.net.mirante.singular.form.mform.MIComposto;
import br.net.mirante.singular.form.mform.MInfoTipo;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.MTipoSimples;
import br.net.mirante.singular.form.mform.TipoBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.basic.ui.AtrBootstrap;
import br.net.mirante.singular.form.mform.core.AtrCore;
import br.net.mirante.singular.form.mform.core.MTipoString;
import br.net.mirante.singular.form.mform.options.MOptionsProvider;
import br.net.mirante.singular.form.mform.util.transformer.Val;
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
        MTipoString siglaUF = estado.addCampoString("sigla");
        MTipoString nomeUF = estado.addCampoString("nome");
        estado
                .withSelectionFromProvider(nomeUF,
                        (MOptionsProvider) optionsInstance ->SelectBuilder.buildEstados(estado)
                );

        MTipoComposto<?> cidade = this.addCampoComposto("cidade");
        cidade
                .as(AtrCore::new)
                .obrigatorio(inst -> Val.notNull(inst, (MTipoSimples) estado.getCampo(siglaUF)))
                .as(AtrBasic::new)
                .label("Cidade")
                .visivel(inst -> Val.notNull(inst, (MTipoSimples) estado.getCampo(siglaUF)))
                .dependsOn(estado)
                .as(AtrBootstrap::new)
                .colPreference(3);
        cidade.addCampoString("id");
        MTipoString nomeCidade = cidade.addCampoString("nome");
        cidade.addCampoString("UF");
        cidade.
                withSelectionFromProvider(nomeCidade, (MOptionsProvider) inst ->
                        SelectBuilder
                                .buildMunicipiosFiltrado(
                                        cidade,
                                        (String) Val.of(inst, (MTipoSimples) estado.getCampo(siglaUF)),
                                        inst.getMTipo().novaLista()));
    }
}
