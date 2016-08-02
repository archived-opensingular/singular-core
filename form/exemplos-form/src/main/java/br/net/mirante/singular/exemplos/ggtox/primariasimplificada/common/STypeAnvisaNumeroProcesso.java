package br.net.mirante.singular.exemplos.ggtox.primariasimplificada.common;


import br.net.mirante.singular.exemplos.ggtox.primariasimplificada.form.SPackagePPSCommon;
import br.net.mirante.singular.form.SInfoType;
import br.net.mirante.singular.form.TypeBuilder;
import br.net.mirante.singular.form.type.core.STypeInteger;
import br.net.mirante.singular.form.type.core.STypeString;

@SInfoType(spackage = SPackagePPSCommon.class)
public class STypeAnvisaNumeroProcesso extends STypeInteger {

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);
    }
}
