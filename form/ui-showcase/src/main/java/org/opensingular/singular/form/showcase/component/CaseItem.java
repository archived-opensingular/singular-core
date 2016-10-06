/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.form.showcase.component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.opensingular.form.wicket.enums.AnnotationMode;

@Inherited
@Retention(value = RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CaseItem {

    String componentName();
    Group group();
    String subCaseName() default "";
    Resource[] resources() default {};
    AnnotationMode annotation() default AnnotationMode.NONE;
    Class<? extends CaseCustomizer> customizer() default CaseCustomizer.class;
}
