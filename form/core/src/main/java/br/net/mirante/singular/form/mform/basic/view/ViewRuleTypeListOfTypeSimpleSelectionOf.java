package br.net.mirante.singular.form.mform.basic.view;

import br.net.mirante.singular.form.mform.SIList;
import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.options.SOptionsProvider;
import br.net.mirante.singular.form.mform.options.SSelectionableType;

/**
 * Decide qual a view mas adequada para uma seleção múltipla de tipos simples
 * (Lista de Tipo Simple, sendo o tipo simples um selectionOf).
 *
 * @author Daniel C. Bordin
 */
class ViewRuleTypeListOfTypeSimpleSelectionOf extends ViewRule {

    @Override @SuppressWarnings("rawtypes")
    public SView apply(SInstance listInstance) {
        if (listInstance instanceof SIList) {
            SIList<?> listType = (SIList<?>) listInstance;
            SType<?> elementType = listType.getElementsType();
            if (elementType instanceof SSelectionableType) {
                SSelectionableType type = (SSelectionableType) elementType;
                if (type.getOptionsProvider() != null) {
                    SOptionsProvider provider = type.getOptionsProvider();
                    return decideView(listInstance, provider);
                }
            }
        }
        return null;
    }
    
    private SView decideView(SInstance instance, SOptionsProvider provider) {
        int size = provider.listAvailableOptions(instance).size();
        if (size <= 3) {
            return newInstance(SMultiSelectionByCheckboxView.class);
        } else if (size < 20) {
            return newInstance(SMultiSelectionBySelectView.class);
        }
        return newInstance(SMultiSelectionByPicklistView.class);
    }

}
