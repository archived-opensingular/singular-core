/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.lib.wicket.util.bootstrap.layout.table;

import org.apache.wicket.behavior.Behavior;

import org.opensingular.lib.wicket.util.bootstrap.layout.BSContainer;
import org.opensingular.lib.wicket.util.bootstrap.layout.IBSGridCol;

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
