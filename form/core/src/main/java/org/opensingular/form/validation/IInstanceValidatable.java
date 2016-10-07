/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.form.validation;

import org.opensingular.form.SInstance;

public interface IInstanceValidatable<I extends SInstance> {

    I getInstance();

    IInstanceValidatable<I> setDefaultLevel(ValidationErrorLevel level);

    ValidationErrorLevel getDefaultLevel();

    IValidationError error(IValidationError error);

    IValidationError error(String msg);

    IValidationError error(ValidationErrorLevel level, IValidationError error);

    IValidationError error(ValidationErrorLevel level, String msg);
}
