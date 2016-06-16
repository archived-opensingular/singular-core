package br.net.mirante.singular.exemplos.ggtox.primariasimplificada.common;


import br.net.mirante.singular.exemplos.ggtox.primariasimplificada.form.SPackagePPSCommon;
import br.net.mirante.singular.form.SInfoType;
import br.net.mirante.singular.form.TypeBuilder;

@SInfoType(spackage = SPackagePPSCommon.class)
public class STypeDocumentacaoPeticaoPrimariaSimplificadaNivelIII extends STypeDocumentacaoPeticaoPrimariaSimplificadaNivelII {

    @Override
    protected void onLoadType(TypeBuilder builder) {
        super.onLoadType(builder);

//        final STypeAttachmentList informacoesSobreFormulador = addFieldListOfAttachment("informacoesSobreFormulador", "informacaoSobreFormulador");
//
//        informacoesSobreFormulador
//                .asAtrBasic()
//                .label("Informações sobre o formulador")
//                .asAtrBootstrap()
//                .colPreference(12);
    }

}