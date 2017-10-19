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

import org.opensingular.form.SInfoType;
import org.opensingular.form.STypeComposite;

@SInfoType(name = "Formula", spackage = SPackageCore.class)
public class STypeFormula extends STypeComposite<SIFormula> {

    public static final String FIELD_SCRIPT = "script";
    public static final String FIELD_SCRIPT_TYPE = "scriptType";

    public STypeFormula() {
        super(SIFormula.class);
    }

    //For now this is not usefull
    //    @Override
    //    protected void onLoadType(TypeBuilder tb) {
    //        addFieldString(FIELD_SCRIPT);
    //        STypeString tipo = addFieldString(FIELD_SCRIPT_TYPE);
    //        tipo.selectionOfEnum(TipoScript.class);
    //    }

    public enum ScriptType {
        JS
    }
}
