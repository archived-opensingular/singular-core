package br.net.mirante.singular.exemplos.ggtox.primariasimplificada.common;

import br.net.mirante.singular.exemplos.ggtox.primariasimplificada.form.SPackagePPSCommon;
import br.net.mirante.singular.exemplos.ggtox.primariasimplificada.form.SPackagePeticaoPrimariaSimplificada;
import br.net.mirante.singular.form.SIComposite;
import br.net.mirante.singular.form.SInfoType;
import br.net.mirante.singular.form.STypeComposite;
import br.net.mirante.singular.form.TypeBuilder;
import br.net.mirante.singular.form.type.core.STypeString;
import br.net.mirante.singular.form.type.core.attachment.STypeAttachment;

@SInfoType(spackage = SPackagePPSCommon.class)
public class STypeDadosGeraisPeticaoPrimariaSimplificada extends STypeComposite<SIComposite> {


    @Override
    protected void onLoadType(TypeBuilder builder) {
        super.onLoadType(builder);

        final STypeAttachment guiaRecolhimentoUniao          = addField("guiaRecolhimentoUniao", STypeAttachment.class);
        final STypeAttachment declaracaoVinculoPeticaoMatriz = addField("declaracaoVinculoPeticaoMatriz", STypeAttachment.class);
        final STypeString     justificativa                  = addFieldString("justificativa");

        guiaRecolhimentoUniao
                .asAtr()
                .label("Guia de Recolhimento da União")
                .asAtrBootstrap()
                .colPreference(12);

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

    }
}
