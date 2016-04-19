package br.net.mirante.singular.form.mform.converter;

import br.net.mirante.singular.form.mform.SInfoType;
import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.provider.SPackageProvider;

@SInfoType(name = "STypeConverter", spackage = SPackageProvider.class)
public class STypeConverter extends SType<SIConverter> {

    public STypeConverter() {
        super(SIConverter.class);
    }

}