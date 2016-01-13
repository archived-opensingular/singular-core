package br.net.mirante.singular.showcase.view.page.form.examples.canabidiol;

import br.net.mirante.singular.form.mform.MIComposto;
import br.net.mirante.singular.form.mform.MILista;
import br.net.mirante.singular.form.mform.MInfoTipo;
import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.TipoBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.options.MSelectionableInstance;
import br.net.mirante.singular.form.wicket.AtrBootstrap;
import br.net.mirante.singular.showcase.view.page.form.examples.SelectBuilder;

@MInfoTipo(nome = "MTipoEndereco", pacote = MPacotePeticaoCanabidiol.class)
public class MTipoEndereco extends MTipoComposto<MIComposto> {

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
                .visivel(inst -> {
                    MSelectionableInstance<Object> estadoInstancia = inst.findNearest(estado).orElse(null);
                    return estadoInstancia != null && estadoInstancia.getSelectValue() != null;
                })
                .dependsOn(estado)
                .as(AtrBootstrap::new)
                .colPreference(3);
        cidade.
                setProviderOpcoes(inst -> {
                    MSelectionableInstance<Object> estadoInstancia = inst.findNearest(estado).orElse(null);
                    MILista<? extends MInstancia> lista = inst
                            .getMTipo()
                            .novaLista();
                    if (estadoInstancia != null) {
                        SelectBuilder
                                .buildMunicipiosFiltrado(cidade,
                                        String.valueOf(estadoInstancia.getSelectValue()))
                                .forEach(si -> lista.addElement(si));
                    }
                    return lista;
                });
    }
}
