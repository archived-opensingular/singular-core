package br.net.mirante.singular.form.mform.options;

import br.net.mirante.singular.form.mform.SType;

import java.util.Collection;

@SuppressWarnings({"rawtypes", "unchecked"})
public interface MSelectionableSimpleType<BASE extends SType, TIPO_NATIVO> extends MSelectionableType<BASE> {


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

    default public BASE withSelectionFromProvider(MOptionsProvider provider) {
        setProviderOpcoes(provider);
        return (BASE) this;
    }


    default public MFixedOptionsSimpleProvider withSelection() {
        MFixedOptionsSimpleProvider provider = new MFixedOptionsSimpleProvider((BASE) this, (Collection) null);
        setProviderOpcoes(provider);
        return (MFixedOptionsSimpleProvider) provider;
    }

    default public <T extends SType<?>> T withSelectionOf(TIPO_NATIVO... opcoes) {
        setProviderOpcoes(new MFixedOptionsSimpleProvider((SType<?>) this, opcoes));
        return (T) this;
    }

    default public <T extends SType<?>> T withSelectionOf(Collection<TIPO_NATIVO> opcoes) {
        setProviderOpcoes(new MFixedOptionsSimpleProvider((SType<?>) this, opcoes));
        return (T) this;
    }


}
