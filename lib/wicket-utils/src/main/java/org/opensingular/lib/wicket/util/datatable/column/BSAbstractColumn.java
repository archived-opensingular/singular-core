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

import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.model.IModel;

import org.opensingular.lib.commons.lambda.IFunction;

public abstract class BSAbstractColumn<T, S>
    extends AbstractColumn<T, S>
    implements IRowMergeableColumn<T> {

    private IFunction<T, ?> rowMergeIdFunction = it -> null;

    public BSAbstractColumn(IModel<String> displayModel, S sortProperty) {
        super(displayModel, sortProperty);
    }

    public BSAbstractColumn(IModel<String> displayModel) {
        super(displayModel);
    }

    public IFunction<T, ?> getRowMergeIdFunction() {
        return rowMergeIdFunction;
    }
    public BSAbstractColumn<T, S> setRowMergeIdFunction(IFunction<T, ?> rowMergeIdFunction) {
        this.rowMergeIdFunction = (rowMergeIdFunction != null) ? rowMergeIdFunction : it -> null;
        return this;
    }
    @Override
    public Object getRowMergeId(IModel<T> rowModel) {
        return (rowMergeIdFunction == null) ? null : rowMergeIdFunction.apply(rowModel.getObject());
    }

}
