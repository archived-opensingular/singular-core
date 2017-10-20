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

import org.opensingular.form.SIComposite;

public class SIFormula extends SIComposite {

    public void setScript(String value) {
        setValue(STypeFormula.FIELD_SCRIPT, value);
    }

    public void setScriptJS(String value) {
        setScript(value);
        setScriptType(STypeFormula.ScriptType.JS);
    }

    private void setScriptType(STypeFormula.ScriptType t) {
        setValue(STypeFormula.FIELD_SCRIPT_TYPE, t);
    }

    public String getScriptType() {
        return getValueString(STypeFormula.FIELD_SCRIPT_TYPE);
    }

    public STypeFormula.ScriptType getScriptTypeAsEnum() {
        return getValueEnum(STypeFormula.FIELD_SCRIPT_TYPE, STypeFormula.ScriptType.class);
    }
}
