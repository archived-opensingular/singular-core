package br.net.mirante.singular.exemplos.ggtox.primariasimplificada.common;


import br.net.mirante.singular.exemplos.ggtox.primariasimplificada.form.SPackagePPSCommon;
import br.net.mirante.singular.form.SInfoType;
import br.net.mirante.singular.form.STypeAttachmentList;
import br.net.mirante.singular.form.TypeBuilder;
import br.net.mirante.singular.form.persistence.STypePersistentComposite;
import br.net.mirante.singular.form.type.core.STypeString;
import br.net.mirante.singular.form.type.core.attachment.STypeAttachment;

@SInfoType(spackage = SPackagePPSCommon.class)
public class STypeInformacoesProcesso extends STypePersistentComposite {

    public STypeString marcaComercial;
    public STypeAttachment modeloRotulo;
    public STypeAttachment modeloBula;
    public STypeAttachmentList embalagens;

    @Override
    protected void onLoadType(TypeBuilder builder) {

        super.onLoadType(builder);

        this
                .asAtr()
                .label("Informações do Processo");

        marcaComercial = addFieldString("marcaComercial");
        modeloRotulo = addFieldAttachment("modeloRotulo");
        modeloBula = addFieldAttachment("modeloBula");
        embalagens = addFieldListOfAttachment("embalagens", "embalagem");

        marcaComercial
                .asAtr()
                .label("Marca Comercial")
                .asAtrBootstrap()
                .colPreference(12);

        modeloRotulo
                .asAtr()
                .label("Modelo de Rótulo")
                .asAtrBootstrap()
                .colPreference(12);

        modeloBula
                .asAtr()
                .label("Modelo de Bula")
                .asAtrBootstrap()
                .colPreference(12);

        embalagens
                .asAtr()
                .label("Embalagens")
                .asAtrBootstrap()
                .colPreference(12);

    }
}
