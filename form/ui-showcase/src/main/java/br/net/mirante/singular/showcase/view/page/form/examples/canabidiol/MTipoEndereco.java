package br.net.mirante.singular.showcase.view.page.form.examples.canabidiol;

import br.net.mirante.singular.form.mform.MIComposto;
import br.net.mirante.singular.form.mform.MInfoTipo;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.TipoBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.wicket.AtrBootstrap;
import br.net.mirante.singular.showcase.view.page.form.examples.SelectBuilder;

@MInfoTipo(nome = "MTipoEndereco", pacote = MPacotePeticaoCanabidiol.class)
public class MTipoEndereco extends MTipoComposto<MIComposto> implements CanabidiolUtil {

    @Override
    protected void onCargaTipo(TipoBuilder tb) {
        super.onCargaTipo(tb);


        this.addCampoString("logradouro")
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
                .as(AtrBasic::new)
                .label("Estado")
                .as(AtrBootstrap::new)
                .colPreference(3);
        estado
                .withSelectionOf(SelectBuilder.buildEstados(estado));

        MTipoComposto<?> cidade = this.addCampoComposto("cidade");
        cidade
                .as(AtrBasic::new)
                .label("Cidade")
                .visivel(inst -> hasValue(inst, estado))
                .dependsOn(estado)
                .as(AtrBootstrap::new)
                .colPreference(3);
        cidade.
                setProviderOpcoes(inst ->
                                SelectBuilder
                                        .buildMunicipiosFiltrado(
                                                cidade,
                                                getValue(inst, estado),
                                                inst.getMTipo().novaLista()));
    }
}
