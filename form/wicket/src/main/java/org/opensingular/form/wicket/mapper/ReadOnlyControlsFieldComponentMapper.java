package org.opensingular.form.wicket.mapper;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.opensingular.form.wicket.WicketBuildContext;
import org.slf4j.Logger;

import org.opensingular.form.SInstance;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSControls;

public class ReadOnlyControlsFieldComponentMapper extends AbstractControlsFieldComponentMapper {

    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(ReadOnlyControlsFieldComponentMapper.class);

    @Override
    public Component appendInput(WicketBuildContext ctx, BSControls formGroup, IModel<String> labelModel) {
        final IModel<? extends SInstance> model = ctx.getModel();

        final FormComponent<?> field = new TextField<>(model.getObject().getName(), new Model<String>() {
            @Override
            public String getObject() {
                return getReadOnlyFormattedText(model);
            }

            @Override
            public void setObject(String object) {}
        });

        field.setEnabled(false);
        field.setLabel(labelModel);
        formGroup.appendInputText(field);

        return field;

    }

    @Override
    public String getReadOnlyFormattedText(IModel<? extends SInstance> model) {
        final String displayString = model.getObject().toStringDisplay();
        if (displayString == null) {
            LOGGER.warn("A avaliação de toStringDisplay retornou null");
        }
        return displayString;
    }
}
