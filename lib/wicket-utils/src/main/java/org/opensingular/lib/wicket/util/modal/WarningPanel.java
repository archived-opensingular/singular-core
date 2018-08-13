package org.opensingular.lib.wicket.util.modal;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.opensingular.form.validation.ValidationError;

import java.util.List;

/**
 * Panel for show warning messages.
 */
public class WarningPanel extends Panel {

    private List<ValidationError> retrieveWarningErrors;

    public WarningPanel(String id, List<ValidationError> retrieveWarningErrors) {
        super(id);
        this.retrieveWarningErrors = retrieveWarningErrors;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        ListView<ValidationError> view = new ListView<ValidationError>("listWaring", retrieveWarningErrors) {
            @Override
            protected void populateItem(ListItem<ValidationError> item) {
                item.add(new Label("warning", item.getModelObject().getMessage()));
            }
        };
        add(view);
    }
}
