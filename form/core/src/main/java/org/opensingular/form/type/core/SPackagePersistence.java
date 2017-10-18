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

package org.opensingular.form.type.core;

import org.opensingular.form.AtrRef;
import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SIPredicate;
import org.opensingular.form.SInstance;
import org.opensingular.form.SPackage;
import org.opensingular.form.SType;
import org.opensingular.form.STypePredicate;

import javax.annotation.Nonnull;
import java.util.function.Predicate;

@SuppressWarnings("unchecked")
public class SPackagePersistence extends SPackage {

    public static final AtrRef<STypeBoolean, SIBoolean, Boolean>                  ATR_PERSISTENT = new AtrRef<>(SPackagePersistence.class, "persistent", STypeBoolean.class, SIBoolean.class, Boolean.class);
    public static final AtrRef<STypeString, SIString, String>                     ATR_ALIAS      = new AtrRef<>(SPackagePersistence.class, "alias", STypeString.class, SIString.class, String.class);
    public static final AtrRef<STypePredicate, SIPredicate, Predicate<SInstance>> ATR_XML        = new AtrRef(SPackagePersistence.class, "xmlOpts", STypePredicate.class, SIPredicate.class, Predicate.class);

    @Override
    protected void onLoadPackage(@Nonnull PackageBuilder pb) {
        pb.createAttributeIntoType(SType.class, ATR_PERSISTENT);
        pb.createAttributeIntoType(SType.class, ATR_XML);
        pb.createAttributeIntoType(SType.class, ATR_ALIAS);
    }
}
