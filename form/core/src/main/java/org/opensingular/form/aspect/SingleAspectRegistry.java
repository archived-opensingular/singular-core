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

import org.opensingular.form.InternalAccess;
import org.opensingular.form.SInstance;
import org.opensingular.form.SType;
import org.opensingular.form.SingularFormException;
import org.opensingular.lib.commons.lambda.ISupplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Maps the specific implementations of a aspect for different {@link SType}. Also provides methods to find
 * the best match implementation through the methods {@link #findAspect(SInstance)}
 * and {@link #findAspect(SType)}.
 *
 * @author Daniel C. Bordin on 09/08/2017
 */
public class SingleAspectRegistry<T, QUALIFIER> {

    private final AspectRef<T> aspectRef;

    private final QualifierStrategy qualifierStrategy;

    private final Map<Class<? extends SType>, List<AspectEntry<T, QUALIFIER>>> registry = new HashMap<>();

    private Integer index;

    public SingleAspectRegistry(@Nonnull AspectRef<T> aspectRef) {
        this.aspectRef = Objects.requireNonNull(aspectRef);
        this.qualifierStrategy = aspectRef.getQualifierStrategy();
    }

    /**
     * Registers for a {@link SType} a supplier of the implementation of the aspect with the default {@link
     * AspectEntry#DEFAULT_ENTRY_PRIORITY} and without a qualifier.
     */
    @Nonnull
    public SingleAspectRegistry<T, QUALIFIER> add(@Nonnull Class<? extends SType> type, @Nonnull ISupplier<T> factory) {
        return add(type, null, factory);
    }

    /**
     * Registers for a {@link SType} a instantiated implementation of the aspect with the default {@link
     * AspectEntry#DEFAULT_ENTRY_PRIORITY} and without a qualifier.
     */
    public SingleAspectRegistry<T, QUALIFIER> addFixImplementation(@Nonnull Class<? extends SType> type,
            @Nonnull T implementation) {
        return add(type, () -> implementation);
    }

    /**
     * Registers for a {@link SType} a supplier of the implementation of the aspect with the default {@link
     * AspectEntry#DEFAULT_ENTRY_PRIORITY}. Optionally, it may be informed a
     * qualifier object to differentiate this entry from other for the same {@link SType}.
     */
    @Nonnull
    public SingleAspectRegistry<T, QUALIFIER> add(@Nonnull Class<? extends SType> type, @Nullable QUALIFIER qualifier,
            @Nonnull Supplier<T> factory) {
        return add(type, qualifier, factory, AspectEntry.DEFAULT_ENTRY_PRIORITY);
    }

    /**
     * Registers  for a {@link SType} a supplier of the implementation of the aspect. Optionally, it may be informed a
     * qualifier object to differentiate this entry from other for the same {@link SType}.
     */
    @Nonnull
    public SingleAspectRegistry<T, QUALIFIER> add(@Nonnull Class<? extends SType> type, @Nullable QUALIFIER qualifier,
            @Nonnull Supplier<T> factory, int entryPriority) {
        Objects.requireNonNull(factory);
        List<AspectEntry<T, QUALIFIER>> list = registry.get(Objects.requireNonNull(type));
        if (list == null) {
            list = new ArrayList<>(1);
            registry.put(type, list);
        }
        list.add(new AspectEntry<>(qualifier, factory, entryPriority));
        return this;
    }

    /**
     * Try to find the implementation of the aspect target of this registry for a {@link SInstance}.
     * See the how the search is done at {@link AspectRef}.
     */
    @Nonnull
    public Optional<T> findAspect(@Nonnull SInstance instance) {
        return findAspect(instance.getType(), qualifierStrategy.getMatcherFor(instance.getType()));
    }

    /**
     * Try to find the implementation of the aspect target of this registry for a {@link SType}.
     * See the how the search is done at {@link AspectRef}.
     */
    @Nonnull
    public Optional<T> findAspect(@Nonnull SType<?> type) {
        return findAspect(type, qualifierStrategy.getMatcherFor(type));
    }

    private Optional<T> findAspect(@Nonnull SType<?> type, @Nonnull QualifierMatcher matcher) {
        T result = findAspectOnTypeTree(type, matcher);
        if (result == null && !matcher.isAny()) {
            result = findAspectOnTypeTree(type, QualifierMatcher.ANY);
        }
        return Optional.ofNullable(result);
    }

    @Nullable
    private T findAspectOnTypeTree(@Nonnull SType<?> type, @Nonnull QualifierMatcher matcher) {
        for (SType<?> current = type; current != null; current = current.getSuperType()) {
            Object result = InternalAccess.INTERNAL.getAspectDirect(current, getIndex());
            if (result == null && !isNextSuperTypeOfTheSameClass(current)) {
                result = lookupOnMap(current, matcher);
            }
            if (result != null) {
                if (!aspectRef.getAspectClass().isInstance(result)) {
                    throw new SingularFormException(
                            "Was expected to find a object of " + aspectRef.getAspectClass().getName() +
                                    " but was found a object of the class " + result.getClass().getName());
                }
                return aspectRef.getAspectClass().cast(result);
            }
        }
        return null;
    }

    @Nullable
    private T lookupOnMap(@Nonnull SType<?> current, @Nonnull QualifierMatcher matcher) {
        List<AspectEntry<T, QUALIFIER>> list = registry.get(current.getClass());
        if (list == null) {
            return null;
        }
        AspectEntry<T, QUALIFIER> currentEntry = null;
        for (AspectEntry<T, QUALIFIER> entry : list) {
            if (matcher.isMatch(entry)) {
                currentEntry = selectBestMatch(matcher, currentEntry, entry);
            }
        }
        return currentEntry == null ? null : currentEntry.getFactory().get();
    }

    private boolean isNextSuperTypeOfTheSameClass(SType<?> type) {
        return type.getSuperType() != null && type.getClass() == type.getSuperType().getClass();
    }

    private AspectEntry<T, QUALIFIER> selectBestMatch(QualifierMatcher matcher, AspectEntry<T, QUALIFIER> currentResult,
            AspectEntry<T, QUALIFIER> newEntry) {
        if (currentResult == null) {
            return newEntry;
        }
        int relevancy = matcher.compare(currentResult, newEntry);
        if (relevancy > 0 || (relevancy == 0 && newEntry.getPriority() > currentResult.getPriority())) {
            return newEntry;
        }
        return currentResult;
    }

    final void setIndex(Integer index) {
        this.index = index;
    }

    /**
     * Return the index (zero or positive number) of this registry inside its {@link MasterAspectRegistry}. This number
     * is unique inside the same
     * {@link MasterAspectRegistry}.
     */
    public Integer getIndex() {
        return index;
    }
}
