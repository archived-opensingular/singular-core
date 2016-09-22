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

@SInfoType(name = "STypeAnaliseGerente", spackage = SPackagePeticaoPrimariaSimplificada.class)
public class STypeAnaliseGerente extends STypePersistentComposite {

    public static final String RESULTADO_ANALISE = "resultadoAnalise";
    public static final String DESPACHO          = "despacho";
    public static final String OFICIO            = "oficio";
    public static final String APROVAR           = "Aprovar";
    public static final String REPROVAR          = "Reprovar";

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);

        final STypeString resultadoAnalise = addField(RESULTADO_ANALISE, STypeString.class);
        final STypeString despacho         = addField(DESPACHO, STypeString.class);
        final STypeHTML   oficio           = addField(OFICIO, STypeHTML.class);

        despacho.setView(SViewTextArea::new);
        oficio.setView(SViewByPortletRichText::new);

        resultadoAnalise.asAtr()
                .label("Resultado da Análise")
                .required();

        resultadoAnalise.selectionOf(APROVAR, REPROVAR);
        resultadoAnalise.withRadioView();

        despacho.asAtr()
                .label("Despacho")
                .maxLength(5000)
                .visible(SingularPredicates.typeValueIsEqualsTo(resultadoAnalise, REPROVAR))
                .required();

        oficio.withInitListener(sihtml -> {
            if (sihtml.isEmptyOfData()) {
                final ClassLoader loader = this.getClass().getClassLoader();
                sihtml.fillFromInputStream(loader.getResourceAsStream("modelo/ModeloParecer.html"));
            }
        });

        oficio.asAtr()
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