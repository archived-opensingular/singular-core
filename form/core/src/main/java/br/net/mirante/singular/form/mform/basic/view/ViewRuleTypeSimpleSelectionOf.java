package br.net.mirante.singular.form.mform.basic.view;

import br.net.mirante.singular.form.mform.MISimples;
import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.options.MISelectItem;
import br.net.mirante.singular.form.mform.options.MOptionsProvider;
import br.net.mirante.singular.form.mform.options.MTipoSelectItem;

/**
 * Decide a melhor view para um tipo simples que seja um selection of.
 * 
 * @author Daniel C. Bordin
 */
public class ViewRuleTypeSimpleSelectionOf extends ViewRule {

    @Override
    public MView apply(MInstancia instance) {
        if (instance instanceof MISimples) {
            MISimples<?> simple = (MISimples<?>) instance;
            if (simple.getMTipo().getProviderOpcoes() != null) {
                MOptionsProvider provider = simple.getMTipo().getProviderOpcoes();
                return decideView(instance, simple, provider);
            }
        }else if (instance instanceof MISelectItem) {
            MISelectItem simple = (MISelectItem) instance;
            MTipoSelectItem type = (MTipoSelectItem) simple.getMTipo();
            if (type.getProviderOpcoes() != null) {
                MOptionsProvider provider = type.getProviderOpcoes();
                return decideView(instance, simple, provider);
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
