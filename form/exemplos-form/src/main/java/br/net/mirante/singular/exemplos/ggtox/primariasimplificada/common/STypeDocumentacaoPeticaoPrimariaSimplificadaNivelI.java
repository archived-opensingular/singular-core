package br.net.mirante.singular.exemplos.ggtox.primariasimplificada.common;


import br.net.mirante.singular.exemplos.ggtox.primariasimplificada.form.SPackagePPSCommon;
import br.net.mirante.singular.form.*;

@SInfoType(spackage = SPackagePPSCommon.class)
public class STypeDocumentacaoPeticaoPrimariaSimplificadaNivelI extends STypeComposite<SIComposite> {

    @Override
    protected void onLoadType(TypeBuilder builder) {

        super.onLoadType(builder);

        final STypeAttachmentList marcarComerciais = addFieldListOfAttachment("marcasComerciais", "marcaComercial");
        final STypeAttachmentList modelosRotulos   = addFieldListOfAttachment("modelosRotulos", "modeloRotulo");
        final STypeAttachmentList modelosBulas     = addFieldListOfAttachment("modelosBulas", "modeloBula");

        marcarComerciais
                .asAtr()
                .label("Marcas Comerciais")
                .asAtrBootstrap()
                .colPreference(12);

        modelosRotulos
                .asAtr()
                .label("Modelos de Rótulos")
                .asAtrBootstrap()
                .colPreference(12);

        modelosBulas
                .asAtr()
                .label("Modelos de Bulas")
                .asAtrBootstrap()
                .colPreference(12);

    }
}
