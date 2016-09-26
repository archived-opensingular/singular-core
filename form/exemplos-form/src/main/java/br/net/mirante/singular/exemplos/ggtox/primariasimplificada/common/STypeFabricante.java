package br.net.mirante.singular.exemplos.ggtox.primariasimplificada.common;


import br.net.mirante.singular.exemplos.ggtox.primariasimplificada.form.SPackagePPSCommon;
import br.net.mirante.singular.form.SInfoType;
import br.net.mirante.singular.form.TypeBuilder;
import br.net.mirante.singular.form.type.core.attachment.STypeAttachment;

import static br.net.mirante.singular.exemplos.ggtox.primariasimplificada.form.STypePeticaoPrimariaSimplificada.OBRIGATORIO;

@SInfoType(spackage = SPackagePPSCommon.class)
public class STypeFabricante extends STypeEntidade {

    public STypeAttachment comprovanteRegistroEstado;
    public STypeAttachment laudoLaboratorial;

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);

        withInitListener(si -> si.findNearest(tipoPessoa).ifPresent(x -> x.setValue("Jurídica")));
        withUpdateListener(si -> si.findNearest(tipoPessoa).ifPresent(x -> x.setValue("Jurídica")));

        tipoPessoa.asAtr().visible((x) -> false);

        comprovanteRegistroEstado = addField("comprovanteRegistroEstado", STypeAttachment.class);
        laudoLaboratorial = addField("laudoLaboratorial", STypeAttachment.class);

        nacional(comprovanteRegistroEstado)
                .asAtr()
                .required(OBRIGATORIO)
                .label("Comprovante de registro em orgão competente nessa modalidade do estado, Distrito Federal ou município")
                .asAtrBootstrap()
                .colPreference(12);

    }
}
