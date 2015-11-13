package br.net.mirante.singular.view.page.dashboard;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.$b;
import static br.net.mirante.singular.util.wicket.util.WicketUtils.$m;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;

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

    private String getSmallColumnClass() {
        int count = 0;
        for (Component component : columns) {
            if (component.isVisible()) {
                count++;
            }
        }
        return count > 1 ? "col-lg-"+(12 / count)+" col-md-" + (12 / count) +" col-sm-"+(12 / count * 2) + " col-sm-12" : " col-xs-12";
    }

    private String getMediumColumnClass() {
        int count = 0;
        for (Component component : columns) {
            if (component.isVisible()) {
                count++;
            }
        }
        return count > 1 ? "col-md-" + (12 / count) + " col-sm-12" : "col-md-12";
    }

}
