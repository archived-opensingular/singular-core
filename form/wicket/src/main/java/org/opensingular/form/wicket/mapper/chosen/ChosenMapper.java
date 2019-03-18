package org.opensingular.form.wicket.mapper.chosen;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.opensingular.form.wicket.WicketBuildContext;
import org.opensingular.form.wicket.mapper.selection.MultipleSelectMapper;
import org.opensingular.form.wicket.model.ReadOnlyModelValue;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSControls;

public class ChosenMapper extends MultipleSelectMapper {
    private String emptyLabel;

    @Override
    protected Component appendFormGroup(BSControls formGroup, WicketBuildContext ctx) {
        final ChosenMultiSelectField<?> field = new ChosenMultiSelectField<Object>(formGroup.newChildId()) {
            @Override
            protected ListMultipleChoice<?> createChoiceField(String markupId) {
                return retrieveChoices(markupId, ctx.getModel(), new ReadOnlyModelValue(ctx.getModel()));
            }
        };
        if (emptyLabel != null) {
            field.setEmptyLabel(emptyLabel);
        }
        formGroup.appendDiv(field);
        return field;
    }

    public ChosenMapper setEmptyLabel(String emptyLabel) {
        this.emptyLabel = emptyLabel;
        return this;
    }
}