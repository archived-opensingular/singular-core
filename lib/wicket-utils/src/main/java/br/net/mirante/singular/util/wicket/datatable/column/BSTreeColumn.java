package br.net.mirante.singular.util.wicket.datatable.column;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.$m;

import java.io.Serializable;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.tree.Node;
import org.apache.wicket.extensions.markup.html.repeater.tree.content.Folder;
import org.apache.wicket.extensions.markup.html.repeater.tree.table.AbstractTreeColumn;
import org.apache.wicket.extensions.markup.html.repeater.tree.table.NodeBorder;
import org.apache.wicket.extensions.markup.html.repeater.tree.table.NodeModel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.lambda.IFunction;

public class BSTreeColumn<T, S> extends AbstractTreeColumn<T, S> {

    public interface NodeComponentFactory<T> extends Serializable {
        Component newNodeComponent(String componentId, IModel<T> model);
    }

    private IFunction<T, ?> contentLabelFunction = it -> it;

    public BSTreeColumn(IModel<String> displayModel) {
        super(displayModel);
    }

    public BSTreeColumn(IModel<String> displayModel, S sortProperty) {
        super(displayModel, sortProperty);
    }

    public BSTreeColumn<T, S> setContentLabelFunction(IFunction<T, ?> contentLabelFunction) {
        this.contentLabelFunction = (contentLabelFunction != null) ? contentLabelFunction : m -> m;
        return this;
    }

    @Override
    public String getCssClass() {
        return "tree";
    }

    @Override
    public void populateItem(Item<ICellPopulator<T>> cellItem, String componentId, IModel<T> rowModel) {
        NodeModel<T> nodeModel = (NodeModel<T>) rowModel;

        Component nodeComponent = newNodeComponent(componentId, nodeModel.getWrappedModel());

        nodeComponent.add(new NodeBorder(nodeModel.getBranches()));

        cellItem.add(nodeComponent);
    }

    protected Component newNodeComponent(String componentId, IModel<T> model) {
        return new Node<T>(componentId, getTree(), model) {
            @Override
            protected Component createContent(String id, IModel<T> model) {
                return BSTreeColumn.this.newContentComponent(id, model);
            }
        };
    }

    protected Component newContentComponent(String componentId, IModel<T> model) {
        return new Folder<T>(componentId, getTree(), model) {
            @Override
            protected IModel<?> newLabelModel(IModel<T> model) {
                return $m.map(model, contentLabelFunction);
            }
        };
    }
}
