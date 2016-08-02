package br.net.mirante.singular.exemplos.ggtox.primariasimplificada.common;


import br.net.mirante.singular.exemplos.ggtox.primariasimplificada.form.SPackagePPSCommon;
import br.net.mirante.singular.form.SIComposite;
import br.net.mirante.singular.form.SInfoType;
import br.net.mirante.singular.form.STypeAttachmentList;
import br.net.mirante.singular.form.STypeComposite;
import br.net.mirante.singular.form.TypeBuilder;

@SInfoType(spackage = SPackagePPSCommon.class)
public class STypeAnexosPeticaoPrimariaSimplificada extends STypeComposite<SIComposite> {

    public STypeAttachmentList informacoesSobreCulturaIndicacao;
    public STypeAttachmentList pareceresTecnicosAvaliacoesDasEmpresas;

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);

        this
                .asAtrAnnotation()
                .setAnnotated();

        this
                .asAtr()
                .label("Anexos");

        informacoesSobreCulturaIndicacao = addFieldListOfAttachment("informacoesSobreCulturaIndicacao", "informacaoSobreCulturaIndicacao");
        pareceresTecnicosAvaliacoesDasEmpresas = addFieldListOfAttachment("pareceresTecnicosAvaliacoesDasEmpresas", "parecerTecnicoAvaliacaoDaEmpresa");

        informacoesSobreCulturaIndicacao
                .asAtr()
                .label("Informações Sobre Cultura e Indicação")
                .asAtrBootstrap()
                .colPreference(12);

        pareceresTecnicosAvaliacoesDasEmpresas
                .asAtr()
                .label("Parecer Técnico de Avaliação da Empresa (PATE)")
                .asAtrBootstrap()
                .colPreference(12);


    }
}
