package br.net.mirante.singular.form.mform.options;

import br.net.mirante.singular.form.mform.MIComposto;
import br.net.mirante.singular.form.mform.MILista;
import br.net.mirante.singular.form.mform.MISimples;
import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.MTipoSimples;
import br.net.mirante.singular.form.mform.util.transformer.Val;

import java.io.Serializable;
import java.util.Collection;

/**
 * This interface represents the providers which will load options that
 * populate the choices for a specific field. The provider is specified
 * during the declaration of a Type or Field by using the
 * {@link MTipoSimples#withSelectionFromProvider(String)} method.
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
     * @return list of options from the expected {@link MInstancia} type.
     */
    default public MILista<? extends MInstancia> listAvailableOptions(MSelectionableInstance selectedValueInstance) {
        MILista<? extends MInstancia> defaultOptions = listOptions((MInstancia) selectedValueInstance);
        checkForDanglingValues((MInstancia) selectedValueInstance, defaultOptions);
        return defaultOptions;
    }

    public default void checkForDanglingValues(MInstancia selectedValueInstance, MILista<? extends MInstancia> defaultOptions) {
        Object value = selectedValueInstance.getValor();
        if (value == null) return;
        if (value instanceof Collection && ((Collection<?>) value).isEmpty()) return;
        addNotPresentElement(selectedValueInstance, defaultOptions);
    }

    public default void addNotPresentElement(MInstancia selectedValueInstance, MILista<? extends MInstancia> defaultOptions) {
        if (selectedValueInstance instanceof MILista) {
            addNotPresentElementsOfList((MILista) selectedValueInstance, defaultOptions);
        } else {
            addNotPresentElement(defaultOptions, selectedValueInstance);
        }
    }

    @SuppressWarnings("rawtypes")
    public default void addNotPresentElementsOfList(MILista listValue, MILista<? extends MInstancia> defaultOptions) {
        for (MInstancia selectedValueInstance : listValue.getAllChildren()) {
            addNotPresentElement(defaultOptions, selectedValueInstance);
        }
    }

    public default void addNotPresentElement(MILista<? extends MInstancia> defaultOptions, MInstancia selectedValueInstance) {
        if (!containsValue(defaultOptions, selectedValueInstance)) {
            addNewValueUpfront(defaultOptions, selectedValueInstance);
        }
    }

    public default boolean containsValue(MILista<? extends MInstancia> defaultOptions, MInstancia selectedValueInstance) {
        if (!Val.notNull(selectedValueInstance)) {
            return true;
        }
        for (MInstancia c : defaultOptions.getAllChildren()) {
            if (selectedValueInstance.equals(c)) {
                return true;
            }
        }
        return false;
    }

    public default void addNewValueUpfront(MILista<? extends MInstancia> defaultOptions, MInstancia value) {
        MSelectionableInstance newValue = (MSelectionableInstance) defaultOptions.addNovoAt(0);
        MSelectionableInstance currentValue = (MSelectionableInstance) value;
        if (currentValue instanceof MIComposto) {
            MIComposto newComposto = (MIComposto) newValue;
            MIComposto currentComposto = (MIComposto) currentValue;
            currentComposto.getCampos().forEach(c ->
                            newComposto.getCampo(c.getNome()).setValor(c.getValor())
            );
        } else if (currentValue instanceof MISimples) {
            MISimples newComposto = (MISimples) newValue;
            MISimples currentComposto = (MISimples) currentValue;
            newComposto.setValor(currentComposto.getValor());
        }
    }

    /**
     * Returns the list of options for this selection.
     *
     * @param optionsInstance : Current isntance used to select the options.
     * @return list of options from the expected {@link MInstancia} type.
     */
    public MILista<? extends MInstancia> listOptions(MInstancia optionsInstance);
}
