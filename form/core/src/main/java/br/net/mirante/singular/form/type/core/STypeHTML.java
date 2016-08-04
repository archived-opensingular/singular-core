package br.net.mirante.singular.form.type.core;

import br.net.mirante.singular.form.SInfoType;
import br.net.mirante.singular.form.STypeSimple;
import br.net.mirante.singular.form.TypeBuilder;

@SInfoType(name = "HTML", spackage = SPackageCore.class)
public class STypeHTML extends STypeSimple<SIHTML, String> {

    public STypeHTML() {
        super(SIHTML.class, String.class);
    }

    protected STypeHTML(Class<? extends SIHTML> classeInstancia) {
        super(classeInstancia, String.class);
    }

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);
        asAtrBootstrap().colPreference(12);
    }

}