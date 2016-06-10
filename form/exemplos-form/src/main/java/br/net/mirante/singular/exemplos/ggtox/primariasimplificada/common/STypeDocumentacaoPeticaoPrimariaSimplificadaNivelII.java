package br.net.mirante.singular.exemplos.ggtox.primariasimplificada.common;


import br.net.mirante.singular.exemplos.ggtox.primariasimplificada.form.SPackagePPSCommon;
import br.net.mirante.singular.form.SInfoType;
import br.net.mirante.singular.form.STypeAttachmentList;
import br.net.mirante.singular.form.TypeBuilder;

@SInfoType(spackage = SPackagePPSCommon.class)
public class STypeDocumentacaoPeticaoPrimariaSimplificadaNivelII extends STypeDocumentacaoPeticaoPrimariaSimplificadaNivelI {

    @Override
    protected void onLoadType(TypeBuilder builder) {
        super.onLoadType(builder);

        final STypeAttachmentList informacoesSobreProdutoTecnico = addFieldListOfAttachment("informacoesSobreProdutoTecnico", "informacaoSobreProdutoTecnico");

        informacoesSobreProdutoTecnico
                .asAtr()
                .label("Informações sobre o produto técnico")
                .asAtrBootstrap()
                .colPreference(12);

    }
}
