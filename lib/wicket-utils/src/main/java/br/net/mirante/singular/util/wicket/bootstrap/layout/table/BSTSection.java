package br.net.mirante.singular.util.wicket.bootstrap.layout.table;

import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import br.net.mirante.singular.util.wicket.bootstrap.layout.IBSComponentFactory;
import br.net.mirante.singular.util.wicket.bootstrap.layout.IBSGridCol.BSGridSize;
import br.net.mirante.singular.util.wicket.lambda.IBiFunction;

public class BSTSection extends BSContainer<BSTSection> {

    private BSGridSize defaultGridSize = BSGridSize.MD;

    public BSTSection(String id) {
        super(id);
    }

    public BSTSection(String id, BSGridSize defaultGridSize) {
        super(id);
        this.defaultGridSize = defaultGridSize;
    }

    public BSTSection(String id, IModel<?> model) {
        super(id, model);
    }

    public BSGridSize getDefaultGridSize() {
        return defaultGridSize;
    }
    public BSTSection setDefaultGridSize(BSGridSize defaultGridSize) {
        this.defaultGridSize = defaultGridSize;
        return this;
    }

    public BSTRow newRow() {
        return newRow(BSTRow::new);
    }

    public <R extends BSTRow> R newRow(IBiFunction<String, BSGridSize, R> factory) {
        return newComponent(id -> factory.apply(id, getDefaultGridSize()));
    }

    public BSTSection appendRow(IBSComponentFactory<BSTRow> factory) {
        newComponent(factory).setDefaultGridSize(getDefaultGridSize());
        return this;
    }

    public BSTDataCell newColInRow() {
        return newColInRow(BSTDataCell.MAX_COLS);
    }

    public BSTDataCell newColInRow(int colspan) {
        return newRow()
            .newCol(colspan);
    }

    @Override
    public BSTSection add(Behavior... behaviors) {
        return (BSTSection) super.add(behaviors);
    }
}
