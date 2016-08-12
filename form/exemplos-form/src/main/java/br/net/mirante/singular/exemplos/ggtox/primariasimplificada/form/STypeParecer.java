package br.net.mirante.singular.exemplos.ggtox.primariasimplificada.form;

import br.net.mirante.singular.form.SIComposite;
import br.net.mirante.singular.form.SInfoType;
import br.net.mirante.singular.form.STypeComposite;
import br.net.mirante.singular.form.TypeBuilder;
import br.net.mirante.singular.form.type.core.STypeHTML;


@SInfoType(name = "STypeParecer", spackage = SPackagePeticaoPrimariaSimplificada.class)
public class STypeParecer extends STypeComposite<SIComposite> {

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);
        STypeHTML parecer = addField("parecer", STypeHTML.class);
        parecer.asAtr().label("Parecer");
    }

}