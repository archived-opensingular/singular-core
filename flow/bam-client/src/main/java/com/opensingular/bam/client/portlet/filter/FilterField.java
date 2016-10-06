/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.opensingular.bam.client.portlet.filter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.apache.commons.lang3.StringUtils;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface FilterField {

    FieldType type() default FieldType.DEFAULT;

    String label();

    FieldSize size() default FieldSize.LARGE;

    String[] options() default StringUtils.EMPTY;

    RestOptionsProvider optionsProvider() default @RestOptionsProvider();

    boolean required() default false;

}
