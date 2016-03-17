/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.validation;

import br.net.mirante.singular.form.mform.SInstance;

public interface IInstanceValidatable<I extends SInstance> {

    I getInstance();

    IInstanceValidatable<I> setDefaultLevel(ValidationErrorLevel level);

    ValidationErrorLevel getDefaultLevel();

    IValidationError error(IValidationError error);

    IValidationError error(String msg);

    IValidationError error(ValidationErrorLevel level, IValidationError error);

    IValidationError error(ValidationErrorLevel level, String msg);
}
