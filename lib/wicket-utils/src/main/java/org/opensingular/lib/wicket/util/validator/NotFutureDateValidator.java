/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.lib.wicket.util.validator;

import java.time.LocalDate;

import org.apache.wicket.model.IModel;
import org.apache.wicket.validation.IValidatable;

public class NotFutureDateValidator implements BaseValidator<LocalDate> {

    private final IModel<String> errorMessageModel;

    public NotFutureDateValidator(IModel<String> errorMessageModel) {
        this.errorMessageModel = errorMessageModel;
    }

    @Override
    public void validate(IValidatable<LocalDate> validatable) {

        if (validatable.getValue().isAfter(LocalDate.now())) {
            validatable.error(validationError(errorMessageModel));
        }
    }
}
