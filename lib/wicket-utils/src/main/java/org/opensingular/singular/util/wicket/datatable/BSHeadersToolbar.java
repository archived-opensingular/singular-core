/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.util.wicket.datatable;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.sort.AjaxFallbackOrderByLink;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.ISortStateLocator;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByLink;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.HeadersToolbar;
import org.apache.wicket.markup.html.WebMarkupContainer;

import static org.opensingular.singular.util.wicket.util.WicketUtils.$b;

public class BSHeadersToolbar<S> extends HeadersToolbar<S> {

    public <T> BSHeadersToolbar(DataTable<T, S> table, ISortStateLocator<S> stateLocator) {
        super(table, stateLocator);
    }

    @Override
    protected WebMarkupContainer newSortableHeader(String headerId, S property, ISortStateLocator<S> locator) {
        return new BSOrderByBorder(headerId, property, locator);
    }

    protected void onSortChanged(AjaxRequestTarget target) {
        if (target != null) {
            target.add(getTable());
        }
    }

    private final class BSOrderByBorder extends OrderByBorder<S> {

        private BSOrderByBorder(String id, S property, ISortStateLocator<S> stateLocator) {
            super(id, property, stateLocator);
        }

        @Override
        protected void onInitialize() {
            super.onInitialize();
            add($b.classAppender("default-cursor"));
        }

        @Override
        protected void onSortChanged() {
            getTable().setCurrentPage(0);
        }

        @Override
        protected OrderByLink<S> newOrderByLink(String id, S property, ISortStateLocator<S> stateLocator) {
            return new BSAjaxOrderByLink(id, property, stateLocator);
        }

        private final class BSAjaxOrderByLink extends AjaxFallbackOrderByLink<S> {
            private BSAjaxOrderByLink(String id, S sortProperty, ISortStateLocator<S> stateLocator) {
                super(id, sortProperty, stateLocator);
            }
            @Override
            protected void onSortChanged() {
                BSOrderByBorder.this.onSortChanged();
            }
            @Override
            public void onClick(AjaxRequestTarget target) {
                BSHeadersToolbar.this.onSortChanged(target);
            }
        }
    }
}
