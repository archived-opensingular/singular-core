/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.form.validation;

import org.opensingular.singular.form.SInstance;

/**
 * Validator para {@link SInstance}
 * 
 * @param <MInstancia>
 */
public interface IInstanceValidator<I extends SInstance> {
    
    void validate(IInstanceValidatable<I> validatable);
    
    /**
     * Caso este método retorne <code>true</code>, este validador só será executado caso a instância correspondente não
     * possua nenhum erro em seus descendentes. Caso retorne <code>false</code>, será executado independentemente da
     * validade de seus descendentes (campos obrigatórios poderão estar nulos neste caso).
     * @return se este validador deve ou não ser executado caso seus descendentes contenham erros. Por padrão, returna <code>true</code>. 
     */
    default boolean executeOnlyIfChildrenValid() {
        return true;
    }
}
