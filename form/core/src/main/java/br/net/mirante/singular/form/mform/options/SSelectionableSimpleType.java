/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.mform.options;

import java.util.Collection;

import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.document.SDocument;

@SuppressWarnings({"rawtypes", "unchecked"})
public interface SSelectionableSimpleType<BASE extends SType<?>, TIPO_NATIVO> extends SSelectionableType<BASE> {


    /**
     * Registers the name of the provider used to load options for this type.
     * This provider will be loaded from the {@link SDocument} attached to the
     * {@link SInstance} enclosing this type.
     *
     * @param providerName
     *            : Name of the {@link SOptionsProvider} to be used.
     * @return <code>this</code>
     */
    default public BASE withSelectionFromProvider(final String providerName) {
        setOptionsProvider(new LookupOptionsProvider(providerName));
        return (BASE) this;
    }

    /**
     * Registers the class of the provider used to load options for this type.
     * This provider will be loaded from the {@link SDocument} attached to the
     * {@link SInstance} enclosing this type.
     * 
     * @param providerClass
     *            : Class of the {@link SOptionsProvider} to be used.
     * @return <code>this</code>
     */
    default public BASE withSelectionFromProvider(Class<? extends SOptionsProvider> providerClass) {
        setOptionsProvider(new LookupOptionsProvider(providerClass));
        return (BASE) this;
    }

    default public BASE withSelectionFromProvider(SOptionsProvider provider) {
        setOptionsProvider(provider);
        return (BASE) this;
    }


    default public SFixedOptionsSimpleProvider withSelection() {
        SFixedOptionsSimpleProvider provider = new SFixedOptionsSimpleProvider((BASE) this, (Collection) null);
        setOptionsProvider(provider);
        return provider;
    }

    default public BASE withSelectionOf(TIPO_NATIVO... opcoes) {
        setOptionsProvider(new SFixedOptionsSimpleProvider((SType<?>) this, opcoes));
        return (BASE) this;
    }

    default public BASE withSelectionOf(Collection<TIPO_NATIVO> opcoes) {
        setOptionsProvider(new SFixedOptionsSimpleProvider((SType<?>) this, opcoes));
        return (BASE) this;
    }


}
