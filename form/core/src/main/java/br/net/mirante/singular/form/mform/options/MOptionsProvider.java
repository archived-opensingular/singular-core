package br.net.mirante.singular.form.mform.options;

import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SList;
import br.net.mirante.singular.form.mform.SISimple;
import br.net.mirante.singular.form.mform.SInstance2;
import br.net.mirante.singular.form.mform.STypeSimples;
import br.net.mirante.singular.form.mform.util.transformer.Value;

import java.io.Serializable;
import java.util.Collection;

/**
 * This interface represents the providers which will load options that
 * populate the choices for a specific field. The provider is specified
 * during the declaration of a Type or Field by using the
 * {@link STypeSimples#withSelectionFromProvider(String)} method.
 */
public interface MOptionsProvider extends Serializable {

    public default String toDebug() {
        return null;
    }

    /**
     * Returns the list of options available for this selection, considering
     * also the old state of it.
     *
     * @param selectedValueInstance : Current isntance used to select the options.
     * @return list of options from the expected {@link SInstance2} type.
     */
    default public SList<? extends SInstance2> listAvailableOptions(MSelectionableInstance selectedValueInstance) {
        SList<? extends SInstance2> defaultOptions = listOptions((SInstance2) selectedValueInstance);
        checkForDanglingValues((SInstance2) selectedValueInstance, defaultOptions);
        return defaultOptions;
    }

    public default void checkForDanglingValues(SInstance2 selectedValueInstance, SList<? extends SInstance2> defaultOptions) {
        Object value = selectedValueInstance.getValor();
        if (value == null) return;
        if (value instanceof Collection && ((Collection<?>) value).isEmpty()) return;
        addNotPresentElement(selectedValueInstance, defaultOptions);
    }

    public default void addNotPresentElement(SInstance2 selectedValueInstance, SList<? extends SInstance2> defaultOptions) {
        if (selectedValueInstance instanceof SList) {
            addNotPresentElementsOfList((SList) selectedValueInstance, defaultOptions);
        } else {
            addNotPresentElement(defaultOptions, selectedValueInstance);
        }
    }

    @SuppressWarnings("rawtypes")
    public default void addNotPresentElementsOfList(SList listValue, SList<? extends SInstance2> defaultOptions) {
        for (SInstance2 selectedValueInstance : listValue.getAllChildren()) {
            addNotPresentElement(defaultOptions, selectedValueInstance);
        }
    }

    public default void addNotPresentElement(SList<? extends SInstance2> defaultOptions, SInstance2 selectedValueInstance) {
        if (!containsValue(defaultOptions, selectedValueInstance)) {
            addNewValueUpfront(defaultOptions, selectedValueInstance);
        }
    }

    public default boolean containsValue(SList<? extends SInstance2> defaultOptions, SInstance2 selectedValueInstance) {
        if (!Value.notNull(selectedValueInstance)) {
            return true;
        }
        for (SInstance2 c : defaultOptions.getAllChildren()) {
            if (selectedValueInstance.equals(c)) {
                return true;
            }
        }
        return false;
    }

    public default void addNewValueUpfront(SList<? extends SInstance2> defaultOptions, SInstance2 value) {
        MSelectionableInstance newValue = (MSelectionableInstance) defaultOptions.addNovoAt(0);
        MSelectionableInstance currentValue = (MSelectionableInstance) value;
        if (currentValue instanceof SIComposite) {
            SIComposite newComposto = (SIComposite) newValue;
            SIComposite currentComposto = (SIComposite) currentValue;
            currentComposto.getCampos().forEach(c ->
                            newComposto.getCampo(c.getNome()).setValor(c.getValor())
            );
        } else if (currentValue instanceof SISimple) {
            SISimple newComposto = (SISimple) newValue;
            SISimple currentComposto = (SISimple) currentValue;
            newComposto.setValor(currentComposto.getValor());
        }
    }

    /**
     * Returns the list of options for this selection.
     *
     * @param optionsInstance : Current isntance used to select the options.
     * @return list of options from the expected {@link SInstance2} type.
     */
    public SList<? extends SInstance2> listOptions(SInstance2 optionsInstance);
}
