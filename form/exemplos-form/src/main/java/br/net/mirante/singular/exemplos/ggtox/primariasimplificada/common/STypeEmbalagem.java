package br.net.mirante.singular.exemplos.ggtox.primariasimplificada.common;


import br.net.mirante.singular.exemplos.ggtox.primariasimplificada.form.SPackagePPSCommon;
import br.net.mirante.singular.form.*;
import br.net.mirante.singular.form.type.core.STypeString;
import br.net.mirante.singular.form.type.core.attachment.STypeAttachment;

@SInfoType(spackage = SPackagePPSCommon.class)
public class STypeEmbalagem extends STypeComposite<SIComposite> {

    public STypeString marcaComercial;
    public STypeAttachment modeloRotuloBula;
    public STypeAttachmentList embalagens;

    @Override
    protected void onLoadType(TypeBuilder builder) {

        super.onLoadType(builder);

        marcaComercial = addFieldString("marcaComercial");
        modeloRotuloBula = addFieldAttachment("modeloRotuloBula");
        embalagens = addFieldListOfAttachment("embalagens", "embalagem");

        marcaComercial
                .asAtr()
                .label("Marca Comercial")
                .asAtrBootstrap()
                .colPreference(12);

        modeloRotuloBula
                .asAtr()
                .label("Modelo de Rótulo e Bula")
                .asAtrBootstrap()
                .colPreference(12);

        embalagens
                .asAtr()
                .label("Embalagens")
                .asAtrBootstrap()
                .colPreference(12);

    }
}
