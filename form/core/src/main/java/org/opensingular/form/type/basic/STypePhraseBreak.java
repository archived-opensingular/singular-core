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

package org.opensingular.form.type.basic;

import org.opensingular.form.STypeSimple;
import org.opensingular.form.enums.PhraseBreak;
import org.opensingular.form.SInfoType;

@SInfoType(name = "STypePhraseBreak", spackage = SPackageBasic.class)
public class STypePhraseBreak extends STypeSimple<SIPhraseBreak, PhraseBreak> {

    public STypePhraseBreak() {
        super(SIPhraseBreak.class, PhraseBreak.class);
    }

}