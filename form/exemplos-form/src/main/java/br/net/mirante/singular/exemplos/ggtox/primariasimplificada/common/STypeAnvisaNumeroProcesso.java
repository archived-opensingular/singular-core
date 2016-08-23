package br.net.mirante.singular.exemplos.ggtox.primariasimplificada.common;


import br.net.mirante.singular.exemplos.ggtox.primariasimplificada.form.SPackagePPSCommon;
import br.net.mirante.singular.form.SInfoType;
import br.net.mirante.singular.form.TypeBuilder;
import br.net.mirante.singular.form.type.core.STypeLong;

@SInfoType(spackage = SPackagePPSCommon.class)
public class STypeAnvisaNumeroProcesso extends STypeLong {

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);
    }
}
