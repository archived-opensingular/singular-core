package org.opensingular.form.converter;

import org.opensingular.form.SInfoType;
import org.opensingular.form.SType;
import org.opensingular.form.provider.SPackageProvider;

@SInfoType(name = "STypeConverter", spackage = SPackageProvider.class)
public class STypeConverter extends SType<SIConverter> {

    public STypeConverter() {
        super(SIConverter.class);
    }

}