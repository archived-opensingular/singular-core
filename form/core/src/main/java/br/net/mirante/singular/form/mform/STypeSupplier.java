package br.net.mirante.singular.form.mform;

import java.util.function.Supplier;

import br.net.mirante.singular.form.mform.basic.ui.SPackageBasic;

@SInfoType(name = "MTipoSupplier", spackage = SPackageBasic.class)
public class STypeSupplier<V> extends STypeCode<SISupplier<V>, Supplier<V>> {

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public STypeSupplier() {
        super((Class) SISupplier.class, (Class) Supplier.class);
    }
}
