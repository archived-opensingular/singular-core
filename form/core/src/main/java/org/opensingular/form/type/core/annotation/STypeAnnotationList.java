/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.form.type.core.annotation;

import org.opensingular.form.STypeList;
import org.opensingular.form.TypeBuilder;
import org.opensingular.form.SInfoType;
import org.opensingular.form.type.core.SPackageCore;

/**
 * This type encloses a MTipoLista of MTipoAnnotation and can be used in order to persist
 * a set of anotations without much hassle.
 *
 * @author Fabricio Buzeto
 */
@SInfoType(name = STypeAnnotationList.NAME, spackage = SPackageCore.class)
public class STypeAnnotationList extends STypeList {

    public static final String NAME = "AnnotationList";

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);
        setElementsType(getDictionary().getType(STypeAnnotation.class));
    }
}
