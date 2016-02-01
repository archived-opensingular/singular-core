package br.net.mirante.singular.form.mform;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface MInfoTipo {

    public String nome();

    public Class<? extends SPackage> pacote();
}
