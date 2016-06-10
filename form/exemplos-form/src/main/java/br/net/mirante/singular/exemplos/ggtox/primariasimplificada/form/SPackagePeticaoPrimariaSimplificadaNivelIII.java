package br.net.mirante.singular.exemplos.ggtox.primariasimplificada.form;

import br.net.mirante.singular.exemplos.ggtox.primariasimplificada.common.STypeDadosGeraisPeticaoPrimariaSimplificada;
import br.net.mirante.singular.exemplos.ggtox.primariasimplificada.common.STypeDocumentacaoPeticaoPrimariaSimplificadaNivelIII;
import br.net.mirante.singular.form.*;
import br.net.mirante.singular.form.view.SViewByBlock;


public class SPackagePeticaoPrimariaSimplificadaNivelIII extends SPackage {


    public static final String PACOTE        = "mform.peticao";
    public static final String TIPO          = "PeticaoPrimariaSimplificadaNivelIII";
    public static final String NOME_COMPLETO = PACOTE + "." + TIPO;

    public SPackagePeticaoPrimariaSimplificadaNivelIII() {
        super(PACOTE);
    }

    @Override
    protected void onLoadPackage(PackageBuilder pb) {
        super.onLoadPackage(pb);
        pb.loadPackage(SPackagePPSCommon.class);

        final STypeComposite<SIComposite>                          peticaoSimplificada = pb.createCompositeType(TIPO);
        final STypeDadosGeraisPeticaoPrimariaSimplificada          dadosGerais         = peticaoSimplificada.addField("dadosGerais", STypeDadosGeraisPeticaoPrimariaSimplificada.class);
        final STypeDocumentacaoPeticaoPrimariaSimplificadaNivelIII documentacao        = peticaoSimplificada.addField("documentacao", STypeDocumentacaoPeticaoPrimariaSimplificadaNivelIII.class);

        peticaoSimplificada.withView(new SViewByBlock(), view -> {
            view
                    .newBlock("Dados Gerais")
                    .add(dadosGerais)
                    .newBlock("Documentação")
                    .add(documentacao);
        });

    }

}
