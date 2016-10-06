/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.form.validation.validator;

import org.opensingular.singular.form.SIComposite;
import org.opensingular.singular.form.STypeSimple;
import org.opensingular.singular.form.validation.IInstanceValidatable;
import org.opensingular.singular.form.validation.IInstanceValidator;

import java.util.Set;

import static java.util.stream.Collectors.toSet;

public enum AllOrNothingInstanceValidator implements IInstanceValidator<SIComposite> {
    INSTANCE;
    @Override
    public void validate(IInstanceValidatable<SIComposite> v) {
        Set<Boolean> nullValues = v.getInstance().streamDescendants(false)
            .filter(it -> it.getType() instanceof STypeSimple<?, ?>)
            .map(it -> it.getValue() == null)
            .collect(toSet());
        
        // os campos devem ser todos nulos ou todos preenchidos
        if (nullValues.size() != 1)
            v.error("Endereço incompleto");
    }
}