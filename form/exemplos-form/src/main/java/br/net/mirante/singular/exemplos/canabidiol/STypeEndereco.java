package br.net.mirante.singular.exemplos.canabidiol;

import br.net.mirante.singular.exemplos.SelectBuilder;
import br.net.mirante.singular.form.mform.SInfoType;
import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.STypeSimple;
import br.net.mirante.singular.form.mform.TypeBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.basic.ui.AtrBootstrap;
import br.net.mirante.singular.form.mform.core.AtrCore;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.mform.options.SOptionsProvider;
import br.net.mirante.singular.form.mform.util.transformer.Value;


@SInfoType(name = "STypeEndereco", spackage = SPackagePeticaoCanabidiol.class)
public class STypeEndereco extends STypeComposite<SIComposite> {


    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);


        this.addFieldString("logradouro")
                .as(AtrCore::new)
                .obrigatorio()
                .as(AtrBasic::new)
                .label("Logradouro")
                .as(AtrBootstrap::new)
                .colPreference(5);

        this.addFieldString("complemento")
                .as(AtrBasic::new)
                .label("Complemento")
                .as(AtrBootstrap::new)
                .colPreference(5);

        this.addFieldString("numero")
                .as(AtrBasic::new)
                .label("Número")
                .as(AtrBootstrap::new)
                .colPreference(2);

        this.addFieldString("bairro")
                .as(AtrBasic::new)
                .label("Bairro")
                .as(AtrBootstrap::new)
                .colPreference(4);

        this.addFieldCEP("CEP")
                .as(AtrBasic::new)
                .label("CEP")
                .as(AtrBootstrap::new)
                .colPreference(2);

        STypeComposite<?> estado = this.addFieldComposite("estado");
        estado
                .as(AtrCore::new)
                .obrigatorio()
                .as(AtrBasic::new)
                .label("Estado")
                .as(AtrBootstrap::new)
                .colPreference(3);
        STypeString siglaUF = estado.addFieldString("sigla");
        STypeString nomeUF = estado.addFieldString("nome");
        estado
                .withSelectionFromProvider(nomeUF,
                        (SOptionsProvider) optionsInstance -> SelectBuilder.buildEstados(estado)
                );

        STypeComposite<?> cidade = this.addFieldComposite("cidade");
        cidade
                .as(AtrCore::new)
                .obrigatorio(inst -> Value.notNull(inst, (STypeSimple) estado.getField(siglaUF)))
                .as(AtrBasic::new)
                .label("Cidade")
                .visivel(inst -> Value.notNull(inst, (STypeSimple) estado.getField(siglaUF)))
                .dependsOn(estado)
                .as(AtrBootstrap::new)
                .colPreference(3);
        cidade.addFieldString("id");
        STypeString nomeCidade = cidade.addFieldString("nome");
        cidade.addFieldString("UF");
        cidade.
                withSelectionFromProvider(nomeCidade, (SOptionsProvider) inst ->
                        SelectBuilder
                                .buildMunicipiosFiltrado(
                                        cidade,
                                        (String) Value.of(inst, (STypeSimple) estado.getField(siglaUF)),
                                        inst.getType().newList()));
    }
}
