package br.net.mirante.singular.exemplos.notificacaosimplificada.form;

import br.net.mirante.singular.exemplos.notificacaosimplificada.form.baixorisco.SPackageNotificacaoSimplificadaBaixoRisco;
import br.net.mirante.singular.exemplos.notificacaosimplificada.service.DominioService;
import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SInfoType;
import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.STypeSimple;
import br.net.mirante.singular.form.mform.TypeBuilder;
import br.net.mirante.singular.form.mform.basic.view.SViewAutoComplete;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.mform.util.transformer.Value;
import org.apache.commons.lang3.tuple.Triple;

@SInfoType(spackage = SPackageNotificacaoSimplificada.class)
public class STypeLocalFabricacao extends STypeComposite<SIComposite> {

    public STypeSimple tipoLocalFabricacao;
    public STypeEmpresaPropria empresaPropria;
    public STypeEmpresaTerceirizada empresaTerceirizada;
    public STypeComposite<SIComposite> outroLocalFabricacao;

    static DominioService dominioService(SInstance ins) {
        return ins.getDocument().lookupService(DominioService.class);
    }

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);

        this.asAtrBasic().label("Local de Fabricação");

        tipoLocalFabricacao = this.addFieldInteger("tipoLocalFabricacao");
        tipoLocalFabricacao
                .asAtrBasic()
                .label("Tipo de local");
        tipoLocalFabricacao
                .withRadioView()
                .withSelection()
                .add(1, "Produção Própria")
                .add(2, "Empresa Internacional")
                .add(3, "Empresa Terceirizada")
                .add(4, "Outro Local de Fabricação");


        empresaPropria = this.addField("empresaPropria", STypeEmpresaPropria.class);

        empresaPropria.asAtrBasic()
                .dependsOn(tipoLocalFabricacao)
                .visivel(i -> Integer.valueOf(1).equals(Value.of(i, tipoLocalFabricacao)));

        final STypeEmpresaInternacional empresaInternacional = this.addField("empresaInternacional", STypeEmpresaInternacional.class);

        empresaInternacional
                .asAtrBasic()
                .dependsOn(tipoLocalFabricacao)
                .visivel(i -> Integer.valueOf(2).equals(Value.of(i, tipoLocalFabricacao)));

        empresaTerceirizada = this.addField("empresaTerceirizada", STypeEmpresaTerceirizada.class);

        empresaTerceirizada
                .asAtrBasic()
                .dependsOn(tipoLocalFabricacao)
                .visivel(i -> Integer.valueOf(3).equals(Value.of(i, tipoLocalFabricacao)));


        outroLocalFabricacao = this.addFieldComposite("outroLocalFabricacao");

        STypeString idOutroLocalFabricacao = outroLocalFabricacao.addFieldString("id");
        STypeString razaoSocialOutroLocalFabricacao = outroLocalFabricacao.addFieldString("razaoSocial");
        razaoSocialOutroLocalFabricacao.asAtrBasic().label("Razão Social");
        STypeString enderecoOutroLocalFabricacao = outroLocalFabricacao.addFieldString("endereco");
        outroLocalFabricacao
                .asAtrBasic().label("Outro local de fabricação")
                .dependsOn(tipoLocalFabricacao)
                .visivel(i -> Integer.valueOf(4).equals(Value.of(i, tipoLocalFabricacao)));

        outroLocalFabricacao
                .withSelectionFromProvider(razaoSocialOutroLocalFabricacao, (optionsInstance, lb) -> {
                    for (Triple p : dominioService(optionsInstance).outroLocalFabricacao()) {
                        lb
                                .add()
                                .set(idOutroLocalFabricacao, p.getLeft())
                                .set(razaoSocialOutroLocalFabricacao, p.getMiddle())
                                .set(enderecoOutroLocalFabricacao, p.getRight());
                    }
                })
                .asAtrBasic().label("Outro local de fabricação")
                .getTipo().setView(SViewAutoComplete::new);
    }


}
