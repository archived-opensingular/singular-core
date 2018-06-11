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

package org.opensingular.form.type.core;

import org.apache.commons.lang3.StringUtils;
import org.opensingular.form.SInfoType;
import org.opensingular.form.STypeSimple;
import org.opensingular.form.TypeBuilder;

@SInfoType(name = "HTML", spackage = SPackageCore.class)
public class STypeHTML extends STypeSimple<SIHTML, String> {

    public STypeHTML() {
        super(SIHTML.class, String.class);
    }

    protected STypeHTML(Class<? extends SIHTML> instanceClass) {
        super(instanceClass, String.class);
    }

    @Override
    protected void onLoadType(TypeBuilder tb) {
        asAtrBootstrap().colPreference(12);
    }

    @Override
    public String convert(Object value) {
        if (value instanceof String) {
            String valueText = (String) value;
            if (StringUtils.isEmpty(valueText)) {
               return null;
            }
            return fromString(valueText);
        } else {
            return super.convert(value);
        }
    }

    @Override
    public String fromString(String value) {
        return value;
//        return HtmlSanitizer.sanitize(value);
    }
}