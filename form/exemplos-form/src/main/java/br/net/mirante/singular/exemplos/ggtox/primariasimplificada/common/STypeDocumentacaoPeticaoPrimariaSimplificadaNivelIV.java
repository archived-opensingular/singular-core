package br.net.mirante.singular.exemplos.ggtox.primariasimplificada.common;

import br.net.mirante.singular.exemplos.ggtox.primariasimplificada.form.SPackagePPSCommon;
import br.net.mirante.singular.form.SInfoType;
import br.net.mirante.singular.form.STypeAttachmentList;
import br.net.mirante.singular.form.TypeBuilder;

@SInfoType(spackage = SPackagePPSCommon.class)
public class STypeDocumentacaoPeticaoPrimariaSimplificadaNivelIV extends STypeDocumentacaoPeticaoPrimariaSimplificadaNivelIII {

    @Override
    protected void onLoadType(TypeBuilder builder) {
        super.onLoadType(builder);

        final STypeAttachmentList informacoesSobreCulturaIndicacao       = addFieldListOfAttachment("informacoesSobreCulturaIndicacao", "informacaoSobreCulturaIndicacao");
        final STypeAttachmentList pareceresTecnicosAvaliacoesDasEmpresas = addFieldListOfAttachment("pareceresTecnicosAvaliacoesDasEmpresas", "parecerTecnicoAvaliacaoDaEmpresa");

        informacoesSobreCulturaIndicacao
                .asAtr()
                .label("Informações sobre cultura e indicação")
                .asAtrBootstrap()
                .colPreference(12);

        pareceresTecnicosAvaliacoesDasEmpresas
                .asAtr()
                .label("Parecer técnicos de Avaliação da Empresa")
                .asAtrBootstrap()
                .colPreference(12);

    }
}
