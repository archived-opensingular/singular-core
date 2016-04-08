package br.net.mirante.singular.form.wicket.mapper;

import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.basic.view.SView;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSControls;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.slf4j.Logger;

public class ReadOnlyControlsFieldComponentMapper implements ControlsFieldComponentMapper {

    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(ReadOnlyControlsFieldComponentMapper.class);

    @Override
    public Component appendInput(SView view,
                                 BSContainer bodyContainer,
                                 BSControls formGroup,
                                 IModel<? extends SInstance> model,
                                 IModel<String> labelModel) {

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
