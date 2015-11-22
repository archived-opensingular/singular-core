package br.net.mirante.singular.form.mform.basic.view;

import br.net.mirante.singular.form.mform.MISimples;
import br.net.mirante.singular.form.mform.MInstancia;

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
                int size = simple.getMTipo().getProviderOpcoes().getOpcoes().size();
                if (size <= 3 && simple.getMTipo().isObrigatorio()) {
                    return newInstance(MSelecaoPorRadioView.class);
                }
                return newInstance(MSelecaoPorSelectView.class);
            }
        }
        return null;
    }

}
