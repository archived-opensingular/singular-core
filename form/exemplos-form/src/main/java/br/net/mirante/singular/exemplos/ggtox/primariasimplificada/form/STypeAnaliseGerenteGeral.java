package br.net.mirante.singular.exemplos.ggtox.primariasimplificada.form;

import br.net.mirante.singular.form.SInfoType;
import br.net.mirante.singular.form.TypeBuilder;
import br.net.mirante.singular.form.persistence.STypePersistentComposite;
import br.net.mirante.singular.form.type.core.STypeHTML;
import br.net.mirante.singular.form.view.SViewByBlock;

@SInfoType(name = "STypeAnaliseGerenteGeral", spackage = SPackagePeticaoPrimariaSimplificada.class)
public class STypeAnaliseGerenteGeral extends STypePersistentComposite {

    private final String PARECER = "parecer";
    private final String OFICIO  = "oficio";

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);

        final STypeHTML parecer = addField(PARECER, STypeHTML.class);
        final STypeHTML oficio  = addField(OFICIO, STypeHTML.class);

        parecer.withInitListener(sihtml -> {
            if (sihtml.isEmptyOfData()) {
                final ClassLoader loader = this.getClass().getClassLoader();
                sihtml.fillFromInputStream(loader.getResourceAsStream("modelo/ModeloParecer.html"));
            }
        });

        parecer.asAtr().required();

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
            vbb.newBlock("Parecer").add(parecer);
        });
    }

}