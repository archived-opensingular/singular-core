package br.net.mirante.singular.form.mform.options;

import java.io.Serializable;
import java.util.Collection;

import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SISimple;
import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.SIList;
import br.net.mirante.singular.form.mform.STypeSimple;
import br.net.mirante.singular.form.mform.util.transformer.Value;

/**
 * This interface represents the providers which will load options that
 * populate the choices for a specific field. The provider is specified
 * during the declaration of a Type or Field by using the
 * {@link STypeSimple#withSelectionFromProvider(String)} method.
 */
public interface SOptionsProvider extends Serializable {

    public default String toDebug() {
        return null;
    }

    /**
     * Returns the list of options available for this selection, considering
     * also the old state of it.
     *
     * @param selectedValueInstance : Current isntance used to select the options.
     * @return list of options from the expected {@link SInstance} type.
     */
    default public SIList<? extends SInstance> listAvailableOptions(SSelectionableInstance selectedValueInstance) {
        SIList<? extends SInstance> defaultOptions = listOptions((SInstance) selectedValueInstance);
        checkForDanglingValues((SInstance) selectedValueInstance, defaultOptions);
        return defaultOptions;
    }

    public default void checkForDanglingValues(SInstance selectedValueInstance, SIList<? extends SInstance> defaultOptions) {
        Object value = selectedValueInstance.getValue();
        if (value == null) return;
        if (value instanceof Collection && ((Collection<?>) value).isEmpty()) return;
        addNotPresentElement(selectedValueInstance, defaultOptions);
    }

    public default void addNotPresentElement(SInstance selectedValueInstance, SIList<? extends SInstance> defaultOptions) {
        if (selectedValueInstance instanceof SIList) {
            addNotPresentElementsOfList((SIList) selectedValueInstance, defaultOptions);
        } else {
            addNotPresentElement(defaultOptions, selectedValueInstance);
        }
    }

    @SuppressWarnings("rawtypes")
    public default void addNotPresentElementsOfList(SIList<?> listValue, SIList<? extends SInstance> defaultOptions) {
        for (SInstance selectedValueInstance : listValue.getAllChildren()) {
            addNotPresentElement(defaultOptions, selectedValueInstance);
        }
    }

    public default void addNotPresentElement(SIList<? extends SInstance> defaultOptions, SInstance selectedValueInstance) {
        if (!containsValue(defaultOptions, selectedValueInstance)) {
            addNewValueUpfront(defaultOptions, selectedValueInstance);
        }
    }

    public default boolean containsValue(SIList<? extends SInstance> defaultOptions, SInstance selectedValueInstance) {
        if (!Value.notNull(selectedValueInstance)) {
            return true;
        }
        for (SInstance c : defaultOptions.getAllChildren()) {
            if (selectedValueInstance.equals(c)) {
                return true;
            }
        }
        return false;
    }

    public default void addNewValueUpfront(SIList<? extends SInstance> defaultOptions, SInstance value) {
        SSelectionableInstance newValue = (SSelectionableInstance) defaultOptions.addNewAt(0);
        SSelectionableInstance currentValue = (SSelectionableInstance) value;
        if (currentValue instanceof SIComposite) {
            SIComposite newComposto = (SIComposite) newValue;
            SIComposite currentComposto = (SIComposite) currentValue;
            currentComposto.getFields().forEach(c ->
                            newComposto.getField(c.getName()).setValue(c.getValue())
            );
        } else if (currentValue instanceof SISimple) {
            SISimple newComposto = (SISimple) newValue;
            SISimple currentComposto = (SISimple) currentValue;
            newComposto.setValue(currentComposto.getValue());
        }
    }

    /**
     * Returns the list of options for this selection.
     *
     * @param optionsInstance : Current isntance used to select the options.
     * @return list of options from the expected {@link SInstance} type.
     */
    public SIList<? extends SInstance> listOptions(SInstance optionsInstance);
}
