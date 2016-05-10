/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.validation.validator;

import br.net.mirante.singular.form.SIComposite;
import br.net.mirante.singular.form.STypeSimple;
import br.net.mirante.singular.form.validation.IInstanceValidatable;
import br.net.mirante.singular.form.validation.IInstanceValidator;

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