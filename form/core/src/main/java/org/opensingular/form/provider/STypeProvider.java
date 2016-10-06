package org.opensingular.form.provider;

import org.opensingular.form.SInstance;
import org.opensingular.form.SInfoType;
import org.opensingular.form.SType;

import java.io.Serializable;

@SInfoType(name = "STypeProvider", spackage = SPackageProvider.class)
public class STypeProvider<P extends Provider<T, SInstance>, T extends Serializable> extends SType<SIProvider<P, T>> {

    public STypeProvider() {
        super((Class) SIProvider.class);
    }


}