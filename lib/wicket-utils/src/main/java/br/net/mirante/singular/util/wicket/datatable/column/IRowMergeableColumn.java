/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.util.wicket.datatable.column;

import org.apache.wicket.model.IModel;

public interface IRowMergeableColumn<T> {
    Object getRowMergeId(IModel<T> rowModel);
}
