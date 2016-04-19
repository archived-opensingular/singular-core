/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.mform.options;

import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SIList;
import br.net.mirante.singular.form.mform.SISimple;
import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.STypeList;
import br.net.mirante.singular.form.mform.STypeSimple;
import br.net.mirante.singular.form.mform.SingularFormException;
import br.net.mirante.singular.form.mform.util.transformer.Value;

import java.io.Serializable;
import java.util.Collection;

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

    /*** Returns the list of options available for this selection, considering
     * also the old state of it.
     *
     * @param selectedValueInstance : Current isntance used to select the options.
     * @param filter
     * @return list of options from the expected {@link SInstance} type.
     */
    public default SIList<? extends SInstance> listAvailableOptions(SSelectionableInstance selectedValueInstance, String filter) {
        SIList<? extends SInstance> defaultOptions = listOptions((SInstance) selectedValueInstance, filter);
        checkForDanglingValues((SInstance) selectedValueInstance, defaultOptions);
        return verifyOptionsType((SInstance) selectedValueInstance, defaultOptions);
    }

    public default SIList<? extends SInstance> verifyOptionsType(SInstance targetInstance, SIList<? extends SInstance> options) {
        SType<?> targetType;
        if (targetInstance.getType() instanceof STypeList) {
            targetType = ((STypeList) targetInstance.getType()).getElementsType();
        } else {
            targetType = targetInstance.getType();
        }
        if (options != null && targetType != null) {
            options.stream().forEach(sInstance -> {
                if (sInstance != null && !sInstance.getType().equals(targetType)) {
                    throw new SingularFormException(
                            String
                                    .format(" As opções fornecidas tem que ser do mesmo tipo da instancia alvo. Tipo fornecido: %s, Tipo esperado: %s",
                                            sInstance.getType().getName(),
                                            targetType.getName()));
                }
            });
        }
        return options;
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
     * @param optionsInstance : Current instance used to select the options.
     * @param filter : optional filter for narrowing options available
     * @return list of options from the expected {@link SInstance} type.
     */
    public SIList<? extends SInstance> listOptions(SInstance optionsInstance, String filter);
}
