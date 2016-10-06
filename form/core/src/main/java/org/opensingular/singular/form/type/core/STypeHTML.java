package org.opensingular.singular.form.type.core;

import org.opensingular.singular.form.SInfoType;
import org.opensingular.singular.form.STypeSimple;
import org.opensingular.singular.form.TypeBuilder;

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