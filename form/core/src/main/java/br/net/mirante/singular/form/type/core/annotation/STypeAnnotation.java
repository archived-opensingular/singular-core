/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.type.core.annotation;

import br.net.mirante.singular.form.SInfoType;
import br.net.mirante.singular.form.STypeComposite;
import br.net.mirante.singular.form.TypeBuilder;
import br.net.mirante.singular.form.type.core.SPackageCore;

/**
 * This type represents an Annotation of a field.
 * For now only composite fields can be anotated but this type does not enforce such rule.
 *
 * @author Fabricio Buzeto
 */
@SInfoType(name = "Annotation", spackage = SPackageCore.class)
public class STypeAnnotation extends STypeComposite<SIAnnotation> {

    public static final String          FIELD_TEXT          = "text",
                                        FIELD_TARGET_ID     = "targetId",
                                        FIELD_APPROVED      = "isApproved",
                                        FIELD_CLASSIFIER     = "classifier"
                                        ;

    public STypeAnnotation() {
        super(SIAnnotation.class);
    }

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);

        addFieldString(FIELD_TEXT);
        addFieldString(FIELD_CLASSIFIER);
        addFieldBoolean(FIELD_APPROVED);
        addFieldInteger(FIELD_TARGET_ID);
    }

    @Override
    public <T extends Object> T convert(Object valor, Class<T> classeDestino) {
        return (T) valor;
    }
}
