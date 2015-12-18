package br.net.mirante.singular.util.wicket.datatable.column;

import br.net.mirante.singular.util.wicket.datatable.IBSAction;
import br.net.mirante.singular.lambda.IFunction;
import br.net.mirante.singular.util.wicket.resource.Icone;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.$m;

public class BSActionColumn<T, S> extends BSAbstractColumn<T, S> {

    private final List<ActionItem> actions = new ArrayList<>();

    public BSActionColumn() {
        super($m.ofValue(""));
    }

    public BSActionColumn(IModel<String> displayModel) {
        super(displayModel);
    }

    @Override
    public String getCssClass() {
        return " action-column " + super.getCssClass();
    }

    @Override
    public final void populateItem(Item<ICellPopulator<T>> cellItem, String componentId, IModel<T> rowModel) {
        BSActionPanel<T> actionPanel = newActionPanel(componentId);
        cellItem.add(actionPanel);
        onPopulateActions(rowModel, actionPanel);
    }

    protected BSActionPanel<T> newActionPanel(String componentId) {
        return new BSActionPanel<>(componentId);
    }

    protected void onPopulateActions(IModel<T> rowModel, BSActionPanel<T> actionPanel) {
        for (ActionItem item : actions)
            actionPanel.appendAction(item.label, item.icone, item.action, rowModel);
    }

    public final BSActionColumn<T, S> appendAction(IModel<?> labelModel, Icone icone, IBSAction<T> action) {
        return appendAction(labelModel, $m.ofValue(icone), action);
    }


    public final BSActionColumn<T, S> appendAction(IModel<?> labelModel, IBSAction<T> action) {
        return appendAction(labelModel, (IModel<Icone>) null, action);
    }

    public final BSActionColumn<T, S> appendAction(IModel<?> labelModel, IModel<Icone> iconeModel, IBSAction<T> action) {
        actions.add(new ActionItem(labelModel, iconeModel, action));
        return this;
    }

    @Override
    public BSActionColumn<T, S> setRowMergeIdFunction(IFunction<T, ?> rowMergeIdFunction) {
        return (BSActionColumn<T, S>) super.setRowMergeIdFunction(rowMergeIdFunction);
    }

    private class ActionItem implements Serializable {
        final IModel<?> label;
        final IModel<Icone> icone;
        final IBSAction<T> action;

        public ActionItem(IModel<?> label, IModel<Icone> icone, IBSAction<T> action) {
            this.label = label;
            this.icone = icone;
            this.action = action;
        }
    }
}
