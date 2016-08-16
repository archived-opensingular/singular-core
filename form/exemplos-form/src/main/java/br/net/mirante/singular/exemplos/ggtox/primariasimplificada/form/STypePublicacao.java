package br.net.mirante.singular.exemplos.ggtox.primariasimplificada.form;

import br.net.mirante.singular.form.SInfoType;
import br.net.mirante.singular.form.TypeBuilder;
import br.net.mirante.singular.form.persistence.STypePersistentComposite;
import br.net.mirante.singular.form.type.core.STypeDate;
import br.net.mirante.singular.form.type.core.STypeInteger;
import br.net.mirante.singular.form.view.SViewByBlock;

@SInfoType(name = "STypePublicacao", spackage = SPackagePeticaoPrimariaSimplificada.class)
public class STypePublicacao extends STypePersistentComposite {

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);
        final STypeDate    dataDOU   = addField("dataDOU", STypeDate.class);
        final STypeInteger numeroDOU = addField("numeroDOU", STypeInteger.class);

        dataDOU
                .asAtrBootstrap()
                .colPreference(3)
                .asAtr()
                .label("Data do DOU")
                .required();
        numeroDOU
                .asAtrBootstrap()
                .colPreference(3)
                .asAtr()
                .label("Número do DOU")
                .required();


        withView(new SViewByBlock(), viewByBlocks -> {
            viewByBlocks.newBlock("Dados da publicação")
                    .add(dataDOU)
                    .add(numeroDOU);
        });
    }

}
