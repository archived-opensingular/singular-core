package br.net.mirante.singular.form.mform;

import java.util.function.Supplier;

import br.net.mirante.singular.form.mform.basic.ui.MPacoteBasic;

@MInfoTipo(nome = "MTipoSupplier", pacote = MPacoteBasic.class)
public class MTipoSupplier<V> extends MTipoCode<MISupplier<V>, Supplier<V>> {

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public MTipoSupplier() {
        super((Class) MISupplier.class, (Class) Supplier.class);
    }
}
