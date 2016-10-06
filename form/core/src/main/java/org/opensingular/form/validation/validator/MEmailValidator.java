/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.form.validation.validator;

import org.opensingular.form.SingularFormException;
import org.opensingular.form.type.core.SIString;
import org.opensingular.form.validation.IInstanceValidatable;
import org.opensingular.form.validation.SingularEmailValidator;

public enum MEmailValidator implements IInstanceValueValidator<SIString, String>  {

    /**
     * Local address is considered invalid
     */
    INSTANCE(false),
    /**
     * Local address is considered valid
     */
    INSTANCE_ALLOW_LOCAL_ADDRESS(true),
    ;
    
    private final boolean allowLocal;
    
    /**
     * @param allowLocal Should local addresses be considered valid?
     */
    private MEmailValidator(boolean allowLocal) {
        this.allowLocal = allowLocal;
    }

    @Override
    public void validate(IInstanceValidatable<SIString> validatable, String value) {
        try {
            boolean isValid = SingularEmailValidator.getInstance(allowLocal).isValid(value);
            if(!isValid){
                validatable.error("E-mail inválido");
            }
        } catch (SingularFormException e){
            validatable.error(e.getMessage());
        }
    }
}
