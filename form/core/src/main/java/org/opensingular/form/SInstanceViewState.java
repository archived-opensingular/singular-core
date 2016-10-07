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

package org.opensingular.form;

import org.opensingular.form.type.basic.SPackageBasic;

public enum SInstanceViewState {
    EDITABLE, READONLY, HIDDEN;
    public boolean isVisible() {
        return this != HIDDEN;
    }
    public boolean isEnabled() {
        return this == EDITABLE;
    }

    /*
    
    +-----------+-----------------------+
    |           |        Enabled        |
    |           +-------+-------+-------+
    |           | TRUE  | FALSE | NULL  |
    +---+-------+-------+-------+-------+--------+
    | V |  NULL |   E   |   R   |   E   | Exists |
    | i |  TRUE |   E   |   R   |   E   | TRUE   |
    | s | FALSE |   H   |   H   |   H   |        |
    | i +-------+-------+-------+-------+--------+
    | b |  NULL |   H   |   H   |   H   | Exists |
    | l |  TRUE |   R   |   R   |   R   | FALSE  |
    | e | FALSE |   H   |   H   |   H   |        |
    +---+-------+-------+-------+-------+--------+
    E = editable
    R = readonly
    H = hidden
    
    */
    public static SInstanceViewState get(SInstance instance) {
        if (instance == null)
            return SInstanceViewState.HIDDEN;

        final boolean exists = instance.exists();
        final Boolean visible = SInstances.attributeValue(instance, SPackageBasic.ATR_VISIBLE, null);
        final Boolean enabled = SInstances.attributeValue(instance, SPackageBasic.ATR_ENABLED, null);

        if (exists) {
            if (visible != null && !visible) {
                return SInstanceViewState.HIDDEN;
            } else if (enabled != null && !enabled) {
                return SInstanceViewState.READONLY;
            } else {
                return SInstanceViewState.EDITABLE;
            }
        } else {
            if (visible != null && visible) {
                return SInstanceViewState.READONLY;
            } else {
                return SInstanceViewState.HIDDEN;
            }
        }
    }

    public static boolean isInstanceRequired(SInstance instance) {
        return (instance != null)
            && SInstances.attributeValue(instance, SPackageBasic.ATR_REQUIRED, false)
            && get(instance).isEnabled();
    }

}
