/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.opensingular.bam.wicket.view.page.dashboard;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;

import static org.opensingular.lib.wicket.util.util.WicketUtils.$b;
import static org.opensingular.lib.wicket.util.util.WicketUtils.$m;

public class DashboardRow extends Panel {

    private RepeatingView columns;

    public DashboardRow(String id) {
        super(id);
        add(columns = new RepeatingView("columns"));
    }

    public <C extends WebMarkupContainer> C addSmallColumn(C column) {
        column.add($b.classAppender($m.get(this::getSmallColumnClass)));
        return addColumn(column);
    }

    public <C extends WebMarkupContainer> C addMediumColumn(C column) {
        column.add($b.classAppender($m.get(this::getMediumColumnClass)));
        return addColumn(column);
    }

    private <C extends WebMarkupContainer> C addColumn(C column) {
        columns.add(column);
        return column;
    }
    
    public int getColumnsSize() {
        int count = 0;
        for (Component component : columns) {
            if (component.isVisible()) {
                count++;
            }
        }
        return count;
    }

    private String getSmallColumnClass() {
        int count = getColumnsSize();
        return count > 1 ? "col-lg-"+(12 / count)+" col-md-" + (12  / ( count/2 )) +" col-sm-12 col-xs-12" : " col-xs-12";
    }

    private String getMediumColumnClass() {
        int count = getColumnsSize();
        return count > 1 ? "col-md-" + (12 / count) + " col-sm-12" : "col-md-12";
    }

}
