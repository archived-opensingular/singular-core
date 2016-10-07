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

package org.opensingular.form.wicket.mapper.masterdetail;

import org.opensingular.lib.commons.base.SingularException;
import org.opensingular.lib.commons.lambda.IFunction;
import org.opensingular.form.SInstance;
import org.opensingular.form.SType;
import org.opensingular.form.type.basic.SPackageBasic;

class ColumnType {

    private final SType<?>                     type;
    private final String                       customLabel;
    private final IFunction<SInstance, String> displayFunction;

    ColumnType(SType<?> type, String customLabel, IFunction<SInstance, String> displayFunction) {
        if (type == null && displayFunction == null) {
            throw new SingularException("Não foi especificado o valor da coluna.");
        }
        this.type = type;
        this.customLabel = customLabel;
        this.displayFunction = displayFunction != null ? displayFunction : SInstance::toStringDisplay;
    }

    ColumnType(SType<?> type, String customLabel) {
        this(type, customLabel, null);
    }

    public SType<?> getType() {
        return type;
    }

    String getCustomLabel() {
        if (customLabel == null && type != null) {
            return type.getAttributeValue(SPackageBasic.ATR_LABEL);
        }
        return customLabel;
    }

    IFunction<SInstance, String> getDisplayFunction() {
        return displayFunction;
    }

}
