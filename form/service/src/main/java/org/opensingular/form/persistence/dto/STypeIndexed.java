package org.opensingular.form.persistence.dto;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface STypeIndexed {

    boolean indexedColumn() default false;
    boolean returnColumn() default true;
    String[] path() default "";
}
