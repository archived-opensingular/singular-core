package br.net.mirante.singular.form.wicket.mapper.selection;

import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.basic.view.SView;
import br.net.mirante.singular.form.mform.basic.view.SViewAutoComplete;
import br.net.mirante.singular.form.wicket.mapper.ControlsFieldComponentMapper;
import br.net.mirante.singular.form.wicket.model.MInstanciaValorModel;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSControls;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;

/**
 * Created by nuk on 21/03/16.
 */
public class AutocompleteMapper implements ControlsFieldComponentMapper {
    @Override
    public Component appendInput(SView view, BSContainer bodyContainer,
                                 BSControls formGroup,
                                 IModel<? extends SInstance> model,
                                 IModel<String> labelModel) {
        Component comp;

        formGroup.appendDiv(comp = new TypeheadComponent(model.getObject().getName(),
                model, ((SViewAutoComplete)view).fetch()));
        return comp;
    }

    @Override
    public String getReadOnlyFormattedText(IModel<? extends SInstance> model) {
        final SInstance mi = model.getObject();
        if ((mi != null) && (mi.getValue() != null)) {
            return String.valueOf(mi.getValue());
        }
        return StringUtils.EMPTY;
    }
}
