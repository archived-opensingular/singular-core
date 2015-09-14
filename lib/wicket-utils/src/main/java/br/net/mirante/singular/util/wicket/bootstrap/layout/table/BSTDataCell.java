package br.net.mirante.singular.util.wicket.bootstrap.layout.table;

import org.apache.wicket.behavior.Behavior;

import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import br.net.mirante.singular.util.wicket.bootstrap.layout.IBSGridCol;

public class BSTDataCell extends BSContainer<BSTDataCell> implements IBSGridCol<BSTDataCell> {

    public BSTDataCell(String id) {
        super(id);
        add(newBSGridColBehavior());
    }

    @Override
    public BSTDataCell add(Behavior... behaviors) {
        return (BSTDataCell) super.add(behaviors);
    }
}
