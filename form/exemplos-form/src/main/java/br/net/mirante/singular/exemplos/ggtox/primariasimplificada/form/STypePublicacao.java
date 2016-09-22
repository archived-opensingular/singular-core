package br.net.mirante.singular.exemplos.ggtox.primariasimplificada.form;

import br.net.mirante.singular.form.SInfoType;
import br.net.mirante.singular.form.TypeBuilder;
import br.net.mirante.singular.form.persistence.STypePersistentComposite;
import br.net.mirante.singular.form.type.core.STypeDate;
import br.net.mirante.singular.form.type.core.STypeInteger;
import br.net.mirante.singular.form.type.core.STypeString;
import br.net.mirante.singular.form.view.SViewByBlock;

@SInfoType(name = "STypePublicacao", spackage = SPackagePeticaoPrimariaSimplificada.class)
public class STypePublicacao extends STypePersistentComposite {

    public static final String DATA_DOU = "dataDOU";
    public static final String NUMERO_RE = "numeroRE";
    public static final String DATA_RE = "dataRE";
    public static final String NUMERO_DOU = "numeroDOU";

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);

        final STypeDate    dataDOU   = addField(DATA_DOU, STypeDate.class);
        final STypeInteger numeroDOU = addField(NUMERO_DOU, STypeInteger.class);
        final STypeDate    dataRE    = addField(DATA_RE, STypeDate.class);
        final STypeString  numeroRE  = addField(NUMERO_RE, STypeString.class);

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

        dataRE
                .asAtrBootstrap()
                .colPreference(3)
                .asAtr()
                .label("Data do RE")
                .required();

        numeroRE
                .asAtrBootstrap()
                .colPreference(3)
                .asAtr()
                .label("Número RE")
                .required();

        withView(new SViewByBlock(), viewByBlocks -> {
            viewByBlocks.newBlock("Dados da publicação")
                    .add(dataDOU)
                    .add(numeroDOU)
                    .add(dataRE)
                    .add(numeroRE);
        });
    }

}
