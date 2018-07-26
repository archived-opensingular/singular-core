package org.opensingular.form.type.generic;

import org.opensingular.form.STypeComposite;

/**
 * Composto que obriga a implementação a sobreescrever o construtor, impedindo erros
 *
 * @param <T> o tipo da instance
 */
public abstract class STGenericComposite<T extends SIGenericComposite> extends STypeComposite<T> {
    public STGenericComposite(Class<T> genericCompositeClass) {
        super(genericCompositeClass);
    }
}