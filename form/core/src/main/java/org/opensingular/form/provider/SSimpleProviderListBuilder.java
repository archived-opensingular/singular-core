/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.form.provider;

import org.opensingular.form.SIComposite;
import org.opensingular.form.SInstance;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.util.transformer.SCompositeListBuilder;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Utility class to build a simple list of itens for a selection provider
 */
public class SSimpleProviderListBuilder {

    private final SCompositeListBuilder sCompositeListBuilder;

    SSimpleProviderListBuilder(@Nonnull SCompositeListBuilder sCompositeListBuilder) {
        this.sCompositeListBuilder = Objects.requireNonNull(sCompositeListBuilder);
    }

    /** Returns the type os elements in the list. */
    @Nonnull
    public STypeComposite<SIComposite> getItemType() {
        return sCompositeListBuilder.getItemType();
    }

    /**
     * Dynamically add a new Selection Element
     * @return Value setter utility class to configure element fields
     */
    @Nonnull
    public SCompositeListBuilder.SCompositeValueSetter add() {
        return sCompositeListBuilder.add();
    }

    /**
     * Appends a copy of the provided {@param sInstance} to the list of selection elements
     * @return
     *  return this object to allow chaining of additions
     */
    @Nonnull
    public SSimpleProviderListBuilder add(SInstance sInstance) {
        add().get().setValue(sInstance);
        return this;
    }


    /**
     * Appends a copy of every SInstance at the provided collection {@param instances} to the list of selection elements
     */
    public void addAll(@Nonnull Collection<? extends SInstance> instances) {
        instances.forEach(i -> add().get().setValue(i));
    }

    /**
     * Returns the target SInstance of  selection
     */
    public SInstance getCurrentInstance() {
        return sCompositeListBuilder.getCurrentInstance();
    }

    @Nonnull
    List<SIComposite> getList() {
        return sCompositeListBuilder.getList();
    }

    /**
     * Lookup for a service for the given class and throws an exception if it is not found.
     */
    @Nonnull
    public <T> T lookupServiceOrException(@Nonnull Class<T> targetClass) {
        return sCompositeListBuilder.lookupServiceOrException(targetClass);
    }

}
