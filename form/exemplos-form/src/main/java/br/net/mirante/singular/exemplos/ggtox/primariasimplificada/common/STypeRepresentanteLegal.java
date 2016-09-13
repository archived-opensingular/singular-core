package br.net.mirante.singular.exemplos.ggtox.primariasimplificada.common;


import br.net.mirante.singular.exemplos.ggtox.primariasimplificada.form.SPackagePPSCommon;
import br.net.mirante.singular.form.SInfoType;
import br.net.mirante.singular.form.TypeBuilder;
import br.net.mirante.singular.form.type.core.attachment.STypeAttachment;

import static br.net.mirante.singular.exemplos.ggtox.primariasimplificada.form.STypePeticaoPrimariaSimplificada.OBRIGATORIO;

@SInfoType(spackage = SPackagePPSCommon.class)
public class STypeRepresentanteLegal extends STypeEntidade {

    public STypeAttachment comprovante;

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);

        tipoEntidade.asAtr().visible((x) -> false);
        tipoPessoa.asAtr().visible((x) -> false);
        cpf.asAtr().visible((x) -> false);

        withInitListener(si -> {
            si.setValue(STypeEntidade.TIPO_ENTIDADE, "Nacional");
            si.setValue(STypeEntidade.TIPO_PESSOA, "Física");
        });
        withUpdateListener(si -> {
            si.setValue(STypeEntidade.TIPO_ENTIDADE, "Nacional");
            si.setValue(STypeEntidade.TIPO_PESSOA, "Física");
        });

        comprovante = addField("comprovante", STypeAttachment.class);

        comprovante
                .asAtrBootstrap()
                .colPreference(12);

        comprovante
                .asAtr()
                .required(OBRIGATORIO)
                .label("Comprovante de representação");

        this
                .asAtrAnnotation()
                .setAnnotated();

        asAtr()
                .label("Representante Legal");


    }

}