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

    /** Implements the logic for choosing between different implementations that may apply to specific case. */
    private final QualifierStrategy<QUALIFIER> qualifierStrategy;

    private final Map<Class<? extends SType>, List<AspectEntry<T, QUALIFIER>>> registry = new HashMap<>();

    private Integer index;

    /** Creates a new registry for the indicated aspect and without a qualifier strategy. */
    public SingleAspectRegistry(@Nonnull AspectRef<T> aspectRef) {
        this(aspectRef, null);
    }

    /** Creates a new registry for the indicated aspect and with a qualifier strategy. */
    public SingleAspectRegistry(@Nonnull AspectRef<T> aspectRef,
            @Nullable QualifierStrategy<QUALIFIER> qualifierStrategy) {
        this.aspectRef = Objects.requireNonNull(aspectRef);
        this.qualifierStrategy = qualifierStrategy;
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
        return addFixImplementation(type, null, implementation);
    }

    /**
     * Registers for a {@link SType} and specific qualifier, a instantiated implementation of the aspect with the
     * default {@link AspectEntry#DEFAULT_ENTRY_PRIORITY}.
     */
    public SingleAspectRegistry<T, QUALIFIER> addFixImplementation(@Nonnull Class<? extends SType> type,
            @Nullable QUALIFIER qualifier, @Nonnull T implementation) {
        return add(type, qualifier, () -> implementation);
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
        return findAspect(instance.getType(),
                qualifierStrategy == null ? null : qualifierStrategy.getMatcherFor(instance));
    }

    /**
     * Try to find the implementation of the aspect target of this registry for a {@link SType}.
     * See the how the search is done at {@link AspectRef}.
     */
    @Nonnull
    public Optional<T> findAspect(@Nonnull SType<?> type) {
        return findAspect(type, qualifierStrategy == null ? null : qualifierStrategy.getMatcherFor(type));
    }

    private Optional<T> findAspect(@Nonnull SType<?> type, @Nullable QualifierMatcher<QUALIFIER> matcher) {
        Objects.requireNonNull(type);
        T result = findAspectOnTypeTree(type, matcher != null ? matcher : QualifierMatcher.nullMatcher());
        return Optional.ofNullable(result);
    }

    @Nullable
    private T findAspectOnTypeTree(@Nonnull SType<?> type, @Nonnull QualifierMatcher<QUALIFIER> matcher) {
        AspectEntry<T, QUALIFIER> currentEntry = null;
        for (SType<?> currentType = type; currentType != null; currentType = currentType.getSuperType()) {
            AspectEntry<?, ?> entry = InternalAccess.INTERNAL.getAspectDirect(currentType, getIndex());
            if (entry != null) {
                return safeConvert(entry);
            } else if (isNextSuperTypeOfTheSameClass(currentType)) {
                continue;
            }
            currentEntry = lookupOnMap(currentType, matcher, currentEntry);
            if (currentEntry != null && matcher.isTheBestPossibleMatch(currentEntry)) {
                return safeConvert(currentEntry);
            }
        }
        return safeConvert(currentEntry);
    }

    @Nullable
    private T safeConvert(@Nullable AspectEntry<?, ?> entry) {
        if (entry == null) {
            return null;
        }
        Object value = entry.getFactory().get();
        if (value != null) {
            if (!aspectRef.getAspectClass().isInstance(value)) {
                throw new SingularFormException(
                        "Was expected to find a object of " + aspectRef.getAspectClass().getName() +
                                " but was found a object of the class " + value.getClass().getName());
            }
            return aspectRef.getAspectClass().cast(value);
        }
        return null;
    }

    @Nullable
    private AspectEntry<T, QUALIFIER> lookupOnMap(@Nonnull SType<?> type, @Nonnull QualifierMatcher<QUALIFIER> matcher,
            @Nullable AspectEntry<T, QUALIFIER> currentResult) {
        AspectEntry<T, QUALIFIER> currentEntry = currentResult;
        List<AspectEntry<T, QUALIFIER>> list = registry.get(type.getClass());
        if (list != null) {
            for (AspectEntry<T, QUALIFIER> entry : list) {
                if (matcher.isMatch(entry)) {
                    currentEntry = selectBestMatch(matcher, currentEntry, entry);
                }
            }
        }
        return currentEntry;
    }

    private boolean isNextSuperTypeOfTheSameClass(SType<?> type) {
        return type.getSuperType() != null && type.getClass() == type.getSuperType().getClass();
    }

    private AspectEntry<T, QUALIFIER> selectBestMatch(QualifierMatcher<QUALIFIER> matcher,
            @Nullable AspectEntry<T, QUALIFIER> currentResult, @Nonnull AspectEntry<T, QUALIFIER> newEntry) {
        if (currentResult == null) {
            return newEntry;
        }
        int relevancy;
        if (currentResult.getQualifier() == null) {
            relevancy = newEntry.getQualifier() == null ? 0 : 1;
        } else if (newEntry.getQualifier() == null) {
            relevancy = currentResult.getQualifier() == null ? 0 : -1;
        } else {
            relevancy = matcher.compare(currentResult, newEntry);
            if (relevancy == 0) {
                relevancy = newEntry.getPriority() - currentResult.getPriority();
            }
        }
        if (relevancy > 0) {
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
