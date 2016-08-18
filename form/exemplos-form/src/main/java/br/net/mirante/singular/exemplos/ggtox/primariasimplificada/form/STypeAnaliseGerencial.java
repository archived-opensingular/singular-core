package br.net.mirante.singular.exemplos.ggtox.primariasimplificada.form;

import br.net.mirante.singular.form.SInfoType;
import br.net.mirante.singular.form.TypeBuilder;
import br.net.mirante.singular.form.persistence.STypePersistentComposite;
import br.net.mirante.singular.form.type.core.STypeHTML;
import br.net.mirante.singular.form.view.SViewByBlock;

@SInfoType(name = "STypeAnaliseGerencial", spackage = SPackagePeticaoPrimariaSimplificada.class)
public class STypeAnaliseGerencial extends STypePersistentComposite {

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);
        final STypeHTML parecer = addField("parecer", STypeHTML.class);
        parecer.asAtr().required();
        withView(new SViewByBlock(), vbb -> {
            vbb.newBlock("Parecer").add(parecer);
        });
    }

}