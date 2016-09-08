package br.net.mirante.singular.exemplos.ggtox.primariasimplificada.form;

import br.net.mirante.singular.form.SInfoType;
import br.net.mirante.singular.form.TypeBuilder;
import br.net.mirante.singular.form.persistence.STypePersistentComposite;
import br.net.mirante.singular.form.type.core.STypeString;

@SInfoType(name = "STypeJustificativaDevolucao", spackage = SPackagePeticaoPrimariaSimplificada.class)
public class STypeJustificativaDevolucao extends STypePersistentComposite {

    public final static String JUSTIFICATIVA = "justificativa";

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);

        final STypeString justificativa = addField(JUSTIFICATIVA, STypeString.class);

        justificativa.
                withTextAreaView();
        justificativa
                .asAtrBootstrap()
                .colPreference(12);
        justificativa.asAtr()
                .label("Justificativa")
                .required();
    }

}