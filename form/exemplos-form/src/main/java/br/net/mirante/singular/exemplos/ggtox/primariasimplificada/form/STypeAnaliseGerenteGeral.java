package br.net.mirante.singular.exemplos.ggtox.primariasimplificada.form;

import br.net.mirante.singular.form.SInfoType;
import br.net.mirante.singular.form.TypeBuilder;
import br.net.mirante.singular.form.persistence.STypePersistentComposite;
import br.net.mirante.singular.form.type.core.STypeHTML;
import br.net.mirante.singular.form.type.core.STypeString;
import br.net.mirante.singular.form.view.SViewByBlock;

@SInfoType(name = "STypeAnaliseGerenteGeral", spackage = SPackagePeticaoPrimariaSimplificada.class)
public class STypeAnaliseGerenteGeral extends STypePersistentComposite {


    public final static String PATH_RESULTADO_ANALISE = "resultadoAnalise";
    public final static String PATH_PARECER           = "parecer";
    public final static String PATH_OFICIO            = "oficio";
    public final static String DEFERIR                = "Deferir";
    public final static String INDEFERIR              = "Indeferir";

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);

        final STypeString resultadoAnalise = addField(PATH_RESULTADO_ANALISE, STypeString.class);
        final STypeHTML   parecer          = addField(PATH_PARECER, STypeHTML.class);
        final STypeHTML   oficio           = addField(PATH_OFICIO, STypeHTML.class);

        resultadoAnalise.asAtr()
                .label("Resultado da Análise")
                .required();

        resultadoAnalise.selectionOf(DEFERIR, INDEFERIR);
        resultadoAnalise.withRadioView();

        parecer.withInitListener(sihtml -> {
            if (sihtml.isEmptyOfData()) {
                final ClassLoader loader = this.getClass().getClassLoader();
                sihtml.fillFromInputStream(loader.getResourceAsStream("modelo/ModeloParecer.html"));
            }
        });

        parecer
                .asAtr()
                .label("Parecer")
                .required();

        oficio.withInitListener(sihtml -> {
            if (sihtml.isEmptyOfData()) {
                final ClassLoader loader = this.getClass().getClassLoader();
                sihtml.fillFromInputStream(loader.getResourceAsStream("modelo/ModeloParecer.html"));
            }
        });

        oficio
                .asAtr()
                .label("Ofício")
                .required();


        withView(new SViewByBlock(), vbb -> {
            vbb.newBlock("Análise Gerencial")
                    .add(resultadoAnalise)
                    .add(parecer)
                    .add(oficio);
        });

    }

}