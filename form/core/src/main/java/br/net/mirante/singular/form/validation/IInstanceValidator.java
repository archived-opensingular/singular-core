/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.validation;

import br.net.mirante.singular.form.SInstance;

/**
 * Validator para {@link SInstance}
 * 
 * @param <MInstancia>
 */
public interface IInstanceValidator<I extends SInstance> {
    
    void validate(IInstanceValidatable<I> validatable);
}
