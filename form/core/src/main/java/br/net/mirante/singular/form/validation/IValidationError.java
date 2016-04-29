/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.validation;

import java.io.Serializable;

public interface IValidationError extends Serializable {
    String getMessage();
    ValidationErrorLevel getErrorLevel();
    Integer getInstanceId();
}
