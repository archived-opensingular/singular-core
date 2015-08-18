package br.net.mirante.singular.util.wicket.datatable.column;

import org.apache.wicket.model.IModel;

public interface IRowMergeableColumn<T> {
    Object getRowMergeId(IModel<T> rowModel);
}
