/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.form.validation.validator;

import org.opensingular.singular.form.SInstance;
import org.opensingular.singular.form.validation.IInstanceValidatable;
import org.opensingular.singular.form.validation.IInstanceValidator;

public interface IInstanceValueValidator<I extends SInstance, V> extends IInstanceValidator<I> {

    @Override
    @SuppressWarnings("unchecked")
    default void validate(IInstanceValidatable<I> validatable) {
        V value = (V) validatable.getInstance().getValue();
        if (value == null)
            return;
        validate(validatable, value);
    }

    void validate(IInstanceValidatable<I> validatable, V value);
}
