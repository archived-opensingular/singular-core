package br.net.mirante.singular.form.mform.basic.view;

import br.net.mirante.singular.form.mform.MILista;
import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.MTipo;
import br.net.mirante.singular.form.mform.options.MOptionsProvider;
import br.net.mirante.singular.form.mform.options.MSelectionableType;

/**
 * Decide qual a view mas adequada para uma seleção múltipla de tipos simples
 * (Lista de Tipo Simple, sendo o tipo simples um selectionOf).
 *
 * @author Daniel C. Bordin
 */
class ViewRuleTypeListOfTypeSimpleSelectionOf extends ViewRule {

    @Override @SuppressWarnings("rawtypes")
    public MView apply(MInstancia listInstance) {
        if (listInstance instanceof MILista) {
            MILista<?> listType = (MILista<?>) listInstance;
            MTipo<?> elementType = listType.getTipoElementos();
            if (elementType instanceof MSelectionableType) {
                MSelectionableType type = (MSelectionableType) elementType;
                if (type.getProviderOpcoes() != null) {
                    MOptionsProvider provider = type.getProviderOpcoes();
                    return decideView(listInstance, provider);
                }
            }
        }
        return null;
    }
    
    private MView decideView(MInstancia instance, MOptionsProvider provider) {
        int size = provider.listAvailableOptions(instance).size();
        if (size <= 3) {
            return newInstance(MSelecaoMultiplaPorCheckView.class);
        } else if (size < 20) {
            return newInstance(MSelecaoMultiplaPorSelectView.class);
        }
        return newInstance(MSelecaoMultiplaPorPicklistView.class);
    }

}
