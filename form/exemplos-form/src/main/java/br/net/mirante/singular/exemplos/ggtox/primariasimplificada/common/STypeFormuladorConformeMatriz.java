package br.net.mirante.singular.exemplos.ggtox.primariasimplificada.common;


import br.net.mirante.singular.exemplos.ggtox.primariasimplificada.form.SPackagePPSCommon;
import br.net.mirante.singular.form.SInfoType;
import br.net.mirante.singular.form.TypeBuilder;
import br.net.mirante.singular.form.type.core.STypeBoolean;

@SInfoType(spackage = SPackagePPSCommon.class)
public class STypeFormuladorConformeMatriz extends STypeBoolean {

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);

        asAtr()
                .label("Declaro que o(s) formulador(es) estão conforme a petição Matriz.");

        addInstanceValidator(validator -> {
            if (!(validator.getInstance().getValue() != null && validator.getInstance().getValue())) {
                validator.error("É obrigatório declaracar que o(s) formulador(es) estão conforme a matriz.");
            }
        });

    }
}
