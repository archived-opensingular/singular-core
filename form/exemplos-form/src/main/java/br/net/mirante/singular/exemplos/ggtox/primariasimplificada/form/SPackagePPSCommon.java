package br.net.mirante.singular.exemplos.ggtox.primariasimplificada.form;

import br.net.mirante.singular.exemplos.ggtox.primariasimplificada.common.*;
import br.net.mirante.singular.form.PackageBuilder;
import br.net.mirante.singular.form.SPackage;


public class SPackagePPSCommon extends SPackage {

    @Override
    protected void onLoadPackage(PackageBuilder pb) {
        super.onLoadPackage(pb);

        pb.createType(STypeDadosGeraisPeticaoPrimariaSimplificada.class);
        pb.createType(STypeDocumentacaoPeticaoPrimariaSimplificadaNivelI.class);
        pb.createType(STypeDocumentacaoPeticaoPrimariaSimplificadaNivelII.class);
        pb.createType(STypeDocumentacaoPeticaoPrimariaSimplificadaNivelIII.class);
        pb.createType(STypeDocumentacaoPeticaoPrimariaSimplificadaNivelIV.class);

    }
}
