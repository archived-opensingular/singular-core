/*
 *
 *  * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.opensingular.form.provider;

import org.opensingular.form.SIComposite;
import org.opensingular.form.SInstance;
import org.opensingular.form.util.transformer.SCompositeListBuilder;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;

/**
 * Utility class to build a simple list of itens for a selection provider
 */
public class SSimpleProviderListBuilder {

    private final SCompositeListBuilder sCompositeListBuilder;

    SSimpleProviderListBuilder(SCompositeListBuilder sCompositeListBuilder) {
        this.sCompositeListBuilder = sCompositeListBuilder;
    }

    /**
     * Dynamically add a new Selection Element
     * @return
     *  Value setter utilitiy class to configure element fields
     */
    public SCompositeListBuilder.SCompositeValueSetter add() {
        return sCompositeListBuilder.add();
    }

    /**
     * Appends a copy of the provided {@param sInstance} to the list of selection elements
     * @param sInstance
     * @return
     *  return this object to allow chaining of additions
     */
    public SSimpleProviderListBuilder add(SInstance sInstance) {
        add().get().setValue(sInstance);
        return this;
    }


    /**
     * Appends a copy of every SInstance at the provided collection {@param instances} to the list of selection elements
     * @param instances
     * @return
     *
     */
    public void addAll(Collection<SInstance> instances) {
        instances.forEach(i -> add().get().setValue(i));
    }

    /**
     * Returns the target SInstance of  selection
     * @return
     */
    public SInstance getCurrentInstance() {
        return sCompositeListBuilder.getCurrentInstance();
    }

    List<SIComposite> getList() {
        return sCompositeListBuilder.getList();
    }

    /**
     * Lookup for a service for the given class and throws an exception if it is not found.
     * @param targetClass
     * @param <T>
     * @return
     */
    @Nonnull
    public <T> T lookupServiceOrException(@Nonnull Class<T> targetClass) {
        return sCompositeListBuilder.lookupServiceOrException(targetClass);
    }

}
