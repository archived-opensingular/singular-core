package br.net.mirante.singular.exemplos.ggtox.primariasimplificada.common;


import br.net.mirante.singular.exemplos.ggtox.primariasimplificada.form.SPackagePPSCommon;
import br.net.mirante.singular.form.SInfoType;
import br.net.mirante.singular.form.TypeBuilder;
import br.net.mirante.singular.form.type.core.STypeBoolean;

@SInfoType(spackage = SPackagePPSCommon.class)
public class STypeFabricanteConformeMatriz extends STypeBoolean {

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);

        asAtr()
                .label("Declaro que o(s) fabricante(s) está(ão) conforme a petição Matriz.");

        addInstanceValidator(validator -> {
            if (!(validator.getInstance().getValue() != null && validator.getInstance().getValue())) {
                validator.error("É obrigatório declarar que o(s) fabricante(s) está(ão) conforme a matriz.");
            }
        });
    }
}
