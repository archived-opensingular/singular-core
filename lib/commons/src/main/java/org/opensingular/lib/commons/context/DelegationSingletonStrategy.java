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

package org.opensingular.lib.commons.context;


import javax.annotation.Nonnull;
import java.util.Map;
import java.util.function.Supplier;

/**
 * A singleton strategy thats delegates all call to other implementation, that is returned by {@link
 * #getStrategyImpl()}.
 *
 * @author Daniel C. Bordin on 2017-08-26
 */
public abstract class DelegationSingletonStrategy implements SingularSingletonStrategy {

    @Nonnull
    protected abstract SingularSingletonStrategy getStrategyImpl();

    @Override
    public final <T> void put(T thisInstance) {
        getStrategyImpl().put(thisInstance);
    }

    @Override
    public <T> void put(Class<? super T> instanceClazz, T thisInstance) {
        getStrategyImpl().put(instanceClazz, thisInstance);
    }

    @Override
    public <T> void put(String nameKey, T thisInstance) {
        getStrategyImpl().put(nameKey, thisInstance);
    }

    @Override
    public <T> boolean exists(Class<T> classKey) {
        return getStrategyImpl().exists(classKey);
    }

    @Override
    public boolean exists(String nameKey) {
        return getStrategyImpl().exists(nameKey);
    }

    @Override
    public <T> T get(Class<T> singletonClass) throws SingularSingletonNotFoundException {
        return getStrategyImpl().get(singletonClass);
    }

    @Override
    public <T> T get(String name) throws SingularSingletonNotFoundException {
        return getStrategyImpl().get(name);
    }

    @Override
    @Nonnull
    public <T> T singletonize(@Nonnull String nameKey, @Nonnull Supplier<T> singletonFactory) {
        return getStrategyImpl().singletonize(nameKey, singletonFactory);
    }


    @Override
    @Nonnull
    public <T> T singletonize(@Nonnull Class<T> classKey, @Nonnull Supplier<T> singletonFactory) {
        return getStrategyImpl().singletonize(classKey, singletonFactory);
    }

    @Override
    public Map<Object, Object> getEntries() {
        return getStrategyImpl().getEntries();
    }

    @Override
    public void putEntries(@Nonnull SingularSingletonStrategy source) {
        getStrategyImpl().putEntries(source);
    }

}
