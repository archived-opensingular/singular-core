package org.opensingular.form.sample;

import org.opensingular.form.SInfoType;
import org.opensingular.form.TypeBuilder;
import org.opensingular.form.type.core.STypeString;

@SInfoType(spackage = FormTestPackage.class, newable = false, name = "PortoLocal")
public class StypeFooDataLocation extends STypeFoo {

    public STypeString localAtracacao;

    @Override
    protected void onLoadType(TypeBuilder tb) {

        localAtracacao = addFieldString("localAtracacao");
        localAtracacao.asAtr().label("Local de atracação");
        localAtracacao.asAtrBootstrap().colPreference(6);

    }
}
