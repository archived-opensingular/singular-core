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

import javax.annotation.Nonnull;
import java.util.Comparator;

/**
 * Filters the implementations of a aspect. It also used to select the best option when there is more the one
 * implementation available for the same target.
 * <p>When a {@link AspectRef} wants to have different implementations for the same {@link org.opensingular.form.SType}
 * or {@link org.opensingular.form.SInstance} based in a particular characteristic, then the matcher should consider the
 * information {@link AspectEntry#getQualifier()}. </p>
 *
 * @author Daniel C. Bordin on 10/08/2017.
 * @see AspectEntry#getQualifier()
 */
public interface QualifierMatcher extends Comparator<AspectEntry<?, ?>> {

    /** A dummy matcher that answer true for any aspect entry. */
    @Nonnull
    public static final QualifierMatcher ANY = new QualifierMatcher() {

        @Override
        public boolean isMatch(@Nonnull AspectEntry<?, ?> aspectEntry) {
            return true;
        }

        @Override
        public boolean isAny() {
            return true;
        }
    };

    /**
     * Verifies if the {@link org.opensingular.form.aspect.AspectEntry} is valid selection.
     *
     * @see AspectEntry#getQualifier()
     */
    boolean isMatch(@Nonnull AspectEntry<?, ?> aspectEntry);

    /** Indicates that this matcher is dummy filter that answer true for everything. */
    default boolean isAny() {
        return false;
    }

    /**
     * Verify between the two entries witch is a better match. If result is:
     * <ul>
     * <li>negative, than the first one is more relevant</li>
     * <li>zero, than both are equivalent</li>
     * <li>positive, than the second one is more relevant</li>
     * </ul>
     * <p>Usually this method should consider the {@link
     * AspectEntry#getQualifier()}. By default, this methods considers the two entries equivalent.</p>
     *
     * @see AspectEntry#getQualifier()
     */
    @Override
    default int compare(AspectEntry<?, ?> o1, AspectEntry<?, ?> o2) {
        return 0;
    }
}
