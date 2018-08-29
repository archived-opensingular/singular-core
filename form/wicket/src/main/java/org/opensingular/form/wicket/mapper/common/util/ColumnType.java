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

import javax.annotation.Nullable;

public class ColumnType {

    private final String typeName;
    private final String customLabel;
    private final String columnSortName; //The ColumnSort Name have to use the same name of the SType.
    private final IFunction<SInstance, String> displayFunction;

    /**
     * @param typeName        The name of the column.
     * @param customLabel     A custom label for the column.
     * @param columnSortName  The ColumnSort Name have to use the same name of the SType.
     *                        Null for don't able the column sort.
     * @param displayFunction Function for display some value.
     */
    public ColumnType(String typeName, String customLabel, @Nullable String columnSortName, IFunction<SInstance, String> displayFunction) {
        this.typeName = typeName;
        this.customLabel = customLabel;
        this.columnSortName = columnSortName;
        this.displayFunction = displayFunction != null ? displayFunction : SInstance::toStringDisplay;
    }

    public ColumnType(String typeName, String customLabel, @Nullable String columnSortName) {
        this(typeName, customLabel, columnSortName, null);
    }

    public SType<?> getType(SInstance instance) {
        return typeName == null ? null : instance.getDictionary().getType(typeName);
    }

    public String getColumnSortName() {
        return columnSortName;
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
