/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.lib.wicket.util.datatable;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.navigation.paging.IPageableItems;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static org.opensingular.lib.wicket.util.util.WicketUtils.$m;

public class BSItemsPerPageDropDown extends DropDownChoice<Long> {

    private final IPageableItems pageableComponent;

    public <P extends Component & IPageableItems> BSItemsPerPageDropDown(String id, P pageableComponent) {
        super(id);
        this.pageableComponent = pageableComponent;
        setModel($m.getSet(() -> this.getPageable().getItemsPerPage(), arg -> this.getPageable().setItemsPerPage(arg)));
    }

    @SuppressWarnings("unchecked")
    public <P extends Component & IPageableItems> P getPageable() {
        return (P) pageableComponent;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        setChoices(this.getItemsPerPageOptions());
    }

    protected List<Long> getItemsPerPageOptions() {
        Set<Long> options = new TreeSet<>();
        options.add(getPageable().getItemsPerPage());
        options.add(10L);
        options.add(30L);
        options.add(50L);
        options.add(100L);
        return new ArrayList<>(options);
    }
}
