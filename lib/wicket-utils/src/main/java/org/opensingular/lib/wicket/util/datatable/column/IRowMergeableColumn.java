/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.lib.wicket.util.datatable.column;

import org.apache.wicket.model.IModel;

public interface IRowMergeableColumn<T> {
    Object getRowMergeId(IModel<T> rowModel);
}
