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

/**
 * A matcher that accepts any entry that has a null qualifier.
 *
 * @author Daniel C. Bordin on 16/08/2017.
 */
final class QualifierMatcherNullQualifier implements QualifierMatcher<Object> {

    @Nonnull
    public static final QualifierMatcher<Object> NULL = new QualifierMatcherNullQualifier();

    @Override
    public boolean isMatch(@Nonnull AspectEntry<?, Object> entry) {
        return entry.getQualifier() == null;
    }

    @Override
    public boolean isTheBestPossibleMatch(@Nonnull AspectEntry<?, Object> entry) {
        return true;
    }

}
