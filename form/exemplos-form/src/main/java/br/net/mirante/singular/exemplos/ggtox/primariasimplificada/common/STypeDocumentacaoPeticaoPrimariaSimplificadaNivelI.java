package br.net.mirante.singular.exemplos.ggtox.primariasimplificada.common;


import br.net.mirante.singular.exemplos.ggtox.primariasimplificada.form.SPackagePPSCommon;
import br.net.mirante.singular.form.*;

@SInfoType(spackage = SPackagePPSCommon.class)
public class STypeDocumentacaoPeticaoPrimariaSimplificadaNivelI extends STypeComposite<SIComposite> {

    public STypeAttachmentList marcasComerciais;
    public STypeAttachmentList modelosRotulos;
    public STypeAttachmentList modelosBulas;

    @Override
    protected void onLoadType(TypeBuilder builder) {

        super.onLoadType(builder);

        marcasComerciais = addFieldListOfAttachment("marcasComerciais", "marcaComercial");
        modelosRotulos = addFieldListOfAttachment("modelosRotulos", "modeloRotulo");
        modelosBulas = addFieldListOfAttachment("modelosBulas", "modeloBula");

        marcasComerciais
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
