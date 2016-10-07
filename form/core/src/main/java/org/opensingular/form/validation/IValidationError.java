/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.form.validation;

import java.io.Serializable;

public interface IValidationError extends Serializable {
    String getMessage();
    ValidationErrorLevel getErrorLevel();
    Integer getInstanceId();
}
