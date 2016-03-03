package br.net.mirante.singular.util.wicket.datatable.column;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.lambda.IBiFunction;
import br.net.mirante.singular.lambda.IFunction;
import br.net.mirante.singular.util.wicket.datatable.IBSAction;
import static br.net.mirante.singular.util.wicket.datatable.column.BSActionPanel.ActionConfig;
import br.net.mirante.singular.util.wicket.resource.Icone;
import static br.net.mirante.singular.util.wicket.util.WicketUtils.$m;

public class BSActionColumn<T, S> extends BSAbstractColumn<T, S> {

    private final List<ActionItem<T>> actions = new ArrayList<>();

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
        for (ActionItem<T> item : actions)
            actionPanel.appendAction(item.actionConfig, item.action, rowModel);
    }

    public final BSActionColumn<T, S> appendAction(IModel<?> labelModel, Icone icone, IBSAction<T> action) {
        return appendAction(labelModel, $m.ofValue(icone), action);
    }

    public final BSActionColumn<T, S> appendAction(IModel<?> labelModel, IBSAction<T> action) {
        return appendAction(labelModel, (IModel<Icone>) null, action);
    }

    public final BSActionColumn<T, S> appendAction(IModel<?> labelModel, IModel<Icone> iconeModel, IBSAction<T> action) {
        actions.add(new ActionItem<>(new ActionConfig().labelModel(labelModel).iconeModel(iconeModel), action));
        return this;
    }

    public final BSActionColumn<T, S> appendAction(ActionConfig config, IBSAction<T> action) {
        actions.add(new ActionItem<>(config, action));
        return this;
    }

    @Override
    public BSActionColumn<T, S> setRowMergeIdFunction(IFunction<T, ?> rowMergeIdFunction) {
        return (BSActionColumn<T, S>) super.setRowMergeIdFunction(rowMergeIdFunction);
    }

    public BSActionColumn<T, S> appendStaticAction(IModel<?> labelModel, Icone icone, IBiFunction<T, S, MarkupContainer> linkFactory) {
        actions.add(new ActionItem<>(new ActionConfig().labelModel(labelModel).iconeModel($m.ofValue(icone), null, $m.ofValue("fa-lg")).linkFactory(linkFactory), null));
        return this;
    }

    public BSActionColumn<T, S> appendStaticActionWithDefaultIcon(IModel<?> labelModel, Icone icone, IBiFunction<T, S, MarkupContainer> linkFactory) {
        actions.add(new ActionItem<>(new ActionConfig().labelModel(labelModel).iconeModel($m.ofValue(icone)).linkFactory(linkFactory), null));
        return this;
    }

    private static class ActionItem<T> implements Serializable {
        final ActionConfig actionConfig;
        final IBSAction<T> action;

        public ActionItem(ActionConfig actionConfig, IBSAction<T> action) {
            this.actionConfig  = actionConfig;
            this.action = action;
        }
    }
}
