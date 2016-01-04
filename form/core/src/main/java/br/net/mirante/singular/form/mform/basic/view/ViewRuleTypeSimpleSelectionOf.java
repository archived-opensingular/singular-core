package br.net.mirante.singular.form.mform.basic.view;

import br.net.mirante.singular.form.mform.MISimples;
import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.options.MOptionsProvider;
import br.net.mirante.singular.form.mform.options.MSelectionableType;
import br.net.mirante.singular.form.mform.options.MSelectionableInstance;

/**
 * Decide a melhor view para um tipo simples que seja um selection of.
 * 
 * @author Daniel C. Bordin
 */
public class ViewRuleTypeSimpleSelectionOf extends ViewRule {

    @Override
    public MView apply(MInstancia instance) {
        if (instance instanceof MSelectionableInstance) {
            MSelectionableInstance simple = (MSelectionableInstance) instance;
            MSelectionableType type = (MSelectionableType) simple.getMTipo();
            if (type.getProviderOpcoes() != null) {
                MOptionsProvider provider = type.getProviderOpcoes();
                return decideView(instance, (MInstancia) simple, provider);
            }
        }
        return null;
    }

    //TODO: [Fabs] this decision is strange to apply when the value is dynamic
    private MView decideView(MInstancia instance, MInstancia simple, MOptionsProvider provider) {
        int size = provider.listAvailableOptions(instance).size();
        if (size <= 3 && simple.getMTipo().isObrigatorio()) {
            return newInstance(MSelecaoPorRadioView.class);
        }
        return newInstance(MSelecaoPorSelectView.class);
    }

}
