/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.util.wicket.bootstrap.layout.table;

import org.apache.wicket.behavior.Behavior;

import org.opensingular.singular.util.wicket.bootstrap.layout.BSContainer;
import org.opensingular.singular.util.wicket.bootstrap.layout.IBSGridCol;

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
