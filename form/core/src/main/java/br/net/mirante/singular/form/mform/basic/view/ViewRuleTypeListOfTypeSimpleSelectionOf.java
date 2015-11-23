package br.net.mirante.singular.form.mform.basic.view;

import br.net.mirante.singular.form.mform.MILista;
import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.MTipoSimples;

/**
 * Decide qual a view mas adequada para uma seleção múltipla de tipos simples
 * (Lista de Tipo Simple, sendo o tipo simples um selectionOf).
 *
 * @author Daniel C. Bordin
 */
class ViewRuleTypeListOfTypeSimpleSelectionOf extends ViewRule {

    @Override
    public MView apply(MInstancia instance) {
        if (instance instanceof MILista) {
            MILista<?> list = (MILista<?>) instance;
            if (list.getTipoElementos() instanceof MTipoSimples) {
                MTipoSimples<?, ?> simples = (MTipoSimples<?, ?>) list.getTipoElementos();
                if (simples.getProviderOpcoes() != null) {
                    int size = simples.getProviderOpcoes().getOpcoes().size();
                    if (size <= 3) {
                        return newInstance(MSelecaoMultiplaPorCheckView.class);
                    } else if (size < 20) {
                        return newInstance(MSelecaoMultiplaPorSelectView.class);
                    }
                    return newInstance(MSelecaoMultiplaPorPicklistView.class);
                }
            }
        }
        return null;
    }

}
