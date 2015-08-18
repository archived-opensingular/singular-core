package br.net.mirante.singular.util.wicket.model;

import br.net.mirante.singular.util.wicket.lambda.ISupplier;

@SuppressWarnings("serial")
public class SupplierReloadableDetachableModel<T> extends ReloadableDetachableModel<T> {
    
    private final ISupplier<T> supplier;
    
    public SupplierReloadableDetachableModel(ISupplier<T> supplier) {
        this.supplier = supplier;
    }

    @Override
    protected T load() {
        return supplier.get();
    }

}
