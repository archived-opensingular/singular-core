package br.net.mirante.singular.exemplos.ggtox.primariasimplificada.common;


import br.net.mirante.singular.exemplos.ggtox.primariasimplificada.form.SPackagePPSCommon;
import br.net.mirante.singular.form.SIComposite;
import br.net.mirante.singular.form.SInfoType;
import br.net.mirante.singular.form.STypeComposite;
import br.net.mirante.singular.form.TypeBuilder;
import br.net.mirante.singular.form.type.core.STypeString;
import br.net.mirante.singular.form.type.core.attachment.STypeAttachment;

@SInfoType(spackage = SPackagePPSCommon.class)
public class STypeDadosGeraisPeticaoPrimariaSimplificada extends STypeComposite<SIComposite> {

    public STypeAnvisaNumeroProcesso numeroProcessoPeticaoMatriz;
    public STypeAttachment           declaracaoVinculoPeticaoMatriz;
    public STypeString               justificativa;
    public STypeAttachment           anexoJustificativa;

    @Override
    protected void onLoadType(TypeBuilder builder) {
        super.onLoadType(builder);

        numeroProcessoPeticaoMatriz = addField("numeroProcessoPeticaoMatriz", STypeAnvisaNumeroProcesso.class);
        declaracaoVinculoPeticaoMatriz = addField("declaracaoVinculoPeticaoMatriz", STypeAttachment.class);
        justificativa = addFieldString("justificativa");
        anexoJustificativa = addField("anexoJustificativa", STypeAttachment.class);

        this
                .asAtrAnnotation()
                .setAnnotated();

        this
                .asAtr()
                .label("Petição Matriz");

        numeroProcessoPeticaoMatriz
                .asAtr()
                .required()
                .label("Número do processo da petição matriz")
                //TODO vincius help para dizer que o número do processo é anvisa
                .asAtrBootstrap()
                .colPreference(4);

        declaracaoVinculoPeticaoMatriz
                .asAtr()
                .required()
                .label("Declaracao de Vinculo a Petição Matriz")
                .asAtrBootstrap()
                .colPreference(12);

        justificativa
                .withTextAreaView()
                .asAtr()
                .required()
                .label("Justificativa da solicitação")
                .asAtrBootstrap()
                .colPreference(12);

        anexoJustificativa
                .asAtrBootstrap()
                .colPreference(12)
                .asAtr()
                .label("Anexo da justificativa (opcional)");

    }
}
