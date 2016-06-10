package br.net.mirante.singular.exemplos.ggtox.primariasimplificada.form;


import br.net.mirante.singular.exemplos.ggtox.primariasimplificada.common.STypeDadosGeraisPeticaoPrimariaSimplificada;
import br.net.mirante.singular.exemplos.ggtox.primariasimplificada.common.STypeDocumentacaoPeticaoPrimariaSimplificadaNivelII;
import br.net.mirante.singular.form.*;
import br.net.mirante.singular.form.view.SViewByBlock;

public class SPackagePeticaoPrimariaSimplificadaNivelII extends SPackage {


    public static final String PACOTE        = "mform.peticao";
    public static final String TIPO          = "PeticaoPrimariaSimplificadaNivelII";
    public static final String NOME_COMPLETO = PACOTE + "." + TIPO;

    public SPackagePeticaoPrimariaSimplificadaNivelII() {
        super(PACOTE);
    }

    @Override
    protected void onLoadPackage(PackageBuilder pb) {
        super.onLoadPackage(pb);
        pb.loadPackage(SPackagePPSCommon.class);

        final STypeComposite<SIComposite>                         peticaoSimplificada = pb.createCompositeType(TIPO);
        final STypeDadosGeraisPeticaoPrimariaSimplificada         dadosGerais         = peticaoSimplificada.addField("dadosGerais", STypeDadosGeraisPeticaoPrimariaSimplificada.class);
        final STypeDocumentacaoPeticaoPrimariaSimplificadaNivelII documentacao        = peticaoSimplificada.addField("documentacao", STypeDocumentacaoPeticaoPrimariaSimplificadaNivelII.class);

        peticaoSimplificada.withView(new SViewByBlock(), view -> {
            view
                    .newBlock("Dados Gerais")
                    .add(dadosGerais)
                    .newBlock("Documentação")
                    .add(documentacao);
        });

    }
}
