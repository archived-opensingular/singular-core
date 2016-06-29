package br.net.mirante.singular.form.converter;

import br.net.mirante.singular.form.SInfoType;
import br.net.mirante.singular.form.SType;
import br.net.mirante.singular.form.provider.SPackageProvider;

@SInfoType(name = "STypeConverter", spackage = SPackageProvider.class)
public class STypeConverter extends SType<SIConverter> {

    public STypeConverter() {
        super(SIConverter.class);
    }

}