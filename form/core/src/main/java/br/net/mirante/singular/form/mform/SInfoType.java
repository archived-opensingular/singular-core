package br.net.mirante.singular.form.mform;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SInfoType {

    public String name();

    public Class<? extends SPackage> spackage();
}
