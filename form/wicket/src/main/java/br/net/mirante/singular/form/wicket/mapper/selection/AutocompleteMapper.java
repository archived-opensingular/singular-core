package br.net.mirante.singular.form.wicket.mapper.selection;

import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.basic.view.SView;
import br.net.mirante.singular.form.mform.basic.view.SViewAutoComplete;
import br.net.mirante.singular.form.wicket.mapper.ControlsFieldComponentAbstractMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;

/**
 * Mapper responsible for rendering the SViewAutoComplete withing wicket.
 *
 * @author Fabricio Buzeto
 */
public class AutocompleteMapper extends ControlsFieldComponentAbstractMapper {

    @Override
    public Component appendInput() {
        TypeaheadComponent comp;

        validateView(view);
        formGroup.appendDiv(comp = new TypeaheadComponent(model.getObject().getName(),
                model, ((SViewAutoComplete)view).fetch()));
        return comp;
    }

    private void validateView(SView view) {
        if(!isAValidView(view)){
            throw new RuntimeException("AutocompleteMapper only accepts SViewAutoComplete as its view");
        }
    }

    private boolean isAValidView(SView view) {  return view instanceof SViewAutoComplete;   }

    @Override
    public String getReadOnlyFormattedText(IModel<? extends SInstance> model) {
        final SInstance mi = model.getObject();
        if ((mi != null) && (mi.getValue() != null)) {
            return mi.getOptionsConfig().getLabelFromOption(mi);
        }
        return StringUtils.EMPTY;
    }
}
