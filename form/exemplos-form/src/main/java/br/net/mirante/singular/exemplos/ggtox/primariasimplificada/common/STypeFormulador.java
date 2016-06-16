package br.net.mirante.singular.exemplos.ggtox.primariasimplificada.common;


import br.net.mirante.singular.exemplos.ggtox.primariasimplificada.form.SPackagePPSCommon;
import br.net.mirante.singular.form.SInfoType;
import br.net.mirante.singular.form.TypeBuilder;
import br.net.mirante.singular.form.type.core.attachment.STypeAttachment;

@SInfoType(spackage = SPackagePPSCommon.class)
public class STypeFormulador extends STypeEntidade {

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);

        final STypeAttachment laudoLaboratorial = addField("laudoLaboratorial", STypeAttachment.class);

        laudoLaboratorial
                .asAtr()
                .label("Laudo laboratorial")
                .asAtrBootstrap()
                .colPreference(12);


        final STypeAttachment comprovanteRegistroEstado = addField("comprovanteRegistroEstado", STypeAttachment.class);

        comprovanteRegistroEstado
                .asAtr()
                .label("Comprovante de registro em orgão competente nessa modalidade do estado, Distrito Federal ou município")
                .asAtrBootstrap()
                .colPreference(12);
    }
}
