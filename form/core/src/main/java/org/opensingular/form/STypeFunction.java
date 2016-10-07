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

package org.opensingular.form;


import org.opensingular.form.provider.SPackageProvider;
import org.opensingular.lib.commons.lambda.IFunction;

@SInfoType(name = "STypeFunction", spackage = SPackageProvider.class)
public class STypeFunction<I extends SIFunction<T, R>, T, R> extends STypeCode<I, IFunction<T, R>> {

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public STypeFunction() {
        super((Class) SIFunction.class, (Class) IFunction.class);
    }

}
