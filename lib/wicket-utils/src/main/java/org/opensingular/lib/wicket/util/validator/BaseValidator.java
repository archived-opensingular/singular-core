/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.lib.wicket.util.validator;

import java.util.Arrays;

import org.apache.wicket.model.IModel;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;

public interface BaseValidator<T> extends IValidator<T> {

    default ValidationError validationError(String key) {
        ValidationError error = new ValidationError();
        error.setKeys(Arrays.asList(key));
        return error;
    }

    default ValidationError validationError(String key, String var1, Object value1) {
        return validationError(key)
            .setVariable(var1, value1);
    }

    default ValidationError validationError(String key, String var1, Object value1, String var2, Object value2) {
        return validationError(key)
            .setVariable(var1, value1)
            .setVariable(var2, value2);
    }

    default ValidationError validationError(IModel<String> errorMessage) {
        return new ValidationError()
            .setMessage(errorMessage.getObject());
    }

}