package br.net.mirante.singular.exemplos.ggtox.primariasimplificada.common;


import br.net.mirante.singular.exemplos.ggtox.primariasimplificada.form.SPackagePPSCommon;
import br.net.mirante.singular.form.SInfoType;
import br.net.mirante.singular.form.TypeBuilder;
import br.net.mirante.singular.form.type.core.attachment.STypeAttachment;

@SInfoType(spackage = SPackagePPSCommon.class)
public class STypeFormulador extends STypeEntidade {


    public STypeAttachment comprovanteRegistroEstado;
    public STypeAttachment laudoLaboratorial;

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);

        tipoPessoa
                .asAtr()
                .visible(false);

        withInitListener(si -> si.findNearest(tipoPessoa)
                .get()
                .setValue("Juridica")
        );

        cnpj
                .asAtr()
                .visible(si -> true);

        laudoLaboratorial = addField("laudoLaboratorial", STypeAttachment.class);

        laudoLaboratorial
                .asAtr()
                .label("Laudo laboratorial")
                .asAtrBootstrap()
                .colPreference(12);


        comprovanteRegistroEstado = addField("comprovanteRegistroEstado", STypeAttachment.class);

        comprovanteRegistroEstado
                .asAtr()
                .label("Comprovante de registro em orgão competente nessa modalidade do estado, Distrito Federal ou município")
                .asAtrBootstrap()
                .colPreference(12);
    }
}
