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

package org.opensingular.form.type.basic;

import org.opensingular.form.SAttributeEnabled;
import org.opensingular.form.STranslatorForAttribute;
import org.opensingular.form.type.core.SPackagePersistence;

import java.util.function.Function;

public class AtrIndex extends STranslatorForAttribute {

    public AtrIndex() {
    }

    public AtrIndex(SAttributeEnabled target) {
        super(target);
    }

    public static <A extends SAttributeEnabled> Function<A, AtrIndex> factory() {
        return AtrIndex::new;
    }

    public AtrIndex persistent(Boolean persistent) {
        setAttributeValue(SPackagePersistence.ATR_PERSISTENT, persistent);
        return this;
    }

    public AtrIndex alias(String alias) {
        setAttributeValue(SPackagePersistence.ATR_ALIAS, alias);
        return this;
    }


    public Boolean isPersistent() {
        return getAttributeValue(SPackagePersistence.ATR_PERSISTENT) == null ? Boolean.FALSE: getAttributeValue(SPackagePersistence.ATR_PERSISTENT);
    }

    public String getAlias() {
        return getAttributeValue(SPackagePersistence.ATR_ALIAS);
    }
}
