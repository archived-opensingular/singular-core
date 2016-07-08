package br.net.mirante.singular.exemplos.ggtox.primariasimplificada.common;


import br.net.mirante.singular.exemplos.ggtox.primariasimplificada.form.SPackagePPSCommon;
import br.net.mirante.singular.form.*;

@SInfoType(spackage = SPackagePPSCommon.class)
public class STypeDocumentacaoPeticaoPrimariaSimplificadaNivelIV extends STypeDocumentacaoPeticaoPrimariaSimplificadaNivelIII {

    public STypeAttachmentList informacoesSobreCulturaIndicacao;
    public STypeAttachmentList pareceresTecnicosAvaliacoesDasEmpresas;

    @Override
    protected void onLoadType(TypeBuilder builder) {
        super.onLoadType(builder);

        informacoesSobreCulturaIndicacao = addFieldListOfAttachment("informacoesSobreCulturaIndicacao", "informacaoSobreCulturaIndicacao");
        pareceresTecnicosAvaliacoesDasEmpresas = addFieldListOfAttachment("pareceresTecnicosAvaliacoesDasEmpresas", "parecerTecnicoAvaliacaoDaEmpresa");

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
