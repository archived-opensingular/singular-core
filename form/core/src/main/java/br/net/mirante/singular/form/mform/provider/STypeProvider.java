package br.net.mirante.singular.form.mform.provider;

import br.net.mirante.singular.form.mform.SInfoType;
import br.net.mirante.singular.form.mform.SType;

@SInfoType(name = "STypeProvider", spackage = SPackageProvider.class)
public class STypeProvider<P extends FilteredPagedProvider<T>, T> extends SType<SIProvider<P, T>> {

    public STypeProvider() {
        super((Class) SIProvider.class);
    }


}