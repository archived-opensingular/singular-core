/*
 *
 *  * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.opensingular.form.flatview;

import org.opensingular.form.SType;
import org.opensingular.form.aspect.QualifierStrategyByEquals;
import org.opensingular.form.view.SView;
import org.opensingular.form.view.ViewResolver;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SViewQualifierQualifierStrategy extends QualifierStrategyByEquals<Class<? extends SView>> {
    @Nullable
    @Override
    protected Class<? extends SView> extractQualifier(@Nonnull SType<?> type) {
        SView view = ViewResolver.resolveView(type);
        if(view != null){
            return view.getClass();
        }
        return null;
    }

}