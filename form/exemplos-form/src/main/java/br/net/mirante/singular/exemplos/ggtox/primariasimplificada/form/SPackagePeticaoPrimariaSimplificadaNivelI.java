package br.net.mirante.singular.exemplos.ggtox.primariasimplificada.form;

import br.net.mirante.singular.exemplos.ggtox.primariasimplificada.common.STypeDadosGeraisPeticaoPrimariaSimplificada;
import br.net.mirante.singular.exemplos.ggtox.primariasimplificada.common.STypeDocumentacaoPeticaoPrimariaSimplificadaNivelI;
import br.net.mirante.singular.form.*;
import br.net.mirante.singular.form.view.SViewByBlock;


public class SPackagePeticaoPrimariaSimplificadaNivelI extends SPackage {


    public static final String PACOTE        = "mform.peticao";
    public static final String TIPO          = "PeticaoPrimariaSimplificadaNivelI";
    public static final String NOME_COMPLETO = PACOTE + "." + TIPO;

    public SPackagePeticaoPrimariaSimplificadaNivelI() {
        super(PACOTE);
    }

    @Override
    protected void onLoadPackage(PackageBuilder pb) {
        super.onLoadPackage(pb);
        pb.loadPackage(SPackagePPSCommon.class);

        final STypeComposite<SIComposite>                        peticaoSimplificada = pb.createCompositeType(TIPO);
        final STypeDadosGeraisPeticaoPrimariaSimplificada        dadosGerais         = peticaoSimplificada.addField("dadosGerais", STypeDadosGeraisPeticaoPrimariaSimplificada.class);
        final STypeDocumentacaoPeticaoPrimariaSimplificadaNivelI documentacao        = peticaoSimplificada.addField("documentacao", STypeDocumentacaoPeticaoPrimariaSimplificadaNivelI.class);

        peticaoSimplificada.withView(new SViewByBlock(), view -> {
            view
                    .addNewBlock("Dados Gerais")
                    .addToBlock(dadosGerais)
                    .addNewBlock("Documentação")
                    .addToBlock(documentacao);
        });

    }

}
