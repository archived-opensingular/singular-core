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

package org.opensingular.lib.wicket.util.datatable.column;

import org.apache.wicket.extensions.markup.html.repeater.data.table.export.IExportableColumn;
import org.apache.wicket.model.IModel;
import org.opensingular.lib.commons.lambda.IFunction;

/**
 * This class action like the BSPropertyColumn, but, this will add a class CSS for represent the click action.
 *
 * @param <T>
 * @param <S>
 */
public class BSPropertyActionColumn<T, S> extends BSPropertyColumn<T, S> implements IExportableColumn<T, S> {

    public BSPropertyActionColumn(IModel<String> displayModel, IFunction<T, Object> propertyFunction) {
        super(displayModel, propertyFunction);
    }

    /**
     * This css class is used for represent the click on the column cell.
     * This is used in <code>SearchModalBodyPanel</code>.
     *
     * @return The css class.
     */
    @Override
    public String getCssClass() {
        return " column-clicked ";
    }
}
