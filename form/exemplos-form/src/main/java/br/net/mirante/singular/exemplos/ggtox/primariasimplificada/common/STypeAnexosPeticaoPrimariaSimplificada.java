package br.net.mirante.singular.exemplos.ggtox.primariasimplificada.common;


import br.net.mirante.singular.exemplos.ggtox.primariasimplificada.form.SPackagePPSCommon;
import br.net.mirante.singular.form.SIComposite;
import br.net.mirante.singular.form.SInfoType;
import br.net.mirante.singular.form.STypeComposite;
import br.net.mirante.singular.form.TypeBuilder;

@SInfoType(spackage = SPackagePPSCommon.class)
public class STypeAnexosPeticaoPrimariaSimplificada extends STypeComposite<SIComposite> {

    public STypeDocumentacaoPeticaoPrimariaSimplificadaNivelII  documentacaoII;
    public STypeDocumentacaoPeticaoPrimariaSimplificadaNivelIII documentacaoIII;
    public STypeDocumentacaoPeticaoPrimariaSimplificadaNivelIV  documentacaoIV;

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);

        this
                .asAtrAnnotation()
                .setAnnotated();

        this
                .asAtr()
                .label("Anexos");

        documentacaoII      = addField("documentacaoII", STypeDocumentacaoPeticaoPrimariaSimplificadaNivelII.class);
        documentacaoIII     = addField("documentacaoIII", STypeDocumentacaoPeticaoPrimariaSimplificadaNivelIII.class);
        documentacaoIV      = addField("documentacaoIV", STypeDocumentacaoPeticaoPrimariaSimplificadaNivelIV.class);

    }
}
