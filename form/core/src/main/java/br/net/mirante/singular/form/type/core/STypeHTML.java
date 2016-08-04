package br.net.mirante.singular.form.type.core;

import br.net.mirante.singular.form.SInfoType;
import br.net.mirante.singular.form.TypeBuilder;

@SInfoType(name = "HTML", spackage = SPackageCore.class)
public class STypeHTML extends STypeString {

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);
        asAtrBootstrap().colPreference(12);
    }
}