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

package org.opensingular.form.provider;

import org.opensingular.lib.commons.lambda.IFunction;
import org.opensingular.form.AtrRef;
import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SIFunction;
import org.opensingular.form.SPackage;
import org.opensingular.form.STypeFunction;
import org.opensingular.form.converter.SIConverter;
import org.opensingular.form.converter.SInstanceConverter;
import org.opensingular.form.converter.STypeConverter;
import org.opensingular.form.SType;

public class SPackageProvider extends SPackage {

    private static final String NAME = "singular.form.provider";

    public static final AtrRef<STypeProvider, SIProvider, Provider>             PROVIDER         = new AtrRef<>(SPackageProvider.class, "provider", STypeProvider.class, SIProvider.class, Provider.class);
    public static final AtrRef<STypeConverter, SIConverter, SInstanceConverter> CONVERTER        = new AtrRef<>(SPackageProvider.class, "converter", STypeConverter.class, SIConverter.class, SInstanceConverter.class);
    public static final AtrRef<STypeFunction, SIFunction, IFunction>            DISPLAY_FUNCTION = new AtrRef<>(SPackageProvider.class, "displayFunction", STypeFunction.class, SIFunction.class, IFunction.class);
    public static final AtrRef<STypeFunction, SIFunction, IFunction>            ID_FUNCTION      = new AtrRef<>(SPackageProvider.class, "idFunction", STypeFunction.class, SIFunction.class, IFunction.class);

    public SPackageProvider() {
        super(NAME);
    }

    @Override
    protected void onLoadPackage(PackageBuilder pb) {
        pb.createType(STypeProvider.class);
        pb.createType(STypeConverter.class);
        pb.createType(STypeFunction.class);
        pb.createAttributeIntoType(SType.class, PROVIDER);
        pb.createAttributeIntoType(SType.class, CONVERTER);
        pb.createAttributeIntoType(SType.class, DISPLAY_FUNCTION);
        pb.createAttributeIntoType(SType.class, ID_FUNCTION);
    }

}