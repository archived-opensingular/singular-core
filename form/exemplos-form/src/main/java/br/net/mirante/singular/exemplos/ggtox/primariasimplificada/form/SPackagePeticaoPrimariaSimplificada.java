package br.net.mirante.singular.exemplos.ggtox.primariasimplificada.form;

import br.net.mirante.singular.exemplos.ggtox.primariasimplificada.common.*;
import br.net.mirante.singular.form.PackageBuilder;
import br.net.mirante.singular.form.SIComposite;
import br.net.mirante.singular.form.SPackage;
import br.net.mirante.singular.form.STypeComposite;
import br.net.mirante.singular.form.type.core.STypeString;
import br.net.mirante.singular.form.view.SViewByBlock;


public class SPackagePeticaoPrimariaSimplificada extends SPackage {


    public static final String PACOTE        = "mform.peticao";
    public static final String TIPO          = "PeticaoPrimariaSimplificada";
    public static final String NOME_COMPLETO = PACOTE + "." + TIPO;

    public SPackagePeticaoPrimariaSimplificada() {
        super(PACOTE);
    }


    @Override
    protected void onLoadPackage(PackageBuilder pb) {

        super.onLoadPackage(pb);

        pb.loadPackage(SPackagePPSCommon.class);

        final STypeComposite<SIComposite>                          peticaoSimplificada = pb.createCompositeType(TIPO);
        final STypeString                                          nivel               = peticaoSimplificada.addFieldString("nivel");
        final STypeDadosGeraisPeticaoPrimariaSimplificada          dadosGerais         = peticaoSimplificada.addField("dadosGerais", STypeDadosGeraisPeticaoPrimariaSimplificada.class);
        final STypeDocumentacaoPeticaoPrimariaSimplificadaNivelI   documentacaoI       = peticaoSimplificada.addField("documentacaoI", STypeDocumentacaoPeticaoPrimariaSimplificadaNivelI.class);
        final STypeDocumentacaoPeticaoPrimariaSimplificadaNivelII  documentacaoII      = peticaoSimplificada.addField("documentacaoII", STypeDocumentacaoPeticaoPrimariaSimplificadaNivelII.class);
        final STypeDocumentacaoPeticaoPrimariaSimplificadaNivelIII documentacaoIII     = peticaoSimplificada.addField("documentacaoIII", STypeDocumentacaoPeticaoPrimariaSimplificadaNivelIII.class);
        final STypeDocumentacaoPeticaoPrimariaSimplificadaNivelIV  documentacaoIV      = peticaoSimplificada.addField("documentacaoIV", STypeDocumentacaoPeticaoPrimariaSimplificadaNivelIV.class);

        nivel
                .selectionOf("I", "II", "III", "IV").withRadioView();

        nivel
                .asAtr().label("Nivel");

        documentacaoI
                .asAtr()
                .visible(si -> si.findNearestValue(nivel).orElse("").equals("I"));

        documentacaoII
                .asAtr()
                .visible(si -> si.findNearestValue(nivel).orElse("").equals("II"));
        documentacaoIII
                .asAtr()
                .visible(si -> si.findNearestValue(nivel).orElse("").equals("III"));

        documentacaoIV
                .asAtr()
                .visible(si -> si.findNearestValue(nivel).orElse("").equals("IV"));

        peticaoSimplificada.withView(new SViewByBlock(), view -> {
            view
                    .addNewBlock("Dados Gerais")
                    .addToBlock(nivel)
                    .addToBlock(dadosGerais)
                    .addNewBlock("Documentação")
                    .addToBlock(documentacaoI)
                    .addToBlock(documentacaoII)
                    .addToBlock(documentacaoIII)
                    .addToBlock(documentacaoIV);
        });
    }

}

