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

package org.opensingular.form.filter;

import org.opensingular.form.SInstance;
import org.opensingular.form.SType;
import org.opensingular.form.aspect.AspectRef;
import org.opensingular.form.aspect.QualifierStrategyByClassQualifier;
import org.opensingular.form.aspect.SingleAspectRegistry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FormFilterRegistry extends SingleAspectRegistry<FormFilter, Class<? extends SType>> {

    public FormFilterRegistry(@Nonnull AspectRef<FormFilter> aspectRef) {
        super(aspectRef, new QualifierStrategyBySType());
    }

    public static class QualifierStrategyBySType extends QualifierStrategyByClassQualifier<Class<? extends SType>> {
        @Nullable
        @Override
        protected Class<? extends SType> extractQualifier(@Nonnull SInstance instance) {
            return instance.getType().getClass();
        }

        @Nullable
        @Override
        protected Class<? extends SType> extractQualifier(@Nonnull SType type) {
            return type.getClass();
        }
    }
}
