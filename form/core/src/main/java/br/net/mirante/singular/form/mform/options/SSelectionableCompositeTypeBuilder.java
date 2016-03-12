package br.net.mirante.singular.form.mform.options;

import br.net.mirante.singular.form.mform.SType;

@SuppressWarnings({"rawtypes", "unchecked"})
public class SSelectionableCompositeTypeBuilder<BASE extends SType & SSelectionableType> {


    private SSelectionableType<BASE> selectionable;

    SSelectionableCompositeTypeBuilder(BASE selectionable) {
        this.selectionable = selectionable;
    }

    public BASE fromProvider(SOptionsCompositeProvider provider) {
        selectionable.setOptionsProvider(provider);
        return (BASE) selectionable;
    }

    /**
     * Registers the name of the provider used to load options for this type.
     * This provider will be loaded from the SDocument attached to the Minstance
     * enclosing this type.
     *
     * @param providerName : Name of the {@link SOptionsProvider} to be used.
     * @return <code>this</code>
     */
    public BASE fromProvider(final String providerName) {
        selectionable.setOptionsProvider(new LookupOptionsProvider(providerName));
        return (BASE) selectionable;
    }

    /**
     * Registers the class of the provider used to load options for this type.
     * This provider will be loaded from the SDocument attached to the Minstance
     * enclosing this type.
     *
     * @param providerClass : Class of the {@link SOptionsProvider} to be used.
     * @return <code>this</code>
     */
    public BASE fromProvider(Class<? extends SOptionsProvider> providerClass) {
        selectionable.setOptionsProvider(new LookupOptionsProvider(providerClass));
        return (BASE) selectionable;
    }

    public BASE fromProvider(SOptionsProvider provider) {
        selectionable.setOptionsProvider(provider);
        return (BASE) selectionable;
    }

}
