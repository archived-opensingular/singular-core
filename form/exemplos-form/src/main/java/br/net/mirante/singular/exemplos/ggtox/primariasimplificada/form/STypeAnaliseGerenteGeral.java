package br.net.mirante.singular.exemplos.ggtox.primariasimplificada.form;

import br.net.mirante.singular.form.SInfoType;
import br.net.mirante.singular.form.TypeBuilder;
import br.net.mirante.singular.form.persistence.STypePersistentComposite;
import br.net.mirante.singular.form.type.core.STypeHTML;
import br.net.mirante.singular.form.type.core.STypeString;
import br.net.mirante.singular.form.util.SingularPredicates;
import br.net.mirante.singular.form.view.SViewByBlock;
import br.net.mirante.singular.form.view.SViewByPortletRichText;
import br.net.mirante.singular.form.view.SViewTextArea;

@SInfoType(name = "STypeAnaliseGerenteGeral", spackage = SPackagePeticaoPrimariaSimplificada.class)
public class STypeAnaliseGerenteGeral extends STypePersistentComposite {


    public final static String PATH_RESULTADO_ANALISE = "resultadoAnalise";
    public final static String PATH_DESPACHO          = "despacho";
    public final static String PATH_OFICIO            = "oficio";
    public final static String DEFERIR                = "Deferir";
    public final static String INDEFERIR              = "Indeferir";

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);

        final STypeString resultadoAnalise = addField(PATH_RESULTADO_ANALISE, STypeString.class);
        final STypeString despacho         = addField(PATH_DESPACHO, STypeString.class);
        final STypeHTML   oficio           = addField(PATH_OFICIO, STypeHTML.class);

        despacho.setView(SViewTextArea::new);
        oficio.setView(SViewByPortletRichText::new);

        resultadoAnalise.asAtr()
                .label("Resultado da Análise")
                .required();

        resultadoAnalise.selectionOf(DEFERIR, INDEFERIR);
        resultadoAnalise.withRadioView();

        resultadoAnalise.withRadioView();

        despacho.asAtr()
                .label("Despacho")
                .required();

        despacho.asAtr()
                .visible(SingularPredicates.typeValueIsEqualsTo(resultadoAnalise, INDEFERIR));

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
                    .add(despacho)
                    .add(oficio);
        });

    }

}