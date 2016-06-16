package br.net.mirante.singular.exemplos.ggtox.primariasimplificada.form;


import br.net.mirante.singular.exemplos.ggtox.primariasimplificada.common.*;
import br.net.mirante.singular.form.PackageBuilder;
import br.net.mirante.singular.form.SPackage;

public class SPackagePPSCommon extends SPackage {

    @Override
    protected void onLoadPackage(PackageBuilder pb) {
        super.onLoadPackage(pb);
        pb.createType(STypeAnvisaNumeroProcesso.class);
        pb.createType(STypeIngredienteAtivo.class);
        pb.createType(STypeDadosGeraisPeticaoPrimariaSimplificada.class);
        pb.createType(STypeDocumentacaoPeticaoPrimariaSimplificadaNivelI.class);
        pb.createType(STypeDocumentacaoPeticaoPrimariaSimplificadaNivelII.class);
        pb.createType(STypeDocumentacaoPeticaoPrimariaSimplificadaNivelIII.class);
        pb.createType(STypeDocumentacaoPeticaoPrimariaSimplificadaNivelIV.class);
        pb.createType(STypeAnexosPeticaoPrimariaSimplificada.class);
        pb.createType(STypeEntidade.class);
        pb.createType(STypeRequerente.class);
        pb.createType(STypeFabricante.class);
        pb.createType(STypeFormuladorConformeMatriz.class);
        pb.createType(STypeFabricanteConformeMatriz.class);
        pb.createType(STypeProdutoTecnicoPeticaoPrimariaSimplificada.class);
        pb.createType(STypeFormulador.class);
        pb.createType(STypeProdutoFormuladoPeticaoPrimariaSimplificada.class);
        pb.createType(STypeRepresentanteLegal.class);
    }
}
