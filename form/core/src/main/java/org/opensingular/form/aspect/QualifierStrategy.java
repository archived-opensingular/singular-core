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

/**
 * It's a factory for creating a {@link QualifierMatcher} witch will look for the best result for a particular {@link
 * SType} or {@link SInstance}.
 *
 * @author Daniel C. Bordin on 09/08/2017.
 */
public interface QualifierStrategy {

    /** Its factory for that creates a matcher that accepts any {@link AspectEntry}. */
    public static final QualifierStrategy NO_QUALIFIER = new EmptyQualifierStrategy();

    /** Creates a matcher for a particular {@link SInstance}. */
    public QualifierMatcher getMatcherFor(SInstance instance);

    /** Creates a matcher for a particular {@link SType}. */
    public QualifierMatcher getMatcherFor(SType<?> type);

    static final class EmptyQualifierStrategy implements QualifierStrategy {

        @Override
        public QualifierMatcher getMatcherFor(SInstance instance) {
            return QualifierMatcher.ANY;
        }

        @Override
        public QualifierMatcher getMatcherFor(SType<?> type) { return QualifierMatcher.ANY; }

    }
}
