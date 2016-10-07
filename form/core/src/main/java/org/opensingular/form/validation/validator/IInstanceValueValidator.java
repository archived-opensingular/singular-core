/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.form.validation.validator;

import org.opensingular.form.SInstance;
import org.opensingular.form.validation.IInstanceValidatable;
import org.opensingular.form.validation.IInstanceValidator;

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
