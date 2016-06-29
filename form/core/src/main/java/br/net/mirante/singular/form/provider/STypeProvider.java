package br.net.mirante.singular.form.provider;

import br.net.mirante.singular.form.SInfoType;
import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.SType;

import java.io.Serializable;

@SInfoType(name = "STypeProvider", spackage = SPackageProvider.class)
public class STypeProvider<P extends Provider<T, SInstance>, T extends Serializable> extends SType<SIProvider<P, T>> {

    public STypeProvider() {
        super((Class) SIProvider.class);
    }


}