package org.opensingular.singular.form.converter;

import org.opensingular.singular.form.SInfoType;
import org.opensingular.singular.form.SType;
import org.opensingular.singular.form.provider.SPackageProvider;

@SInfoType(name = "STypeConverter", spackage = SPackageProvider.class)
public class STypeConverter extends SType<SIConverter> {

    public STypeConverter() {
        super(SIConverter.class);
    }

}