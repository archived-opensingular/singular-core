package br.net.mirante.singular.form.mform.options;

import java.util.Collection;

import br.net.mirante.singular.form.mform.MTipo;

@SuppressWarnings({"rawtypes","unchecked"})
public interface MSelectionableType<BASE extends MTipo> {
    public MOptionsProvider getProviderOpcoes();
    public void setProviderOpcoes(MOptionsProvider p);
    
    default public boolean hasProviderOpcoes() {
        return getProviderOpcoes() != null;
    }
    
    default public MOptionsProvider selectionOf(Collection<MISelectItem> opcoes) {
        setProviderOpcoes(new MFixedOptionsSimpleProvider((BASE) this, opcoes));
        return getProviderOpcoes();
    }

    default public MOptionsProvider selectionOf(MISelectItem ... opcoes) {
        setProviderOpcoes(new MFixedOptionsSimpleProvider((BASE) this, opcoes));
        return getProviderOpcoes();
    }

    /**
     * Register a collection of options to be selected for this field.
     * Also restricts the range of values available for the field.
     * 
     * @param options Collection of values to be used.
     * @return <code>this</code>
     */
    default public BASE withSelectionOf(Collection<MISelectItem> options) {
        setProviderOpcoes(new MFixedOptionsSimpleProvider((MTipo<?>)this, options));
        return (BASE) this;
    }
    
    /**
     * Register a collection of options to be selected for this field.
     * Also restricts the range of values available for the field.
     * 
     * @param options Collection of values to be used.
     * @return <code>this</code>
     */
    default public BASE withSelectionOf(MISelectItem ... opcoes) {
        setProviderOpcoes(new MFixedOptionsSimpleProvider((BASE)this, opcoes));
        return (BASE) this;
    }
    
    /**
     * Registers the name of the provider used to load options for this type.
     * This provider will be loaded from the SDocument attached to the Minstance
     * enclosing this type.
     * 
     * @param providerName : Name of the {@link MOptionsProvider} to be used.
     * @return <code>this</code>
     */
    default public BASE withSelectionFromProvider(final String providerName) {
        setProviderOpcoes(new LookupOptionsProvider(providerName));
        return (BASE) this;
    }
    
    /**
     * Registers the class of the provider used to load options for this type.
     * This provider will be loaded from the SDocument attached to the Minstance
     * enclosing this type.
     * @param providerClass : Class of the {@link MOptionsProvider} to be used.
     * @return <code>this</code>
     */
    default public BASE withSelectionFromProvider(Class<? extends MOptionsProvider> providerClass) {
        setProviderOpcoes(new LookupOptionsProvider(providerClass));
        return (BASE) this;
    }
}
