package br.net.mirante.singular.form.wicket.mapper.selection;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.SIList;
import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.STypeLista;
import br.net.mirante.singular.form.mform.basic.view.MView;
import br.net.mirante.singular.form.mform.options.MSelectionableInstance;
import br.net.mirante.singular.form.wicket.mapper.ControlsFieldComponentMapper;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSControls;
import static org.apache.wicket.util.lang.Generics.newArrayList;

@SuppressWarnings("serial")
public class MultipleSelectMapper implements ControlsFieldComponentMapper {

    @Override
    @SuppressWarnings("rawtypes")
    public Component appendInput(MView view, BSContainer bodyContainer,
                                 BSControls formGroup, final IModel<? extends SInstance> model,
                                 IModel<String> labelModel) {
        final List<SelectOption> opcoesValue;
        final STypeLista tipoLista;
        if (model.getObject().getType() instanceof STypeLista) {
            tipoLista = (STypeLista) model.getObject().getType();
            SType elementType = tipoLista.getTipoElementos();
            opcoesValue = WicketSelectionUtils.createOptions(model, elementType);
        } else {
            opcoesValue = newArrayList();
        }

        return formGroupAppender(formGroup, model, opcoesValue);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected ListMultipleChoice<SelectOption> retrieveChoices(
            IModel<? extends SInstance> model,
            final List<SelectOption> opcoesValue) {
        return new ListMultipleChoice<>(model.getObject().getNome(),
                (IModel) new MSelectionInstanceModel<List<SelectOption>>(model), opcoesValue, renderer());
    }

    @SuppressWarnings("rawtypes")
    protected Component formGroupAppender(BSControls formGroup,
                                          IModel<? extends SInstance> model,
                                          final List<SelectOption> opcoesValue) {
        final ListMultipleChoice<SelectOption> choices = retrieveChoices(model, opcoesValue);
        formGroup.appendSelect(choices.setMaxRows(5), true, false);
        return choices;
    }

    @SuppressWarnings("rawtypes")
    protected ChoiceRenderer renderer() {
        return new ChoiceRenderer("selectLabel", "value");
    }

    @Override
    public String getReadOnlyFormattedText(IModel<? extends SInstance> model) {

        final StringBuilder output = new StringBuilder();
        final SInstance mi = model.getObject();

        if (mi instanceof SIList) {
            final Collection children = ((SIList) mi).getChildren();
            final Iterator iterator = children.iterator();
            boolean first = true;
            while (iterator.hasNext()) {
                final Object val = iterator.next();
                if (val instanceof MSelectionableInstance) {
                    final String label = ((MSelectionableInstance) val).getSelectLabel();
                    if (first) {
                        output.append(label);
                        first = false;
                    } else {
                        output.append(", ");
                        output.append(label);
                    }
                }
            }
        }

        return output.toString();
    }

}
