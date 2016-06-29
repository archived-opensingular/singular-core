package br.net.mirante.singular.exemplos.ggtox.primariasimplificada.common;


import br.net.mirante.singular.exemplos.ggtox.primariasimplificada.form.SPackagePPSCommon;
import br.net.mirante.singular.form.SInfoType;
import br.net.mirante.singular.form.TypeBuilder;
import br.net.mirante.singular.form.type.core.attachment.STypeAttachment;

@SInfoType(spackage = SPackagePPSCommon.class)
public class STypeFabricante extends STypeEntidade {

    public STypeAttachment comprovanteRegistroEstado;

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);

        tipoPessoa
                .asAtr()
                .exists(false);

        withInitListener(si -> si.findNearest(tipoPessoa)
                        .get()
                        .setValue("Juridica")
        );

        withUpdateListener(si -> si.findNearest(tipoPessoa)
                        .get()
                        .setValue("Juridica")
        );

        cnpj
                .asAtr()
                .exists( si -> true);


        comprovanteRegistroEstado = addField("comprovanteRegistroEstado", STypeAttachment.class);

        comprovanteRegistroEstado
                .asAtr()
                .label("Comprovante de registro em orgão competente nessa modalidade do estado, Distrito Federal ou município")
                .asAtrBootstrap()
                .colPreference(12);
    }
}
