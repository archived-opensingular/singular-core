package br.net.mirante.singular.showcase.view.page.form.examples.canabidiol;

import br.net.mirante.singular.form.mform.MInfoTipo;
import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.STypeSimple;
import br.net.mirante.singular.form.mform.TypeBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.basic.ui.AtrBootstrap;
import br.net.mirante.singular.form.mform.core.AtrCore;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.mform.options.MOptionsProvider;
import br.net.mirante.singular.form.mform.util.transformer.Value;
import br.net.mirante.singular.showcase.view.page.form.examples.SelectBuilder;

@MInfoTipo(nome = "MTipoEndereco", pacote = SPackagePeticaoCanabidiol.class)
public class STypeEndereco extends STypeComposite<SIComposite> {


    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);


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

        STypeComposite<?> estado = this.addCampoComposto("estado");
        estado
                .as(AtrCore::new)
                .obrigatorio()
                .as(AtrBasic::new)
                .label("Estado")
                .as(AtrBootstrap::new)
                .colPreference(3);
        STypeString siglaUF = estado.addCampoString("sigla");
        STypeString nomeUF = estado.addCampoString("nome");
        estado
                .withSelectionFromProvider(nomeUF,
                        (MOptionsProvider) optionsInstance ->SelectBuilder.buildEstados(estado)
                );

        STypeComposite<?> cidade = this.addCampoComposto("cidade");
        cidade
                .as(AtrCore::new)
                .obrigatorio(inst -> Value.notNull(inst, (STypeSimple) estado.getCampo(siglaUF)))
                .as(AtrBasic::new)
                .label("Cidade")
                .visivel(inst -> Value.notNull(inst, (STypeSimple) estado.getCampo(siglaUF)))
                .dependsOn(estado)
                .as(AtrBootstrap::new)
                .colPreference(3);
        cidade.addCampoString("id");
        STypeString nomeCidade = cidade.addCampoString("nome");
        cidade.addCampoString("UF");
        cidade.
                withSelectionFromProvider(nomeCidade, (MOptionsProvider) inst ->
                        SelectBuilder
                                .buildMunicipiosFiltrado(
                                        cidade,
                                        (String) Value.of(inst, (STypeSimple) estado.getCampo(siglaUF)),
                                        inst.getType().novaLista()));
    }
}
