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

package org.opensingular.form.io.definition;

import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SDictionary;
import org.opensingular.form.SInfoPackage;
import org.opensingular.form.SPackage;

@SInfoPackage(name = SDictionary.SINGULAR_PACKAGES_PREFIX + "io.definition")
public class SPackageDefinitionPersitence extends SPackage {

    @Override
    protected void onLoadPackage(PackageBuilder pb) {
        pb.createType(STypePersistenceAttribute.class);
        pb.createType(STypePersistenceType.class);
        pb.createType(STypePersistencePackage.class);
        pb.createType(STypePersistenceArchive.class);
    }
}
