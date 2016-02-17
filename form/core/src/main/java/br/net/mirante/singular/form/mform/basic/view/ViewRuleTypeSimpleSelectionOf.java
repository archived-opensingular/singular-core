package br.net.mirante.singular.form.mform.basic.view;

import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.options.MOptionsProvider;
import br.net.mirante.singular.form.mform.options.MSelectionableInstance;
import br.net.mirante.singular.form.mform.options.MSelectionableType;

/**
 * Decide a melhor view para um tipo simples que seja um selection of.
 * 
 * @author Daniel C. Bordin
 */
public class ViewRuleTypeSimpleSelectionOf extends ViewRule {

    @Override
    public MView apply(SInstance instance) {
        if (instance != null) {
            MSelectionableInstance simple = (MSelectionableInstance) instance;
            MSelectionableType type = (MSelectionableType) simple.getType();
            if (type.getProviderOpcoes() != null) {
                MOptionsProvider provider = type.getProviderOpcoes();
                return decideView(instance, (SInstance) simple, provider);
            }
        }
        return null;
    }

    //TODO: [Fabs] this decision is strange to apply when the value is dynamic
    private MView decideView(SInstance instance, SInstance simple, MOptionsProvider provider) {
        int size = instance.getOptionsConfig().listSelectOptions().size();
        /* Tamanho zero indica uma possivel carga condicional e/ou dinamica. Nesse caso é mais produtente escolher
        *  combo: MSelecaoPorSelectView
        * */
        if (size <= 3 &&  size != 0 && simple.getType().isObrigatorio()) {
            return newInstance(MSelecaoPorRadioView.class);
        }
        return newInstance(MSelecaoPorSelectView.class);
    }

}
