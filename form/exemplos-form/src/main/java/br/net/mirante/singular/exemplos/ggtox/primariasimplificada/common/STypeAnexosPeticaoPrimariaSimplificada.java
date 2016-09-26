package br.net.mirante.singular.exemplos.ggtox.primariasimplificada.common;


import br.net.mirante.singular.exemplos.ggtox.primariasimplificada.form.SPackagePPSCommon;
import br.net.mirante.singular.form.SInfoType;
import br.net.mirante.singular.form.STypeAttachmentList;
import br.net.mirante.singular.form.TypeBuilder;
import br.net.mirante.singular.form.persistence.STypePersistentComposite;

import static br.net.mirante.singular.exemplos.ggtox.primariasimplificada.form.STypePeticaoPrimariaSimplificada.OBRIGATORIO;

@SInfoType(spackage = SPackagePPSCommon.class)
public class STypeAnexosPeticaoPrimariaSimplificada extends STypePersistentComposite {

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

        pareceresTecnicosAvaliacoesDasEmpresas = addFieldListOfAttachment("pareceresTecnicosAvaliacoesDasEmpresas", "parecerTecnicoAvaliacaoDaEmpresa");

        pareceresTecnicosAvaliacoesDasEmpresas
                .asAtr()
                .required(OBRIGATORIO)
                .label("Parecer Técnico de Avaliação da Empresa (PATE)")
                .asAtrBootstrap()
                .colPreference(12);
    }

}