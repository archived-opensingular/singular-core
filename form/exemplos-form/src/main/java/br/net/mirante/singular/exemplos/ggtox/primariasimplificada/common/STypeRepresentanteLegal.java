package br.net.mirante.singular.exemplos.ggtox.primariasimplificada.common;


import br.net.mirante.singular.exemplos.ggtox.primariasimplificada.form.SPackagePPSCommon;
import br.net.mirante.singular.form.SInfoType;
import br.net.mirante.singular.form.TypeBuilder;

@SInfoType(spackage = SPackagePPSCommon.class)
public class STypeRepresentanteLegal extends STypeEntidade {

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);

        this
                .asAtrAnnotation()
                .setAnnotated();

        asAtr()
                .label("Representante Legal");

        tipoPessoa
                .asAtr()
                .exists(false);

        tipoPessoa.withInitListener(si -> si.setValue("Fisica"));
        tipoPessoa.withUpdateListener(si -> {
            System.out.println(si.getValue());
        });

    }

}