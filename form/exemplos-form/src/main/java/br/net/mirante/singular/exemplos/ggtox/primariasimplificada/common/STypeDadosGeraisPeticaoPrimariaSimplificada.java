package br.net.mirante.singular.exemplos.ggtox.primariasimplificada.common;


import br.net.mirante.singular.exemplos.ggtox.primariasimplificada.form.SPackagePPSCommon;
import br.net.mirante.singular.form.SInfoType;
import br.net.mirante.singular.form.TypeBuilder;
import br.net.mirante.singular.form.persistence.STypePersistentComposite;
import br.net.mirante.singular.form.type.core.STypeString;
import br.net.mirante.singular.form.type.core.attachment.STypeAttachment;

import static br.net.mirante.singular.exemplos.ggtox.primariasimplificada.form.STypePeticaoPrimariaSimplificada.OBRIGATORIO;

@SInfoType(spackage = SPackagePPSCommon.class)
public class STypeDadosGeraisPeticaoPrimariaSimplificada extends STypePersistentComposite {

    public static final String NUMERO_PROCESSO_PETICAO_MATRIZ = "numeroProcessoPeticaoMatriz";
    public STypeAnvisaNumeroProcesso numeroProcessoPeticaoMatriz;
    public STypeAttachment           declaracaoVinculoPeticaoMatriz;
    public STypeString               justificativa;
    public STypeAttachment           anexoJustificativa;

    @Override
    protected void onLoadType(TypeBuilder builder) {
        super.onLoadType(builder);

        numeroProcessoPeticaoMatriz = addField(NUMERO_PROCESSO_PETICAO_MATRIZ, STypeAnvisaNumeroProcesso.class);
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
                .required(OBRIGATORIO)
                .label("Número do processo da petição matriz")
                .maxLength(17)
                //TODO vincius help para dizer que o número do processo é anvisa
                .asAtrBootstrap()
                .colPreference(4);

        declaracaoVinculoPeticaoMatriz
                .asAtr()
                .required(OBRIGATORIO)
                .label("Declaracao de Vinculo a Petição Matriz")
                .asAtrBootstrap()
                .colPreference(12);

        justificativa
                .withTextAreaView()
                .asAtr()
                .required(OBRIGATORIO)
                .label("Justificativa da solicitação")
                .maxLength(500)
                .asAtrBootstrap()
                .colPreference(12);

        anexoJustificativa
                .asAtrBootstrap()
                .colPreference(12)
                .asAtr()
                .label("Anexo da justificativa (opcional)");

    }
}
