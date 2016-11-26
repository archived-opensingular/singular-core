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

package org.opensingular.form.wicket.mapper.common.util;

import org.opensingular.form.SInstance;
import org.opensingular.form.SType;
import org.opensingular.form.type.basic.SPackageBasic;
import org.opensingular.lib.commons.base.SingularException;
import org.opensingular.lib.commons.lambda.IFunction;

public class ColumnType {

    private final String                       typeName;
    private final String                       customLabel;
    private final IFunction<SInstance, String> displayFunction;

    public ColumnType(String typeName, String customLabel, IFunction<SInstance, String> displayFunction) {
        this.typeName = typeName;
        this.customLabel = customLabel;
        this.displayFunction = displayFunction != null ? displayFunction : SInstance::toStringDisplay;
    }

    public ColumnType(String typeName, String customLabel) {
        this(typeName, customLabel, null);
    }

    public SType<?> getType(SInstance instance) {
        return typeName == null ? null : instance.getDictionary().getType(typeName);
    }

    public String getTypeName() {
        return typeName;
    }

    public String getCustomLabel(SInstance instance) {
        SType<?> type = getType(instance);
        if (customLabel == null && type != null) {
            return getType(instance).getAttributeValue(SPackageBasic.ATR_LABEL);
        }
        if (type == null && displayFunction == null) {
            throw SingularException.rethrow("Não foi especificado label para coluna nem através do tipo nem através de displayFunction específica.");
        }
        return customLabel;
    }

    public IFunction<SInstance, String> getDisplayFunction() {
        return displayFunction;
    }

}
