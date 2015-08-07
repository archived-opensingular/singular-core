package br.net.mirante.singular.ui.mform;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface MFormTipo {

    public String nome();

    public Class<? extends MPacote> pacote();
}
