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

package org.opensingular.form.persistence;

import org.opensingular.form.AtrRef;
import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SDictionary;
import org.opensingular.form.SISimple;
import org.opensingular.form.SInfoPackage;
import org.opensingular.form.SPackage;
import org.opensingular.form.SType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.STypeSimple;
import org.opensingular.form.type.core.SIString;
import org.opensingular.form.type.core.STypeString;

/**
 * Pacote com atributos e tipos para apoio na persistência de Collections de instâncias.
 *
 * @author Daniel C. Bordin
 * @author Edmundo Andrade
 */
@SInfoPackage(name = SDictionary.SINGULAR_PACKAGES_PREFIX + "persitence")
public class SPackageFormPersistence extends SPackage {

    static final AtrRef<STypeFormKey, SISimple, FormKey> ATR_FORM_KEY = new AtrRef<>(SPackageFormPersistence.class,
            "formKey", STypeFormKey.class, SISimple.class, FormKey.class);

    // Relational mapping attributes
    public static final AtrRef<STypeString, SIString, String> ATR_TABLE = new AtrRef<>(SPackageFormPersistence.class, "table", STypeString.class, SIString.class, String.class);
    public static final AtrRef<STypeString, SIString, String> ATR_TABLE_PK = new AtrRef<>(SPackageFormPersistence.class, "tablePK", STypeString.class, SIString.class, String.class);
    public static final AtrRef<STypeString, SIString, String> ATR_COLUMN = new AtrRef<>(SPackageFormPersistence.class, "column", STypeString.class, SIString.class, String.class);
    public static final AtrRef<STypeString, SIString, String> ATR_TABLE_FKS = new AtrRef<>(SPackageFormPersistence.class, "tableFKs",  STypeString.class, SIString.class, String.class);

    protected void onLoadPackage(PackageBuilder pb) {
        pb.createType(STypeFormKey.class);
        pb.createAttributeIntoType(STypeComposite.class, ATR_FORM_KEY);

        // Relational mapping
        pb.createAttributeIntoType(SType.class, ATR_TABLE);
        pb.createAttributeIntoType(SType.class, ATR_TABLE_PK);
        pb.createAttributeIntoType(SType.class, ATR_TABLE_FKS);
        pb.createAttributeIntoType(STypeSimple.class, ATR_COLUMN);
    }
}
