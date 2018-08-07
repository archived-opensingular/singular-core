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

package org.opensingular.form.aspect;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Supplier;

/**
 * Represents a single registration of a {@link AspectRef} implementation. Usually it is the entries create in a
 * {@link SingleAspectRegistry}.
 *
 * @author Daniel C. Bordin on 11/08/2017.
 */
public final class AspectEntry<T, QUALIFIER> {

    /** It's the default assigned priority attributed to a entry. */
    public static final int DEFAULT_ENTRY_PRIORITY = 100;

    private final QUALIFIER qualifier;
    private final Supplier<T> factory;
    private final int priority;

    /** Creates a entry with the default priority ({@link #DEFAULT_ENTRY_PRIORITY}).*/
    public AspectEntry(@Nullable QUALIFIER qualifier, @Nonnull Supplier<T> factory) {
        this(qualifier, factory, DEFAULT_ENTRY_PRIORITY);
    }

    public AspectEntry(@Nullable QUALIFIER qualifier, @Nonnull Supplier<T> factory, int priority) {
        this.qualifier = qualifier;
        this.factory = factory;
        this.priority = priority;
    }

    /**
     * Returns, if available, a additional information that differentiates this entry from another entry that may be
     * applied to the same target.
     * <p>For example, if a particular {@link AspectRef} want to have different implementation
     * based on the background of the type, for the same {@link org.opensingular.form.SType} may be two entries, one
     * for
     * when the type has a blue background and another when the background is null. In this case, the first entry would
     * have {@link #getQualifier()} to return a object representing the blue color.</p>
     * <p>This information with be used by the {@link QualifierMatcher#isSecondMoreRelevant(AspectEntry, AspectEntry)}
     * and {@link QualifierMatcher#isMatch(AspectEntry)} for the actual selection.</p>
     */
    @Nullable
    public QUALIFIER getQualifier() {
        return qualifier;
    }

    /** Returns the factory that produces the implementation of the aspect to witch this entry refers. */
    @Nonnull
    public Supplier<T> getFactory() {
        return factory;
    }

    /**
     * It's a number the indicates the priority of this particular entry (implementation of the aspect). A entry with a
     * higher priority must be the select options if there aren't another factors to be considered (i.e. if {@link
     * QualifierMatcher#compare(AspectEntry, AspectEntry)} return 0).
     *
     * @see AspectEntry#DEFAULT_ENTRY_PRIORITY
     * @see QualifierMatcher
     */
    public int getPriority() {
        return priority;
    }
}
