package br.net.mirante.singular.exemplos.ggtox.primariasimplificada.form;


import br.net.mirante.singular.form.SInfoType;
import br.net.mirante.singular.form.TypeBuilder;
import br.net.mirante.singular.form.persistence.STypePersistentComposite;
import br.net.mirante.singular.form.type.core.STypeHTML;
import br.net.mirante.singular.form.view.SViewByPortletRichText;

@SInfoType(name = "STypeParecerExigencia", spackage = SPackagePeticaoPrimariaSimplificada.class)
public class STypeParecerExigencia extends STypePersistentComposite {

    public static final String PARECER = "parecer";

    @Override
    protected void onLoadType(TypeBuilder tb) {
        final STypeHTML parecer = addField(PARECER, STypeHTML.class);
        parecer.setView(SViewByPortletRichText::new);
        parecer.asAtr().label("Parecer").required();
    }

}