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

import org.opensingular.form.SType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

/**
 * It's a {@link QualifierStrategy} that match entries that have the class nearest class returned by {@link
 * #extractQualifier(SType)}.
 *
 * @author Daniel C. Bordin on 15/08/2017.
 */
public abstract class QualifierStrategyByClassQualifier<T extends Class<?>> implements QualifierStrategy<T> {

    @Nullable
    protected abstract T extractQualifier(@Nonnull SType<?> type);

    @Override
    public QualifierMatcher<T> getMatcherFor(@Nonnull SType<?> type) {
        return new QualifierMatcherByClassQualifier<T>(extractQualifier(type));
    }

    private static class QualifierMatcherByClassQualifier<T extends Class<?>> implements QualifierMatcher<T> {

        @Nullable
        private final T qualifierTargetClass;

        public QualifierMatcherByClassQualifier(@Nullable T qualifierTargetClass) {
            this.qualifierTargetClass = qualifierTargetClass;
        }

        @Override
        public boolean isMatch(@Nonnull AspectEntry<?, T> entry) {
            if (qualifierTargetClass == null) {
                return entry.getQualifier() == null;
            } else if (entry.getQualifier() == null) {
                return true;
            }
            return entry.getQualifier().isAssignableFrom(qualifierTargetClass);
        }

        @Override
        public boolean isTheBestPossibleMatch(@Nonnull AspectEntry<?, T> entry) {
            return Objects.equals(qualifierTargetClass, entry.getQualifier());
        }

        @Override
        public int compare(AspectEntry<?, T> entry1, AspectEntry<?, T> entry2) {
            if (qualifierTargetClass == null) {
                return 0;
            }
            return distanceToClass(entry1.getQualifier(), qualifierTargetClass) - distanceToClass(entry2.getQualifier(),
                    qualifierTargetClass);
        }

        private int distanceToClass(Class<?> current, Class<?> target) {
            int distance = 0;
            for (Class<?> c = target; c != null; c = c.getSuperclass()) {
                if (c == current) {
                    break;
                }
                distance++;
            }
            return distance;
        }
    }
}
