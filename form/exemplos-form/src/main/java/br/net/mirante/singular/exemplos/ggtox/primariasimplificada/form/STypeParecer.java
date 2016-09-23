package br.net.mirante.singular.exemplos.ggtox.primariasimplificada.form;

import br.net.mirante.singular.form.SInfoType;
import br.net.mirante.singular.form.TypeBuilder;
import br.net.mirante.singular.form.persistence.STypePersistentComposite;
import br.net.mirante.singular.form.type.core.STypeHTML;
import br.net.mirante.singular.form.type.core.STypeString;
import br.net.mirante.singular.form.util.SingularPredicates;
import br.net.mirante.singular.form.view.SViewByPortletRichText;


@SInfoType(name = "STypeParecer", spackage = SPackagePeticaoPrimariaSimplificada.class)
public class STypeParecer extends STypePersistentComposite {

    public static final String DEFERIR           = "Deferir";
    public static final String RESULTADO_ANALISE = "resultadoAnalise";
    public static final String PARECER           = "parecer";
    public static final String OFICIO            = "oficio";

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);

        final STypeString resultadoAnalise = addField(RESULTADO_ANALISE, STypeString.class);
        final STypeHTML   parecer          = addField(PARECER, STypeHTML.class);
        final STypeHTML   oficio           = addField(OFICIO, STypeHTML.class);

        resultadoAnalise.asAtr()
                .label("Resultado da Análise")
                .required();

        resultadoAnalise.selectionOf(DEFERIR, "Indeferir");
        resultadoAnalise.withRadioView();

        parecer.setView(SViewByPortletRichText::new);
        oficio.setView(SViewByPortletRichText::new);

        parecer.asAtr()
                .dependsOn(resultadoAnalise)
                .visible(SingularPredicates.typeValueIsNotNull(resultadoAnalise))
                .label("Parecer")
                .required();

        oficio.asAtr()
                .dependsOn(resultadoAnalise)
                .visible(SingularPredicates.typeValueIsNotNull(resultadoAnalise))
                .label("Ofício")
                .required();
    }

}