package br.net.mirante.singular.form.mform.options;

import br.net.mirante.singular.form.mform.MTipo;

@SuppressWarnings({"rawtypes", "unchecked"})
public class MSelectionableCompositeTypeBuilder<BASE extends MTipo & MSelectionableType> {


    private MSelectionableType<BASE> selectionable;

    MSelectionableCompositeTypeBuilder(BASE selectionable) {
        this.selectionable = selectionable;
    }

    public BASE fromProvider(MOptionsCompositeProvider provider) {
        selectionable.setProviderOpcoes(provider);
        return (BASE) selectionable;
    }

    /**
     * Registers the name of the provider used to load options for this type.
     * This provider will be loaded from the SDocument attached to the Minstance
     * enclosing this type.
     *
     * @param providerName : Name of the {@link MOptionsProvider} to be used.
     * @return <code>this</code>
     */
    public BASE fromProvider(final String providerName) {
        selectionable.setProviderOpcoes(new LookupOptionsProvider(providerName));
        return (BASE) selectionable;
    }

    /**
     * Registers the class of the provider used to load options for this type.
     * This provider will be loaded from the SDocument attached to the Minstance
     * enclosing this type.
     *
     * @param providerClass : Class of the {@link MOptionsProvider} to be used.
     * @return <code>this</code>
     */
    public BASE fromProvider(Class<? extends MOptionsProvider> providerClass) {
        selectionable.setProviderOpcoes(new LookupOptionsProvider(providerClass));
        return (BASE) selectionable;
    }

    public BASE fromProvider(MOptionsProvider provider) {
        selectionable.setProviderOpcoes(provider);
        return (BASE) selectionable;
    }

}
