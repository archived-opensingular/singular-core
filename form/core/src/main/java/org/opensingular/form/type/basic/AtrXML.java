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
import org.opensingular.form.SInstance;
import org.opensingular.form.STranslatorForAttribute;
import org.opensingular.form.type.core.SPackagePersistence;

import java.util.function.Function;
import java.util.function.Predicate;

public class AtrXML extends STranslatorForAttribute {

    public AtrXML() {
    }

    public AtrXML(SAttributeEnabled target) {
        super(target);
    }

    public static <A extends SAttributeEnabled> Function<A, AtrXML> factory() {
        return AtrXML::new;
    }

    public AtrXML keepEmptyNode() {
        setAttributeValue(SPackagePersistence.ATR_XML, si -> true);
        return this;
    }

    public AtrXML keepEmptyNode(boolean keep) {
        setAttributeValue(SPackagePersistence.ATR_XML, si -> keep);
        return this;
    }

    public AtrXML keepEmptyNode(Predicate<SInstance> value) {
        setAttributeValue(SPackagePersistence.ATR_XML, value);
        return this;
    }

    public Predicate<SInstance> getKeepNodePredicate() {
        return isKeepNodePredicateConfigured() ? getAttributeValue(SPackagePersistence.ATR_XML) : si -> false;
    }

    public boolean isKeepNodePredicateConfigured() {
        return getAttributeValue(SPackagePersistence.ATR_XML) != null;
    }

}
