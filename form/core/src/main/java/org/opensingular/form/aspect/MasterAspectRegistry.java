/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
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

package org.opensingular.form.aspect;

import org.opensingular.form.SInstance;
import org.opensingular.form.SType;
import org.opensingular.form.SingularFormException;
import org.opensingular.internal.form.util.ArrUtil;

import javax.annotation.Nonnull;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * It's a map of {@link SingleAspectRegistry}, one for each the class of the aspect ({@link AspectRef}) being used into
 * a {@link org.opensingular.form.SDictionary}. It promotes the reuse of aspects default registrations.
 *
 * @author Daniel C. Bordin on 10/08/2017.
 */
public class MasterAspectRegistry {

    private final Map<Class<?>, Integer> aspectIndexes = new HashMap<>();

    private SingleAspectRegistry<?, ?>[] registries;

    /**
     * Finds the correspondent {@link SingleAspectRegistry} and calls
     * {@link SingleAspectRegistry#findAspect(SInstance)}. The {@link SingleAspectRegistry} will be dynamically loaded
     * if not already loaded.
     *
     * @see AspectRef
     */
    @Nonnull
    public <T> Optional<T> getAspect(@Nonnull SInstance instance, @Nonnull AspectRef<T> aspectRef) {
        return getAspectRegistry(aspectRef).findAspect(instance);
    }

    /**
     * Finds the correspondent {@link SingleAspectRegistry} and calls
     * {@link SingleAspectRegistry#findAspect(SType)}. The {@link SingleAspectRegistry} will be dynamically loaded
     * if not already loaded.
     *
     * @see AspectRef
     */
    @Nonnull
    public <T> Optional<T> getAspect(@Nonnull SType<?> type, @Nonnull AspectRef<T> aspectRef) {
        return getAspectRegistry(aspectRef).findAspect(type);
    }

    @Nonnull
    private <T> SingleAspectRegistry<T, ?> getAspectRegistry(@Nonnull AspectRef<T> aspectRef) {
        Integer index = getIndex(aspectRef);
        SingleAspectRegistry<?, ?> result = ArrUtil.arrayGet(registries, index);
        if (result == null) {
            Class<? extends SingleAspectRegistry<T, ?>> registryClass = aspectRef.getRegistryClass();
            if (registryClass == null) {
                result = new SingleAspectRegistry<T, Object>(aspectRef);
            } else {
                try {
                    Constructor<? extends SingleAspectRegistry<T, ?>> constructor = registryClass.getConstructor(
                            AspectRef.class);
                    result = constructor.newInstance(aspectRef);
                } catch (NoSuchMethodException e) {
                    throw new SingularFormException(
                            "The class " + registryClass.getName() + " doesn't implements a constructor " +
                                    registryClass.getSimpleName() + "(" + AspectRef.class.getSimpleName() + ")", e);
                } catch (Exception e) {
                    throw new SingularFormException("Erro calling constructor " + registryClass.getName() + "(" +
                            AspectRef.class.getSimpleName() + ")", e);
                }
            }
            result.setIndex(index);
            registries = ArrUtil.arraySet(registries, index, result, SingleAspectRegistry.class, 8);
        }
        return (SingleAspectRegistry<T, ?>) result;
    }

    /**
     * Returns the index of the aspect inside this registry. It's a unique number inside the same {@link
     * MasterAspectRegistry}.
     */
    @Nonnull
    public Integer getIndex(@Nonnull AspectRef<?> aspectRef) {
        Integer index = aspectIndexes.get(aspectRef.getAspectClass());
        if (index == null) {
            index = Integer.valueOf(aspectIndexes.size());
            aspectIndexes.put(aspectRef.getAspectClass(), index);
        }
        return index;
    }
}
