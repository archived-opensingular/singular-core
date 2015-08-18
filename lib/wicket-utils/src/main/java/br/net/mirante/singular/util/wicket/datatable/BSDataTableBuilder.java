package br.net.mirante.singular.util.wicket.datatable;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.ISortableDataProvider;
import org.apache.wicket.extensions.markup.html.repeater.tree.ISortableTreeProvider;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.util.wicket.datatable.column.BSActionColumn;
import br.net.mirante.singular.util.wicket.datatable.column.BSPropertyColumn;
import br.net.mirante.singular.util.wicket.lambda.IConsumer;
import br.net.mirante.singular.util.wicket.lambda.IFunction;

public class BSDataTableBuilder<T, S, PREVCOL extends IColumn<T, S>> {

    public static interface BSActionColumnCallback<T, S> extends IConsumer<BSActionColumn<T, S>> {}

    private final List<? extends IColumn<T, S>> columns        = new ArrayList<>();
    private ISortableDataProvider<T, S>         dataProvider;
    private ISortableTreeProvider<T, S>         treeProvider;
    private Long                                rowsPerPage    = null;

    private boolean                             stripedRows    = false;
    private boolean                             hoverRows      = true;
    private boolean                             borderedTable  = true;
    private boolean                             condensedTable = false;

    public BSDataTableBuilder() {}

    public BSDataTableBuilder(ISortableDataProvider<T, S> dataProvider) {
        setDataProvider(dataProvider);
    }

    public BSDataTableBuilder(ISortableTreeProvider<T, S> dataProvider) {
        setTreeProvider(dataProvider);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public <C extends IColumn<T, S>> BSDataTableBuilder<T, S, C> appendColumn(C column) {
        ((List) columns).add(column);
        return (BSDataTableBuilder<T, S, C>) this;
    }

    @SuppressWarnings("unchecked")
    public BSDataTableBuilder<T, S, PREVCOL> configurePreviousColumn(IConsumer<PREVCOL> columnConfig) {
        columnConfig.accept((PREVCOL) columns.get(columns.size() - 1));
        return this;
    }

    public BSDataTableBuilder<T, S, BSPropertyColumn<T, S>> appendPropertyColumn(
        IModel<String> displayModel,
        IFunction<T, Object> propertyFunction)
    {
        return appendColumn(new BSPropertyColumn<T, S>(displayModel, propertyFunction));
    }

    public BSDataTableBuilder<T, S, BSPropertyColumn<T, S>> appendPropertyColumn(
        IModel<String> displayModel,
        String propertyExpression)
    {
        return appendColumn(new BSPropertyColumn<>(displayModel, propertyExpression));
    }

    public BSDataTableBuilder<T, S, BSPropertyColumn<T, S>> appendPropertyColumn(
        IModel<String> displayModel,
        S sortProperty,
        IFunction<T, Object> propertyFunction)
    {
        return appendColumn(new BSPropertyColumn<>(displayModel, sortProperty, propertyFunction));
    }

    public BSDataTableBuilder<T, S, BSPropertyColumn<T, S>> appendPropertyColumn(
        IModel<String> displayModel,
        S sortProperty,
        String propertyExpression)
    {
        BSPropertyColumn<T, S> column = new BSPropertyColumn<>(displayModel, sortProperty, propertyExpression);
        return appendColumn(column);
    }

    public BSDataTableBuilder<T, S, BSActionColumn<T, S>> appendActionColumn(
        IModel<String> displayModel,
        BSActionColumnCallback<T, S> callback)
    {
        BSActionColumn<T, S> column = new BSActionColumn<>(displayModel);
        callback.accept(column);
        return appendColumn(column);
    }

    public BSDataTableBuilder<T, S, ?> setDataProvider(ISortableDataProvider<T, S> dataProvider) {
        this.dataProvider = dataProvider;
        return this;
    }

    public BSDataTableBuilder<T, S, ?> setTreeProvider(ISortableTreeProvider<T, S> treeProvider) {
        this.treeProvider = treeProvider;
        return this;
    }

    public BSDataTableBuilder<T, S, ?> setStripedRows(boolean stripedRows) {
        this.stripedRows = stripedRows;
        return this;
    }
    public BSDataTableBuilder<T, S, ?> setHoverRows(boolean hoverRows) {
        this.hoverRows = hoverRows;
        return this;
    }
    public BSDataTableBuilder<T, S, ?> setBorderedTable(boolean borderedTable) {
        this.borderedTable = borderedTable;
        return this;
    }
    public BSDataTableBuilder<T, S, ?> setCondensedTable(boolean condensedTable) {
        this.condensedTable = condensedTable;
        return this;
    }

    public BSDataTableBuilder<T, S, ?> setRowsPerPage(long rowsPerPage) {
        this.rowsPerPage = rowsPerPage;
        return this;
    }

    public BSDataTable<T, S> build(String id) {
        return new BSDataTable<>(id, new ArrayList<>(columns), dataProvider)
            .setRowsPerPage(rowsPerPage)
            .setStripedRows(stripedRows)
            .setHoverRows(hoverRows)
            .setBorderedTable(borderedTable)
            .setCondensedTable(condensedTable);
    }

    public BSFlexDataTable<T, S> buildFlex(String id) {
        BSFlexDataTable<T, S> table = new BSFlexDataTable<>(id, new ArrayList<>(columns), dataProvider);
        table
            .setRowsPerPage(rowsPerPage)
            .setStripedRows(stripedRows)
            .setHoverRows(hoverRows)
            .setBorderedTable(borderedTable)
            .setCondensedTable(condensedTable);
        return table;
    }

    public BSTableTree<T, S> buildTree(String id) {
        return new BSTableTree<T, S>(id, new ArrayList<>(columns), treeProvider)
            .setRowsPerPage(rowsPerPage)
        //        .setStripedRows(stripedRows)
        //        .setHoverRows(hoverRows)
        //        .setBorderedTable(borderedTable)
        //        .setCondensedTable(condensedTable)
        ;
    }
}
