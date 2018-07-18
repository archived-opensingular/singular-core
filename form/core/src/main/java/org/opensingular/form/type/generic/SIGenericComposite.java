package org.opensingular.form.type.generic;

import org.opensingular.form.SIComposite;

/**
 * Um SIComposite que permite o uso tipado do seu SType
 *
 * @param <T>
 */
public class SIGenericComposite<T extends STGenericComposite<?>> extends SIComposite {
    @Override
    public T getType() {
        return (T) super.getType();
    }
}